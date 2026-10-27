package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.enums.order.ServerRefundModeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServer;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServerConfig;
import com.rxinyun.member.order.modular.ecs.entity.MemberServerRefundRecord;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderMapper;
import com.rxinyun.member.order.modular.ecs.mapper.MemberServerRefundRecordMapper;
import com.rxinyun.member.order.modular.ecs.param.MemberServerRefundParam;
import com.rxinyun.member.order.modular.ecs.response.MemberServerRefundPreviewResponse;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerConfigService;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import com.rxinyun.member.order.modular.ecs.service.MemberServerRefundService;
import com.rxinyun.member.order.modular.independent.entity.MemberIndependentOrderServerConfig;
import com.rxinyun.member.order.modular.independent.service.MemberIndependentOrderServerConfigService;
import com.rxinyun.member.order.modular.invoice.service.MemberOrderInvoiceService;
import com.rxinyun.member.order.modular.vhost.entity.VhostOrderConfig;
import com.rxinyun.member.order.modular.vhost.service.VhostOrderConfigService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServerRefundServiceImpl implements MemberServerRefundService {
   private static final Logger log = LoggerFactory.getLogger(MemberServerRefundServiceImpl.class);
   @Resource
   private MemberOrderMapper memberOrderMapper;
   @Resource
   private MemberServerRefundRecordMapper memberServerRefundRecordMapper;
   @Resource
   private MemberServerApi memberServerApi;
   @Resource
   private MemberIndependentApi memberIndependentApi;
   @Resource
   private MemberVhostApi memberVhostApi;
   @Resource
   private VhostOrderConfigService vhostOrderConfigService;
   @Resource
   private MemberOrderServerService memberOrderServerService;
   @Resource
   private MemberOrderServerConfigService memberOrderServerConfigService;
   @Resource
   private MemberIndependentOrderServerConfigService memberIndependentOrderServerConfigService;
   @Resource
   private MemberOrderInvoiceService memberOrderInvoiceService;

   @Override
   public CommonResult<MemberServerRefundPreviewResponse> preview(MemberServerRefundParam param) {
      try {
         return CommonResult.data(this.buildPreview(param));
      } catch (CommonException var3) {
         return CommonResult.error(var3.getMessage());
      } catch (Exception var4) {
         log.error("会员服务器退款试算失败 serverId={} productType={}", new Object[]{param.getServerId(), param.getProductType(), var4});
         return CommonResult.error("退款试算失败");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> execute(MemberServerRefundParam param) {
      try {
         MemberServerRefundServiceImpl.RefundTarget target = this.resolveOwnedTarget(param);
         this.assertServerNotLocked(target);
         List<MemberOrder> occupiedCheckOrders = this.loadPaidOrders(target.serverId, target.memberNo);
         if (!occupiedCheckOrders.isEmpty()) {
            this.memberOrderInvoiceService.assertOrdersNotOccupied(occupiedCheckOrders.stream().map(MemberOrder::getOrderNo).toList());
         }

         MemberServerRefundPreviewResponse preview = this.buildPreview(param);
         if (preview.getRefundAmount() != null && preview.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
            long pendingCount = this.memberServerRefundRecordMapper
               .selectCount(
                  (Wrapper<MemberServerRefundRecord>)((LambdaQueryWrapper<MemberServerRefundRecord>)new LambdaQueryWrapper<MemberServerRefundRecord>().eq(MemberServerRefundRecord::getServerId, target.serverId))
                     .eq(MemberServerRefundRecord::getStatus, 0)
               );
            if (pendingCount > 0L) {
               return CommonResult.error("该服务器已存在待处理的退款申请，请勿重复提交");
            } else {
               Date now = new Date();
               MemberServerRefundRecord record = new MemberServerRefundRecord();
               record.setId(SnowflakeIdGenerator.generateId());
               record.setMemberNo(target.memberNo);
               record.setServerId(target.serverId);
               record.setProductType(target.productType);
               record.setServerName(StrUtil.blankToDefault(target.serverName, target.serverId));
               record.setPublicIp(StrUtil.blankToDefault(target.publicIp, ""));
               record.setRefundAmount(preview.getRefundAmount());
               record.setStatus(0);
               record.setCreateTime(now);
               record.setUpdateTime(now);
               this.memberServerRefundRecordMapper.insert(record);
               log.info(
                  "会员服务器退款申请已提交 memberNo={} serverId={} productType={} refundAmount={} recordId={}",
                  new Object[]{target.memberNo, target.serverId, target.productType, preview.getRefundAmount(), record.getId()}
               );
               return CommonResult.data(record.getId());
            }
         } else {
            return CommonResult.error("按天退款金额为 0，已到期或无可退时长");
         }
      } catch (CommonException var9) {
         return CommonResult.error(var9.getMessage());
      } catch (Exception var10) {
         log.error("会员服务器退款申请失败 serverId={} productType={}", new Object[]{param.getServerId(), param.getProductType(), var10});
         return CommonResult.error(var10.getMessage() != null ? var10.getMessage() : "退款申请失败");
      }
   }

   private MemberServerRefundPreviewResponse buildPreview(MemberServerRefundParam param) {
      MemberServerRefundServiceImpl.RefundTarget target = this.resolveOwnedTarget(param);
      List<MemberOrder> orders = this.loadPaidOrders(target.serverId, target.memberNo);
      if (orders.isEmpty()) {
         throw new CommonException("没有可退款的已支付订单", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(target.businessStatus)) {
         throw new CommonException("服务器已删除，无法退款", new Object[0]);
      } else if (target.endTime == null) {
         throw new CommonException("实例缺少到期时间，无法计价", new Object[0]);
      } else {
         Date buyTime = target.buyTime;
         if (buyTime == null) {
            buyTime = orders.stream().map(MemberOrder::getPayTime).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
         }

         if (buyTime == null) {
            buyTime = orders.stream().map(MemberOrder::getCreateTime).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(null);
         }

         if (buyTime == null) {
            throw new CommonException("缺少开通时间或订单时间，无法计算服务总时长", new Object[0]);
         } else {
            List<MemberServerRefundServiceImpl.OrderRefundContext> refundContexts = this.buildOrderRefundContexts(orders, target.productType);
            BigDecimal sumPayPrice = refundContexts.stream()
               .map(MemberServerRefundServiceImpl.OrderRefundContext::getRefundBaseAmount)
               .filter(amount -> amount != null && amount.compareTo(BigDecimal.ZERO) > 0)
               .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (sumPayPrice.compareTo(BigDecimal.ZERO) <= 0) {
               throw new CommonException("没有可退的实付金额", new Object[0]);
            } else {
               Date now = new Date();
               long totalMillis = target.endTime.getTime() - buyTime.getTime();
               long leftMillis = target.endTime.getTime() - now.getTime();
               BigDecimal refundAmount = this.computeProratedRefund(sumPayPrice, totalMillis, leftMillis);
               MemberServerRefundPreviewResponse response = new MemberServerRefundPreviewResponse();
               response.setRefundAmount(refundAmount);
               response.setRefundMode(ServerRefundModeEnum.PRORATED.name());
               response.setSumPayPrice(sumPayPrice.setScale(2, RoundingMode.HALF_UP));
               response.setTotalMillis(totalMillis);
               response.setLeftMillis(leftMillis);
               response.setBuyTime(buyTime);
               response.setEndTime(target.endTime);
               response.setNow(now);
               response.setOrderCount(refundContexts.size());
               response.setOrderNos(
                  new ArrayList<>(
                     refundContexts.stream().map(MemberServerRefundServiceImpl.OrderRefundContext::getOrder).map(MemberOrder::getOrderNo).distinct().toList()
                  )
               );
               response.setProductTypeResolved(target.productType);
               if (leftMillis <= 0L) {
                  response.setHint("已到期或剩余时长为 0，按天退款金额为 0");
               }

               return response;
            }
         }
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget resolveOwnedTarget(MemberServerRefundParam param) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      String serverId = StrUtil.trim(param.getServerId());
      if (StrUtil.isBlank(serverId)) {
         throw new CommonException("serverId 不能为空", new Object[0]);
      } else {
         Integer productType = param.getProductType();
         if (productType != null) {
            if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
               return this.resolveCloudTarget(serverId, loginUser.getId());
            } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
               return this.resolveIndependentTarget(serverId, loginUser.getId());
            } else if (ProductTypeEnum.VHOST.getCode().equals(productType)) {
               return this.resolveVhostTarget(serverId, loginUser.getId());
            } else {
               throw new CommonException("不支持的 productType: " + productType, new Object[0]);
            }
         } else {
            MemberServerRefundServiceImpl.RefundTarget cloud = this.tryResolveCloudTarget(serverId, loginUser.getId());
            if (cloud != null) {
               return cloud;
            } else {
               MemberServerRefundServiceImpl.RefundTarget independent = this.tryResolveIndependentTarget(serverId, loginUser.getId());
               if (independent != null) {
                  return independent;
               } else {
                  MemberServerRefundServiceImpl.RefundTarget vhost = this.tryResolveVhostTarget(serverId, loginUser.getId());
                  if (vhost != null) {
                     return vhost;
                  } else {
                     throw new CommonException("服务器不存在或无权退款", new Object[0]);
                  }
               }
            }
         }
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget resolveCloudTarget(String serverId, Long memberNo) {
      MemberServerRefundServiceImpl.RefundTarget target = this.tryResolveCloudTarget(serverId, memberNo);
      if (target == null) {
         throw new CommonException("未找到云服务器或无权退款", new Object[0]);
      } else {
         return target;
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget resolveIndependentTarget(String serverId, Long memberNo) {
      MemberServerRefundServiceImpl.RefundTarget target = this.tryResolveIndependentTarget(serverId, memberNo);
      if (target == null) {
         throw new CommonException("未找到独立服务器或无权退款", new Object[0]);
      } else {
         return target;
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget tryResolveCloudTarget(String serverId, Long memberNo) {
      BaseMemberServer server = this.memberServerApi.selectByServerIdAndMemberNo(serverId, memberNo);
      if (server != null && !StrUtil.isBlank(server.getId())) {
         MemberServerRefundServiceImpl.RefundTarget target = new MemberServerRefundServiceImpl.RefundTarget();
         target.serverId = server.getId();
         target.memberNo = server.getMemberNo();
         target.buyTime = server.getBuyTime();
         target.endTime = server.getEndTime();
         target.businessStatus = server.getBusinessStatus();
         target.serverStatus = server.getServerStatus();
         target.productType = ProductTypeEnum.CLOUD_SERVER.getCode();
         target.serverName = StrUtil.trimToNull(server.getHostName());
         target.publicIp = StrUtil.trimToEmpty(server.getPublicIp());
         target.resourcePoolId = StrUtil.trimToNull(server.getResourcePoolId());
         target.upstreamServerId = StrUtil.trimToNull(server.getUpstreamServerId());
         target.resourcePoolServer = Integer.valueOf(1).equals(server.getType())
            || StrUtil.isNotBlank(target.resourcePoolId)
            || StrUtil.isNotBlank(target.upstreamServerId);
         return target;
      } else {
         return null;
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget resolveVhostTarget(String serverId, Long memberNo) {
      MemberServerRefundServiceImpl.RefundTarget target = this.tryResolveVhostTarget(serverId, memberNo);
      if (target == null) {
         throw new CommonException("未找到虚拟主机或无权退款", new Object[0]);
      } else {
         return target;
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget tryResolveVhostTarget(String serverId, Long memberNo) {
      BaseMemberVhost host = this.memberVhostApi.selectByIdAndMemberNo(serverId, String.valueOf(memberNo));
      if (host != null && !StrUtil.isBlank(host.getId())) {
         MemberServerRefundServiceImpl.RefundTarget target = new MemberServerRefundServiceImpl.RefundTarget();
         target.serverId = host.getId();

         try {
            target.memberNo = Long.parseLong(StrUtil.trim(host.getMemberNo()));
         } catch (NumberFormatException var6) {
            throw new CommonException("虚拟主机会员编号格式异常", new Object[0]);
         }

         target.buyTime = host.getCreateTime();
         target.endTime = host.getEndTime();
         target.businessStatus = host.getBusinessStatus();
         target.serverStatus = host.getHostStatus();
         target.productType = ProductTypeEnum.VHOST.getCode();
         target.serverName = StrUtil.trimToNull(host.getHostName());
         target.publicIp = this.resolveVhostRemark(host);
         target.resourcePoolServer = false;
         return target;
      } else {
         return null;
      }
   }

   private String resolveVhostRemark(BaseMemberVhost host) {
      if (host == null) {
         return "";
      } else if (StrUtil.isNotBlank(host.getHostName())) {
         return host.getHostName().trim();
      } else {
         if (host.getBindDomains() != null) {
            for (String domain : host.getBindDomains()) {
               if (StrUtil.isNotBlank(domain)) {
                  return domain.trim();
               }
            }
         }

         return "";
      }
   }

   private MemberServerRefundServiceImpl.RefundTarget tryResolveIndependentTarget(String serverId, Long memberNo) {
      BaseMemberIndependentServer server = this.memberIndependentApi.selectByServerIdAndMemberNo(serverId, memberNo);
      if (server != null && !StrUtil.isBlank(server.getId())) {
         MemberServerRefundServiceImpl.RefundTarget target = new MemberServerRefundServiceImpl.RefundTarget();
         target.serverId = server.getId();
         target.memberNo = server.getMemberNo();
         target.buyTime = server.getBuyTime();
         target.endTime = server.getEndTime();
         target.businessStatus = server.getBusinessStatus();
         target.serverStatus = server.getServerStatus();
         target.productType = ProductTypeEnum.INDEPENDENT_SERVER.getCode();
         target.serverName = StrUtil.trimToNull(server.getServerName());
         target.publicIp = StrUtil.trimToEmpty(server.getServerIp());
         target.resourcePoolId = StrUtil.trimToNull(server.getResourcePoolId());
         target.upstreamServerId = StrUtil.trimToNull(server.getUpstreamServerId());
         target.resourcePoolServer = Integer.valueOf(1).equals(server.getType())
            || StrUtil.isNotBlank(target.resourcePoolId)
            || StrUtil.isNotBlank(target.upstreamServerId);
         return target;
      } else {
         return null;
      }
   }

   private List<MemberOrder> loadPaidOrders(String serverId, Long memberNo) {
      return this.memberOrderMapper.selectPaidRefundableOrdersByServerIdAndMemberNo(serverId, memberNo);
   }

   private List<MemberServerRefundServiceImpl.OrderRefundContext> buildOrderRefundContexts(List<MemberOrder> orders, int productType) {
      List<MemberServerRefundServiceImpl.OrderRefundContext> contexts = new ArrayList<>();

      for (MemberOrder order : orders) {
         if (order != null && !StrUtil.isBlank(order.getOrderNo())) {
            int remainingServerCountBeforeRefund = this.resolveRemainingServerCount(order, productType);
            int totalServerCount = this.resolveOrderServerCount(order, productType, remainingServerCountBeforeRefund);
            BigDecimal refundBaseAmount = this.resolveRefundBaseAmount(order.getPayPrice(), totalServerCount, remainingServerCountBeforeRefund);
            contexts.add(new MemberServerRefundServiceImpl.OrderRefundContext(order, refundBaseAmount));
         }
      }

      return contexts;
   }

   private int resolveOrderServerCount(MemberOrder order, int productType, int remainingServerCountBeforeRefund) {
      Integer configuredServerCount = this.resolveConfiguredServerCount(order, productType);
      return configuredServerCount != null && configuredServerCount > 0 ? configuredServerCount : Math.max(remainingServerCountBeforeRefund, 1);
   }

   private Integer resolveConfiguredServerCount(MemberOrder order, int productType) {
      if (order != null && !StrUtil.isBlank(order.getOrderNo())) {
         if (!Objects.equals(order.getOrderType(), OrderTypeEnum.NEW_PURCHASE.getCode())) {
            return 1;
         } else if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType)) {
            MemberOrderServerConfig config = this.memberOrderServerConfigService.queryByOrderNo(order.getOrderNo());
            return config == null ? null : config.getNumber();
         } else if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType)) {
            MemberIndependentOrderServerConfig config = this.memberIndependentOrderServerConfigService.queryByOrderNo(order.getOrderNo());
            return config == null ? null : config.getNumber();
         } else {
            VhostOrderConfig config = this.vhostOrderConfigService.queryByOrderNo(order.getOrderNo());
            return config == null ? null : config.getNumber();
         }
      } else {
         return null;
      }
   }

   private int resolveRemainingServerCount(MemberOrder order, int productType) {
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
         List<MemberOrderServer> linkedServers = this.memberOrderServerService.queryByList(order.getOrderNo());
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

   private BigDecimal computeProratedRefund(BigDecimal sumPayPrice, long totalMillis, long leftMillis) {
      BigDecimal s = sumPayPrice == null ? BigDecimal.ZERO : sumPayPrice;
      if (s.compareTo(BigDecimal.ZERO) <= 0) {
         return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
      } else if (totalMillis <= 0L) {
         throw new CommonException("服务总时长无效，无法按剩余比例退款", new Object[0]);
      } else if (leftMillis <= 0L) {
         return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
      } else {
         BigDecimal ratio = BigDecimal.valueOf(leftMillis).divide(BigDecimal.valueOf(totalMillis), 8, RoundingMode.HALF_UP);
         return s.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
      }
   }

   private void assertServerNotLocked(MemberServerRefundServiceImpl.RefundTarget target) {
      if (target != null && ServerStatusEnum.LOCKED.getCode().equals(target.serverStatus)) {
         throw new CommonException("服务器已锁定，请先解锁后再申请退款", new Object[0]);
      }
   }

   private static class OrderRefundContext {
      private final MemberOrder order;
      private final BigDecimal refundBaseAmount;

      private OrderRefundContext(MemberOrder order, BigDecimal refundBaseAmount) {
         this.order = order;
         this.refundBaseAmount = refundBaseAmount;
      }

      private MemberOrder getOrder() {
         return this.order;
      }

      private BigDecimal getRefundBaseAmount() {
         return this.refundBaseAmount;
      }
   }

   private static class RefundTarget {
      private String serverId;
      private Long memberNo;
      private Date buyTime;
      private Date endTime;
      private Integer businessStatus;
      private String serverStatus;
      private Integer productType;
      private String serverName;
      private String publicIp;
      private String resourcePoolId;
      private String upstreamServerId;
      private boolean resourcePoolServer;
   }
}
