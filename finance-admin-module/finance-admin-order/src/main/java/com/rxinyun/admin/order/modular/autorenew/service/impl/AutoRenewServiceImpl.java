package com.rxinyun.admin.order.modular.autorenew.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.order.modular.autorenew.service.AutoRenewService;
import com.rxinyun.admin.order.modular.ecs.entity.Order;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.admin.order.modular.ecs.service.CalculatePriceService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.admin.order.modular.ecs.service.OrderService;
import com.rxinyun.admin.order.modular.independent.param.IndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.admin.order.modular.independent.service.IndependentCalculatePriceService;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudRenewOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.manual.response.ManualOrderResult;
import com.rxinyun.admin.order.modular.manual.service.ManualOrderService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AutoRenewServiceImpl implements AutoRenewService {
   private static final Logger log = LoggerFactory.getLogger(AutoRenewServiceImpl.class);
   private static final String CONFIG_DAYS_BEFORE = "AUTO_RENEW_DAYS_BEFORE";
   private static final int DEFAULT_DAYS_BEFORE = 1;
   private static final int DEDUCT_MODE_AUTO = 2;
   private static final int RENEW_MONTHS = 1;
   private static final long RECENT_PAID_HOURS = 25L;
   private static final int LOCK_EXPIRE_MS = 600000;
   private static final String LOCK_PREFIX = "auto_renew:lock:";
   private static final String INSUFFICIENT_PREFIX = "auto_renew:insufficient:";
   private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private ServerApi serverApi;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private ManualOrderService manualOrderService;
   @Resource
   private CalculatePriceService calculatePriceService;
   @Resource
   private IndependentCalculatePriceService independentCalculatePriceService;
   @Resource
   private OrderService orderService;
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private BalanceApi balanceApi;
   @Resource
   private ToolsNotificationApi toolsNotificationApi;
   @Resource
   private CommonCacheOperator commonCacheOperator;

   @Override
   public void execute() {
      int daysBefore = this.readDaysBefore();
      LocalDateTime windowEnd = TimeUtil.now().plusDays(daysBefore);
      List<BaseServer> cloudServers = this.serverApi.getServersForAutoRenew(windowEnd);
      List<BaseIndependent> independents = this.independentApi.getServersForAutoRenew(windowEnd);
      log.info("自动续费扫描: 窗口={}天, 云服务器={}, 独立服务器={}", new Object[]{daysBefore, cloudServers.size(), independents.size()});
      cloudServers.forEach(this::renewCloud);
      independents.forEach(this::renewIndependent);
   }

   private void renewCloud(BaseServer server) {
      Long memberId = this.parseMemberId(server.getMemberNo());
      if (memberId == null) {
         log.warn("云服务器 {} 未关联会员，跳过自动续费", server.getId());
      } else {
         this.processOne("cloud", server.getId(), memberId, server.getHostName(), server.getEndTime(), true);
      }
   }

   private void renewIndependent(BaseIndependent server) {
      if (server.getMemberNo() == null) {
         log.warn("独立服务器 {} 未关联会员，跳过自动续费", server.getId());
      } else {
         this.processOne("independent", server.getId(), server.getMemberNo(), server.getServerName(), server.getEndTime(), false);
      }
   }

   private void processOne(String product, String serverId, Long memberId, String serverName, Date endTime, boolean cloud) {
      String lockKey = "auto_renew:lock:" + product + ":" + serverId;
      String requestId = UUID.randomUUID().toString();
      if (!Boolean.TRUE.equals(this.commonCacheOperator.tryGetDistributedLock(lockKey, requestId, 600000))) {
         log.debug("自动续费跳过，未拿到锁: {} {}", product, serverId);
      } else {
         try {
            if (this.hasRecentPaidRenew(serverId)) {
               log.info("自动续费跳过，近{}小时已有已支付续费单: {} {}", new Object[]{25L, product, serverId});
               this.retryRecentFulfill(serverId);
               return;
            }

            BigDecimal needAmount;
            try {
               needAmount = this.calculateNeedAmount(serverId, cloud);
            } catch (Exception var19) {
               log.warn("自动续费计价失败，跳过: {} {}, {}", new Object[]{product, serverId, var19.getMessage()});
               return;
            }

            BaseBalance balance = this.balanceApi.selectByMemberNo(memberId);
            BigDecimal current = balance != null && balance.getBalance() != null ? balance.getBalance() : BigDecimal.ZERO;
            if (needAmount.compareTo(BigDecimal.ZERO) <= 0 || current.compareTo(needAmount) >= 0) {
               CommonResult<ManualOrderResult> result = cloud ? this.createCloudRenew(memberId, serverId) : this.createIndependentRenew(memberId, serverId);
               if (result != null && result.getCode() != null && result.getCode() == 200) {
                  this.notifySuccess(product, memberId, serverId, serverName, (ManualOrderResult)result.getData(), endTime);
                  return;
               }

               String msg = result == null ? "空结果" : result.getMsg();
               this.handleRenewFailure(product, memberId, serverId, serverName, needAmount, current, endTime, msg);
               return;
            }

            this.notifyBalanceFail(product, memberId, serverId, serverName, needAmount, current, endTime);
         } catch (CommonException var20) {
            this.handleRenewFailure(product, memberId, serverId, serverName, null, null, endTime, var20.getMessage());
            return;
         } catch (Exception var21) {
            log.error("自动续费执行异常: {} {}", new Object[]{product, serverId, var21});
            return;
         } finally {
            this.commonCacheOperator.releaseDistributedLock(lockKey, requestId);
         }
      }
   }

   private CommonResult<ManualOrderResult> createCloudRenew(Long memberId, String serverId) {
      ManualCloudRenewOrderParam param = new ManualCloudRenewOrderParam();
      param.setMemberId(memberId);
      param.setServerId(serverId);
      param.setDuration(1);
      param.setDeductMode(2);
      return this.manualOrderService.renewCloudOrder(param, "自动续费云服务器");
   }

   private CommonResult<ManualOrderResult> createIndependentRenew(Long memberId, String serverId) {
      ManualIndependentRenewOrderParam param = new ManualIndependentRenewOrderParam();
      param.setMemberId(memberId);
      param.setServerId(serverId);
      param.setDuration(1);
      param.setDeductMode(2);
      return this.manualOrderService.renewIndependentOrder(param, "自动续费独立服务器");
   }

   private BigDecimal calculateNeedAmount(String serverId, boolean cloud) {
      if (cloud) {
         RenewOrderParam renewParam = new RenewOrderParam();
         renewParam.setServerId(serverId);
         renewParam.setDuration(1);
         CommonResult<CalculatePriceResponse> result = this.calculatePriceService.renew(renewParam);
         if (result != null && result.getData() != null && ((CalculatePriceResponse)result.getData()).getPayPrice() != null) {
            return ((CalculatePriceResponse)result.getData()).getPayPrice();
         } else {
            throw new CommonException(result == null ? "云服务器续费计价失败" : result.getMsg(), new Object[0]);
         }
      } else {
         IndependentRenewOrderParam renewParam = new IndependentRenewOrderParam();
         renewParam.setServerId(serverId);
         renewParam.setDuration(1);
         CommonResult<IndependentCalculatePriceResponse> result = this.independentCalculatePriceService.renew(renewParam);
         if (result != null && result.getData() != null && ((IndependentCalculatePriceResponse)result.getData()).getPayPrice() != null) {
            return ((IndependentCalculatePriceResponse)result.getData()).getPayPrice();
         } else {
            throw new CommonException(result == null ? "独立服务器续费计价失败" : result.getMsg(), new Object[0]);
         }
      }
   }

   private boolean hasRecentPaidRenew(String serverId) {
      List<OrderServer> relations = this.orderServerService.list((Wrapper<OrderServer>)new LambdaQueryWrapper<OrderServer>().eq(OrderServer::getServerId, serverId));
      if (relations != null && !relations.isEmpty()) {
         List<String> orderNos = relations.stream().map(OrderServer::getOrderNo).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toList());
         if (orderNos.isEmpty()) {
            return false;
         } else {
            Date since = new Date(System.currentTimeMillis() - 90000000L);
            return this.orderService
                  .count(
                     (Wrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)new LambdaQueryWrapper<Order>().in(Order::getOrderNo, orderNos))
                              .eq(Order::getOrderType, OrderTypeEnum.RENEW.getCode()))
                           .eq(Order::getOrderStatus, OrderStatusEnum.PAID.getCode()))
                        .ge(Order::getPayTime, since)
                  )
               > 0L;
         }
      } else {
         return false;
      }
   }

   private void retryRecentFulfill(String serverId) {
      List<OrderServer> relations = this.orderServerService.list((Wrapper<OrderServer>)new LambdaQueryWrapper<OrderServer>().eq(OrderServer::getServerId, serverId));
      if (relations != null && !relations.isEmpty()) {
         List<String> orderNos = relations.stream().map(OrderServer::getOrderNo).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toList());
         Date since = new Date(System.currentTimeMillis() - 90000000L);
         List<Order> paid = this.orderService
            .list(
               (Wrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)((LambdaQueryWrapper<Order>)new LambdaQueryWrapper<Order>()
                                 .in(Order::getOrderNo, orderNos))
                              .eq(Order::getOrderType, OrderTypeEnum.RENEW.getCode()))
                           .eq(Order::getOrderStatus, OrderStatusEnum.PAID.getCode()))
                        .ge(Order::getPayTime, since))
                     .orderByDesc(Order::getPayTime))
                  .last("limit 1")
            );
         if (!paid.isEmpty()) {
            this.orderService.handlePaidOrderAsync(paid.get(0).getOrderNo());
         }
      }
   }

   private void handleRenewFailure(
      String product, Long memberId, String serverId, String serverName, BigDecimal needAmount, BigDecimal current, Date endTime, String message
   ) {
      log.warn("自动续费失败: {} {} {}", new Object[]{product, serverId, message});
      if (StrUtil.contains(message, "余额不足")) {
         this.notifyBalanceFail(product, memberId, serverId, serverName, needAmount, current, endTime);
      }
   }

   private void notifySuccess(String product, Long memberId, String serverId, String serverName, ManualOrderResult orderResult, Date endTime) {
      Map<String, Object> params = new HashMap<>();
      params.put("serverName", StrUtil.blankToDefault(serverName, serverId));
      params.put("serverId", serverId);
      params.put("orderNo", orderResult == null ? "" : StrUtil.blankToDefault(orderResult.getOrderNo(), ""));
      params.put("payAmount", orderResult != null && orderResult.getPayPrice() != null ? orderResult.getPayPrice().toPlainString() : "0");
      Date newEndTime = endTime == null ? null : DateUtil.offsetMonth(endTime, 1);
      params.put("newEndTime", this.formatBeijing(newEndTime));
      String type = "cloud".equals(product)
         ? NotificationTypeEnum.MEMBER_CLOUD_SERVER_AUTO_RENEW_SUCCESS.getCode()
         : NotificationTypeEnum.MEMBER_DEDICATED_SERVER_AUTO_RENEW_SUCCESS.getCode();
      this.toolsNotificationApi.sendMemberNotificationBoth(type, memberId, params);
   }

   private void notifyBalanceFail(String product, Long memberId, String serverId, String serverName, BigDecimal needAmount, BigDecimal current, Date endTime) {
      String redisKey = "auto_renew:insufficient:" + product + ":" + serverId;
      if (this.commonCacheOperator.get(redisKey) != null) {
         log.debug("自动续费余额不足今日已通知: {} {}", product, serverId);
      } else {
         Map<String, Object> params = new HashMap<>();
         params.put("serverName", StrUtil.blankToDefault(serverName, serverId));
         params.put("serverId", serverId);
         params.put("needAmount", needAmount == null ? "" : needAmount.toPlainString());
         params.put("currentBalance", current == null ? "" : current.toPlainString());
         params.put("expiryDate", this.formatBeijing(endTime));
         String type = "cloud".equals(product)
            ? NotificationTypeEnum.MEMBER_CLOUD_SERVER_AUTO_RENEW_BALANCE_FAIL.getCode()
            : NotificationTypeEnum.MEMBER_DEDICATED_SERVER_AUTO_RENEW_BALANCE_FAIL.getCode();
         this.toolsNotificationApi.sendMemberNotificationBoth(type, memberId, params);
         this.commonCacheOperator.put(redisKey, TimeUtil.format(TimeUtil.now()), this.getExpireSeconds());
      }
   }

   private int readDaysBefore() {
      String value = this.devConfigApi.getValueByKey("AUTO_RENEW_DAYS_BEFORE");
      if (StrUtil.isBlank(value)) {
         return 1;
      } else {
         try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : 1;
         } catch (NumberFormatException var3) {
            return 1;
         }
      }
   }

   private Long parseMemberId(String memberNo) {
      if (StrUtil.isBlank(memberNo)) {
         return null;
      } else {
         try {
            return Long.valueOf(memberNo);
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }

   private String formatBeijing(Date date) {
      return date == null ? "" : date.toInstant().atZone(BEIJING_ZONE).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
   }

   private long getExpireSeconds() {
      LocalDateTime now = TimeUtil.now();
      LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
      return Math.max(Duration.between(now, endOfDay).getSeconds(), 60L);
   }
}
