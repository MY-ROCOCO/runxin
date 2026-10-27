package com.rxinyun.admin.order.modular.ecs.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.mapper.OrderMapper;
import com.rxinyun.admin.order.modular.ecs.param.ServerRefundParam;
import com.rxinyun.admin.order.modular.ecs.refund.ServerRefundCalculator;
import com.rxinyun.admin.order.modular.ecs.response.IndependentOrderServerConfigResponse;
import com.rxinyun.admin.order.modular.ecs.response.OrderServerConfigResponse;
import com.rxinyun.admin.order.modular.ecs.response.ServerRefundPreviewResponse;
import com.rxinyun.admin.order.modular.ecs.service.IndependentOrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.ecs.service.ServerRefundService;
import com.rxinyun.admin.order.modular.invoice.service.OrderInvoiceService;
import com.rxinyun.admin.order.modular.vhost.entity.AdminVhostOrderConfig;
import com.rxinyun.admin.order.modular.vhost.mapper.AdminVhostOrderConfigMapper;
import com.rxinyun.agent.modular.service.AgentCommissionService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseIndependentIp;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.enums.order.ServerRefundModeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.ResourcePoolRefundApi;
import com.rxinyun.marketing.api.MemberCouponApi;
import com.rxinyun.member.admin.api.BalanceApi;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class ServerRefundServiceImpl implements ServerRefundService {
   private static final Logger log = LoggerFactory.getLogger(ServerRefundServiceImpl.class);
   @Resource
   private OrderMapper orderMapper;
   @Resource
   private OrderService orderService;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private OrderServerConfigService orderServerConfigService;
   @Resource
   private IndependentOrderServerConfigService independentOrderServerConfigService;
   @Resource
   private ServerApi serverApi;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private MemberVhostApi memberVhostApi;
   @Resource
   private AdminVhostOrderConfigMapper adminVhostOrderConfigMapper;
   @Resource
   private BalanceApi balanceApi;
   @Resource
   private ResourcePoolRefundApi resourcePoolRefundApi;
   @Resource
   private MemberCouponApi memberCouponApi;
   @Resource
   private AgentCommissionService agentCommissionService;
   @Resource
   private OrderInvoiceService orderInvoiceService;
   @Resource(
      name = "refundTaskExecutor"
   )
   private Executor refundTaskExecutor;

   @Override
   public CommonResult<ServerRefundPreviewResponse> preview(ServerRefundParam param) {
      try {
         return CommonResult.data(this.buildPreview(param));
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> execute(ServerRefundParam param) {
      try {
         String serverId = param.getServerId();
         ServerRefundModeEnum mode = ServerRefundModeEnum.fromParam(param.getRefundMode());
         int productType = this.resolveProductType(serverId, param.getProductType());
         boolean resourcePoolServer = this.isResourcePoolServer(serverId, productType);
         Long memberNo = this.resolveRefundMemberNo(serverId, productType, param.getMemberNo());
         List<Order> orders = this.loadDistinctPaidOrders(serverId, productType, memberNo);
         if (orders.isEmpty()) {
            return CommonResult.error("没有可退款的已支付订单（云/独立/虚拟主机关联）");
         } else {
            this.orderInvoiceService.assertOrdersNotOccupied(orders.stream().map(Order::getOrderNo).toList());
            List<ServerRefundServiceImpl.OrderRefundContext> refundContexts = this.buildOrderRefundContexts(orders, productType);
            ServerRefundPreviewResponse pv = this.buildPreview(param);
            if (pv.getRefundAmount() != null && pv.getSumPayPrice() != null) {
               if (pv.getSumPayPrice().compareTo(BigDecimal.ZERO) <= 0) {
                  return CommonResult.error("没有可退的实付金额");
               } else if (mode == ServerRefundModeEnum.PRORATED && pv.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
                  return CommonResult.error("按天退款时剩余可退金额为 0，已到期或无可退时长");
               } else if (pv.getSumPayPrice() != null && pv.getSumPayPrice().compareTo(BigDecimal.ZERO) > 0) {
                  String batchNo = "SRF-" + param.getServerId() + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
                  Date now = new Date();
                  int updatedCount = 0;
                  int finalizableOrderCount = 0;

                  for (ServerRefundServiceImpl.OrderRefundContext ctx : refundContexts) {
                     if (ctx.shouldFinalizeOrderStatus()) {
                        finalizableOrderCount++;
                        Order o = ctx.getOrder();
                        LambdaUpdateWrapper<Order> u = new LambdaUpdateWrapper();
                        ((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)u.eq(Order::getOrderNo, o.getOrderNo()))
                                 .eq(Order::getOrderStatus, OrderStatusEnum.PAID.getCode()))
                              .set(Order::getOrderStatus, OrderStatusEnum.REFUNDING.getCode()))
                           .set(Order::getUpdateTime, now);
                        if (this.orderService.update(u)) {
                           updatedCount++;
                        }
                     }
                  }

                  if (finalizableOrderCount > 0 && updatedCount <= 0) {
                     return CommonResult.error("订单已被处理，请刷新后重试");
                  } else {
                     this.scheduleRefundProcessAfterCommit(batchNo, param, pv, orders, resourcePoolServer, memberNo);
                     log.info("服务器退款已受理 serverId={} batchNo={} amount={}", new Object[]{param.getServerId(), batchNo, pv.getRefundAmount()});
                     return CommonResult.data(batchNo);
                  }
               } else {
                  return CommonResult.error("没有可退的实付金额");
               }
            } else {
               return CommonResult.error("试算结果异常");
            }
         }
      } catch (CommonException var18) {
         return CommonResult.error(var18.getMessage());
      } catch (Exception var19) {
         log.error("服务器退款执行失败 serverId={}", param.getServerId(), var19);
         return CommonResult.error(var19.getMessage() != null ? var19.getMessage() : "退款执行失败");
      }
   }

   private void scheduleRefundProcessAfterCommit(
      String batchNo, ServerRefundParam param, ServerRefundPreviewResponse pv, List<Order> orders, boolean resourcePoolServer, Long memberNo
   ) {
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
         TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            public void afterCommit() {
               ServerRefundServiceImpl.this.runRefundProcessAsync(batchNo, param, pv, orders, resourcePoolServer, memberNo);
            }
         });
      } else {
         this.runRefundProcessAsync(batchNo, param, pv, orders, resourcePoolServer, memberNo);
      }
   }

   private void runRefundProcessAsync(
      String batchNo, ServerRefundParam param, ServerRefundPreviewResponse pv, List<Order> orders, boolean resourcePoolServer, Long memberNo
   ) {
      this.refundTaskExecutor.execute(() -> {
         try {
            this.processRefund(batchNo, param, pv, orders, resourcePoolServer, memberNo);
         } catch (Exception var8) {
            log.error("后台处理退款失败 batchNo={} serverId={} err={}", new Object[]{batchNo, param.getServerId(), var8.getMessage(), var8});
         }
      });
   }

   private void processRefund(
      String batchNo, ServerRefundParam param, ServerRefundPreviewResponse pv, List<Order> orders, boolean resourcePoolServer, Long memberNo
   ) {
      String serverId = param.getServerId();
      int productTypeResolved = pv.getProductTypeResolved();
      ServerRefundModeEnum mode = ServerRefundModeEnum.fromParam(param.getRefundMode());
      List<String> orderNos = orders.stream().map(Order::getOrderNo).distinct().toList();
      List<ServerRefundServiceImpl.OrderRefundContext> refundContexts = this.buildOrderRefundContexts(orders, productTypeResolved);
      if (resourcePoolServer) {
         String poolId = this.resolveResourcePoolId(serverId, productTypeResolved);
         String poolBody = this.buildPoolRefundRequestJson(param, orders, productTypeResolved);
         CommonResult<String> execRes = this.resourcePoolRefundApi.refundExecute(poolId, productTypeResolved, poolBody);
         if (execRes.getCode() != 200) {
            throw new CommonException(StrUtil.blankToDefault(execRes.getMsg(), "资源池退款执行调用失败"), new Object[0]);
         }

         this.assertUpstreamPoolBusinessOk((String)execRes.getData());
      }

      Date now = new Date();
      Map<String, Object> biz = new LinkedHashMap<>();
      biz.put("serverId", serverId);
      biz.put("refundMode", mode.name());
      biz.put("orderNos", orderNos);
      biz.put("reason", param.getReason());
      String publicIp = this.resolvePublicIpForRefundRemark(serverId, productTypeResolved);
      if (StrUtil.isNotBlank(publicIp)) {
         biz.put("publicIp", publicIp);
      }

      String refundBizType = this.resolveRefundBizType(productTypeResolved);
      String remark = StrUtil.isBlank(publicIp)
         ? refundBizType + "入账"
         : (ProductTypeEnum.VHOST.getCode().equals(productTypeResolved) ? refundBizType + "入账（" + publicIp + "）" : refundBizType + "入账（公网IP：" + publicIp + "）");
      this.balanceApi.creditAndRecord(memberNo, pv.getRefundAmount(), batchNo, refundBizType, JSONUtil.toJsonStr(biz), now, remark);
      if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productTypeResolved)) {
         if (resourcePoolServer) {
            this.serverApi.finalizeRefundCloudIpBindings(serverId);
         } else {
            this.serverApi.deleteServer(serverId);
         }
      } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productTypeResolved)) {
         this.independentApi.deleteServer(serverId);
      } else {
         this.memberVhostApi.refundDelete(serverId);
      }

      for (String linkSid : this.refundOrderLinkServerIds(serverId, productTypeResolved)) {
         this.orderServerService.removeByServerId(linkSid);
      }

      for (ServerRefundServiceImpl.OrderRefundContext ctx : refundContexts) {
         if (!ctx.shouldFinalizeOrderStatus()) {
            log.info(
               "服务器退款为部分退款，保留原订单状态 orderNo={} totalServerCount={} remainingServerCountBeforeRefund={}",
               new Object[]{ctx.getOrder().getOrderNo(), ctx.getTotalServerCount(), ctx.getRemainingServerCountBeforeRefund()}
            );
         } else {
            String orderNo = ctx.getOrder().getOrderNo();
            this.memberCouponApi.rollbackCoupon(orderNo);
            LambdaUpdateWrapper<Order> u = new LambdaUpdateWrapper();
            ((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)((LambdaUpdateWrapper<Order>)u.eq(Order::getOrderNo, orderNo))
                     .eq(Order::getOrderStatus, OrderStatusEnum.REFUNDING.getCode()))
                  .set(Order::getOrderStatus, OrderStatusEnum.REFUNDED.getCode()))
               .set(Order::getUpdateTime, now);
            this.orderService.update(u);
         }
      }

      this.agentCommissionService.deductCommissionByRefund(batchNo, orderNos, pv.getRefundAmount(), remark);
      log.info("服务器退款完成 serverId={} batchNo={} amount={}", new Object[]{serverId, batchNo, pv.getRefundAmount()});
   }

   private ServerRefundPreviewResponse buildPreview(ServerRefundParam param) {
      String serverId = param.getServerId();
      ServerRefundModeEnum mode = ServerRefundModeEnum.fromParam(param.getRefundMode());
      int productType = this.resolveProductType(serverId, param.getProductType());
      Long memberNo = this.resolveRefundMemberNo(serverId, productType, param.getMemberNo());
      List<Order> orders = this.loadDistinctPaidOrders(serverId, productType, memberNo);
      if (orders.isEmpty()) {
         throw new CommonException("没有可退款的已支付订单（云/独立/虚拟主机关联）", new Object[0]);
      } else {
         List<ServerRefundServiceImpl.OrderRefundContext> refundContexts = this.buildOrderRefundContexts(orders, productType);
         BigDecimal s = refundContexts.stream()
            .map(ServerRefundServiceImpl.OrderRefundContext::getRefundBaseAmount)
            .filter(p -> p != null && p.compareTo(BigDecimal.ZERO) > 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
         Date buyTime;
         Date endTime;
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
            BaseServer cloud = this.serverApi.getById(serverId);
            buyTime = cloud.getBuyTime();
            endTime = cloud.getEndTime();
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
            BaseIndependent indep = this.independentApi.getById(serverId);
            buyTime = indep.getBuyTime();
            endTime = indep.getEndTime();
         } else {
            BaseMemberVhost vhost = this.memberVhostApi.selectById(serverId);
            buyTime = vhost == null ? null : vhost.getCreateTime();
            endTime = vhost == null ? null : vhost.getEndTime();
         }

         if (endTime == null) {
            throw new CommonException("实例缺少到期时间，无法计价", new Object[0]);
         } else {
            if (buyTime == null) {
               buyTime = orders.stream().map(Order::getPayTime).filter(d -> d != null).min(Comparator.naturalOrder()).orElse(null);
            }

            if (buyTime == null) {
               throw new CommonException("缺少开通时间或订单支付时间，无法计算服务总时长", new Object[0]);
            } else {
               Date now = new Date();
               long totalMs = endTime.getTime() - buyTime.getTime();
               long leftMs = endTime.getTime() - now.getTime();
               BigDecimal refund = ServerRefundCalculator.computeRefundAmount(s, mode, totalMs, leftMs);
               ServerRefundPreviewResponse vo = new ServerRefundPreviewResponse();
               vo.setRefundAmount(refund);
               vo.setRefundMode(mode.name());
               vo.setSumPayPrice(s.setScale(2, RoundingMode.HALF_UP));
               vo.setTotalMillis(totalMs);
               vo.setLeftMillis(leftMs);
               vo.setBuyTime(buyTime);
               vo.setEndTime(endTime);
               vo.setNow(now);
               vo.setOrderCount(refundContexts.size());
               vo.setOrderNos(
                  refundContexts.stream()
                     .map(ServerRefundServiceImpl.OrderRefundContext::getOrder)
                     .map(Order::getOrderNo)
                     .distinct()
                     .collect(Collectors.toCollection(ArrayList::new))
               );
               vo.setProductTypeResolved(productType);
               if (mode == ServerRefundModeEnum.PRORATED && leftMs <= 0L) {
                  vo.setHint("已到期或剩余时长为 0，按天退款金额为 0");
               }

               return vo;
            }
         }
      }
   }

   private List<String> refundOrderLinkServerIds(String serverId, int productType) {
      return StrUtil.isBlank(serverId) ? new ArrayList<>() : new ArrayList<>(List.of(serverId.trim()));
   }

   private List<Order> loadDistinctPaidOrders(String serverId, int productType, Long memberNo) {
      List<String> linkIds = this.refundOrderLinkServerIds(serverId, productType);
      if (linkIds.isEmpty()) {
         return new ArrayList<>();
      } else {
         List<Order> list = this.orderMapper.selectPaidRefundableOrdersByServerIds(linkIds, memberNo);
         Map<String, Order> byNo = new LinkedHashMap<>();

         for (Order o : list) {
            if (o.getOrderNo() != null) {
               byNo.putIfAbsent(o.getOrderNo(), o);
            }
         }

         return new ArrayList<>(byNo.values());
      }
   }

   private List<ServerRefundServiceImpl.OrderRefundContext> buildOrderRefundContexts(List<Order> orders, int productType) {
      List<ServerRefundServiceImpl.OrderRefundContext> contexts = new ArrayList<>();

      for (Order order : orders) {
         if (order != null && !StrUtil.isBlank(order.getOrderNo())) {
            int remainingServerCountBeforeRefund = this.resolveRemainingServerCount(order, productType);
            int totalServerCount = this.resolveOrderServerCount(order, productType, remainingServerCountBeforeRefund);
            BigDecimal refundBaseAmount = this.resolveRefundBaseAmount(order.getPayPrice(), totalServerCount, remainingServerCountBeforeRefund);
            contexts.add(new ServerRefundServiceImpl.OrderRefundContext(order, refundBaseAmount, totalServerCount, remainingServerCountBeforeRefund));
         }
      }

      return contexts;
   }

   private int resolveOrderServerCount(Order order, int productType, int remainingServerCountBeforeRefund) {
      Integer configuredServerCount = this.resolveConfiguredServerCount(order, productType);
      return configuredServerCount != null && configuredServerCount > 0 ? configuredServerCount : Math.max(remainingServerCountBeforeRefund, 1);
   }

   private Integer resolveConfiguredServerCount(Order order, int productType) {
      if (order != null && !StrUtil.isBlank(order.getOrderNo())) {
         if (!Objects.equals(order.getOrderType(), OrderTypeEnum.NEW_PURCHASE.getCode())) {
            return 1;
         } else if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
            OrderServerConfigResponse config = this.orderServerConfigService.queryByOrderNo(order.getOrderNo());
            return config != null && config.getNumber() != null && config.getNumber() > 0 ? config.getNumber() : null;
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
            IndependentOrderServerConfigResponse config = this.independentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
            return config != null && config.getNumber() != null && config.getNumber() > 0 ? config.getNumber() : null;
         } else {
            AdminVhostOrderConfig config = (AdminVhostOrderConfig)this.adminVhostOrderConfigMapper
               .selectOne((Wrapper<AdminVhostOrderConfig>)((LambdaQueryWrapper<AdminVhostOrderConfig>)new LambdaQueryWrapper<AdminVhostOrderConfig>().eq(AdminVhostOrderConfig::getOrderNo, order.getOrderNo())).last("limit 1"));
            return config != null && config.getNumber() != null && config.getNumber() > 0 ? config.getNumber() : null;
         }
      } else {
         return null;
      }
   }

   private int resolveRemainingServerCount(Order order, int productType) {
      if (ProductTypeEnum.VHOST.getCode().equals(productType)) {
         List<BaseMemberVhost> hosts = this.memberVhostApi.listByOrderNo(order.getOrderNo());
         int remaining = 0;
         if (hosts != null) {
            for (BaseMemberVhost host : hosts) {
               if (host != null && !BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
                  remaining++;
               }
            }
         }

         return Math.max(remaining, 1);
      } else {
         List<OrderServer> linkedServers = this.orderServerService.queryByList(order.getOrderNo());
         return linkedServers != null && !linkedServers.isEmpty() ? linkedServers.size() : 1;
      }
   }

   private BigDecimal resolveRefundBaseAmount(BigDecimal orderPayPrice, int totalServerCount, int remainingServerCountBeforeRefund) {
      BigDecimal payPrice = orderPayPrice == null ? BigDecimal.ZERO : orderPayPrice;
      if (payPrice.compareTo(BigDecimal.ZERO) <= 0) {
         return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
      } else {
         int normalizedTotal = Math.max(totalServerCount, 1);
         int normalizedRemaining = Math.max(remainingServerCountBeforeRefund, 1);
         if (normalizedTotal <= 1) {
            return payPrice.setScale(2, RoundingMode.HALF_UP);
         } else {
            BigDecimal avg = payPrice.divide(BigDecimal.valueOf((long)normalizedTotal), 2, RoundingMode.HALF_UP);
            if (normalizedRemaining <= 1) {
               BigDecimal refundedBefore = avg.multiply(BigDecimal.valueOf((long)(normalizedTotal - 1)));
               return payPrice.subtract(refundedBefore).setScale(2, RoundingMode.HALF_UP);
            } else {
               return avg;
            }
         }
      }
   }

   private Long resolveRefundMemberNo(String serverId, int productType, Long paramMemberNo) {
      Long actualMemberNo = this.resolveMemberNo(serverId, productType);
      if (paramMemberNo == null) {
         return actualMemberNo;
      } else if (!paramMemberNo.equals(actualMemberNo)) {
         throw new CommonException("会员编号与服务器归属不匹配", new Object[0]);
      } else {
         return actualMemberNo;
      }
   }

   private int resolveProductType(String serverId, Integer productType) {
      if (productType != null) {
         if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
            if (this.serverApi.getById(serverId) == null) {
               throw new CommonException("未找到云服务器: " + serverId, new Object[0]);
            } else {
               return ProductTypeEnum.CLOUD_SERVER.getCode();
            }
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
            if (this.independentApi.getById(serverId) == null) {
               throw new CommonException("未找到独立服务器: " + serverId, new Object[0]);
            } else {
               return ProductTypeEnum.INDEPENDENT_SERVER.getCode();
            }
         } else if (ProductTypeEnum.VHOST.getCode().equals(productType)) {
            if (this.memberVhostApi.selectById(serverId) == null) {
               throw new CommonException("未找到虚拟主机: " + serverId, new Object[0]);
            } else {
               return ProductTypeEnum.VHOST.getCode();
            }
         } else {
            throw new CommonException("不支持的 productType: " + productType, new Object[0]);
         }
      } else {
         BaseServer cloud = this.serverApi.getById(serverId);
         if (cloud != null) {
            return ProductTypeEnum.CLOUD_SERVER.getCode();
         } else {
            BaseIndependent indep = this.independentApi.getById(serverId);
            if (indep != null) {
               return ProductTypeEnum.INDEPENDENT_SERVER.getCode();
            } else {
               BaseMemberVhost vhost = this.memberVhostApi.selectById(serverId);
               if (vhost != null) {
                  return ProductTypeEnum.VHOST.getCode();
               } else {
                  throw new CommonException("未找到服务器，请检查 serverId 或传入 productType", new Object[0]);
               }
            }
         }
      }
   }

   private boolean isResourcePoolServer(String serverId, int productType) {
      if (ProductTypeEnum.VHOST.getCode().equals(productType)) {
         return false;
      } else if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
         BaseServer cloud = this.serverApi.getById(serverId);
         return cloud != null
            && (Integer.valueOf(1).equals(cloud.getType()) || StrUtil.isNotBlank(cloud.getResourcePoolId()) || StrUtil.isNotBlank(cloud.getUpstreamServerId()));
      } else {
         BaseIndependent indep = this.independentApi.getById(serverId);
         return indep != null
            && (Integer.valueOf(1).equals(indep.getType()) || StrUtil.isNotBlank(indep.getResourcePoolId()) || StrUtil.isNotBlank(indep.getUpstreamServerId()));
      }
   }

   private String resolveResourcePoolId(String serverId, int productType) {
      if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
         BaseServer cloud = this.serverApi.getById(serverId);
         if (cloud == null) {
            throw new CommonException("未找到云服务器: " + serverId, new Object[0]);
         } else if (StrUtil.isNotBlank(cloud.getResourcePoolId())) {
            return cloud.getResourcePoolId().trim();
         } else {
            throw new CommonException("资源池云服务器缺少 resourcePoolId，无法调用上游退款", new Object[0]);
         }
      } else {
         BaseIndependent indep = this.independentApi.getById(serverId);
         if (indep == null) {
            throw new CommonException("未找到独立服务器: " + serverId, new Object[0]);
         } else if (StrUtil.isNotBlank(indep.getResourcePoolId())) {
            return indep.getResourcePoolId().trim();
         } else {
            throw new CommonException("资源池独立服务器缺少 resourcePoolId，无法调用上游退款", new Object[0]);
         }
      }
   }

   private String buildPoolRefundRequestJson(ServerRefundParam param, List<Order> orders, int productType) {
      ServerRefundModeEnum mode = ServerRefundModeEnum.fromParam(param.getRefundMode());
      Long memberNo = this.resolveMemberNo(param.getServerId(), productType);
      Map<String, Object> m = new LinkedHashMap<>();
      String localServerId = param.getServerId();
      m.put("refundMode", mode.name());
      m.put("memberNo", memberNo);
      m.put("reason", param.getReason());
      m.put("orderNos", orders.stream().map(Order::getOrderNo).filter(CharSequenceUtil::isNotBlank).distinct().toList());
      String upstreamPk;
      if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
         BaseServer c = this.serverApi.getById(localServerId);
         if (c == null) {
            throw new CommonException("未找到云服务器: " + localServerId, new Object[0]);
         }

         upstreamPk = StrUtil.trimToNull(c.getUpstreamServerId());
         if (StrUtil.isBlank(upstreamPk)) {
            throw new CommonException("资源池云服务器缺少 UPSTREAM_SERVER_ID（上游主键），无法请求上游退款", new Object[0]);
         }
      } else {
         if (!ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
            throw new CommonException("虚拟主机不走资源池退款", new Object[0]);
         }

         BaseIndependent indep = this.independentApi.getById(localServerId);
         if (indep == null) {
            throw new CommonException("未找到独立服务器: " + localServerId, new Object[0]);
         }

         upstreamPk = StrUtil.trimToNull(indep.getUpstreamServerId());
         if (StrUtil.isBlank(upstreamPk)) {
            throw new CommonException("资源池独立服务器缺少 UPSTREAM_SERVER_ID（上游主键），无法请求上游退款", new Object[0]);
         }
      }

      m.put("upstreamServerId", upstreamPk);
      return JSONUtil.toJsonStr(m);
   }

   private void assertUpstreamPoolBusinessOk(String responseBody) {
      if (StrUtil.isBlank(responseBody)) {
         throw new CommonException("资源池返回为空", new Object[0]);
      } else {
         JSONObject root = JSONUtil.parseObj(responseBody);
         Integer code = root.getInt("code");
         if (code == null || code != 200) {
            throw new CommonException(StrUtil.blankToDefault(root.getStr("msg"), "资源池业务失败"), new Object[0]);
         }
      }
   }

   private Long resolveMemberNo(String serverId, int productType) {
      if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
         BaseServer cloud = this.serverApi.getById(serverId);
         if (cloud != null && cloud.getMemberNo() != null) {
            try {
               return Long.parseLong(cloud.getMemberNo().trim());
            } catch (NumberFormatException var5) {
               throw new CommonException("云服务器会员编号格式异常", new Object[0]);
            }
         } else {
            throw new CommonException("无法解析会员编号", new Object[0]);
         }
      } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
         BaseIndependent indep = this.independentApi.getById(serverId);
         if (indep != null && indep.getMemberNo() != null) {
            return indep.getMemberNo();
         } else {
            throw new CommonException("无法解析会员编号", new Object[0]);
         }
      } else {
         BaseMemberVhost vhost = this.memberVhostApi.selectById(serverId);
         if (vhost != null && !StrUtil.isBlank(vhost.getMemberNo())) {
            try {
               return Long.parseLong(vhost.getMemberNo().trim());
            } catch (NumberFormatException var6) {
               throw new CommonException("虚拟主机会员编号格式异常", new Object[0]);
            }
         } else {
            throw new CommonException("无法解析会员编号", new Object[0]);
         }
      }
   }

   private String resolvePublicIpForRefundRemark(String serverId, int productType) {
      if (ProductTypeEnum.VHOST.getCode().equals(productType)) {
         return this.resolveVhostRemark(this.memberVhostApi.selectById(serverId));
      } else if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
         BaseServer s = this.serverApi.getById(serverId);
         return s != null ? StrUtil.trimToEmpty(s.getPublicIp()) : "";
      } else {
         List<BaseIndependentIp> ips = this.independentApi.listIpsByIndependentServerId(serverId);
         if (ips != null) {
            for (BaseIndependentIp ip : ips.stream()
               .filter(ipx -> ipx != null && "PUBLIC".equalsIgnoreCase(StrUtil.trim(ipx.getType())))
               .sorted(Comparator.comparingInt(ipx -> ipx.getIsMain() != null && ipx.getIsMain() == 1 ? 0 : 1))
               .toList()) {
               if (StrUtil.isNotBlank(ip.getIp())) {
                  return ip.getIp().trim();
               }
            }
         }

         BaseIndependent indep = this.independentApi.getById(serverId);
         return indep != null ? StrUtil.trimToEmpty(indep.getServerIp()) : "";
      }
   }

   private String resolveRefundBizType(int productType) {
      if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
         return "云服务器退款";
      } else {
         return ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType) ? "独立服务器退款" : "虚拟主机退款";
      }
   }

   private String resolveVhostRemark(BaseMemberVhost vhost) {
      if (vhost == null) {
         return "";
      } else if (StrUtil.isNotBlank(vhost.getHostName())) {
         return vhost.getHostName().trim();
      } else {
         if (vhost.getBindDomains() != null) {
            for (String domain : vhost.getBindDomains()) {
               if (StrUtil.isNotBlank(domain)) {
                  return domain.trim();
               }
            }
         }

         return "";
      }
   }

   private static final class OrderRefundContext {
      private final Order order;
      private final BigDecimal refundBaseAmount;
      private final int totalServerCount;
      private final int remainingServerCountBeforeRefund;

      private OrderRefundContext(Order order, BigDecimal refundBaseAmount, int totalServerCount, int remainingServerCountBeforeRefund) {
         this.order = order;
         this.refundBaseAmount = refundBaseAmount;
         this.totalServerCount = totalServerCount;
         this.remainingServerCountBeforeRefund = remainingServerCountBeforeRefund;
      }

      private Order getOrder() {
         return this.order;
      }

      private BigDecimal getRefundBaseAmount() {
         return this.refundBaseAmount;
      }

      private int getTotalServerCount() {
         return this.totalServerCount;
      }

      private int getRemainingServerCountBeforeRefund() {
         return this.remainingServerCountBeforeRefund;
      }

      private boolean shouldFinalizeOrderStatus() {
         return this.remainingServerCountBeforeRefund <= 1;
      }
   }
}
