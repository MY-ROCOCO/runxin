package com.rxinyun.admin.dev.modular.job.task;

import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.member.api.RechargeBalanceApi;
import com.rxinyun.order.api.OrderApi;
import com.rxinyun.order.core.pojo.BaseOrder;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderUnpaidTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(OrderUnpaidTaskRunner.class);
   private static final String PAYMENT_ORDER_EXPIRATION_TIME = "PAYMENT_ORDER_EXPIRATION_TIME";
   @Resource
   private OrderApi orderApi;
   @Resource
   private RechargeBalanceApi rechargeBalanceApi;
   @Autowired
   private DevConfigApi devConfigApi;
   private static final long ORDER_TIMEOUT = 1800000L;

   public void action() {
      String paymentOrderExpirationTime = this.devConfigApi.getValueByKey("PAYMENT_ORDER_EXPIRATION_TIME");
      if (ObjectUtil.isEmpty(paymentOrderExpirationTime)) {
         throw new CommonException("支付订单过期时间配置错误：支付订单过期时间为空", new Object[0]);
      } else {
         Long orderTimeout = Long.valueOf(paymentOrderExpirationTime) * 60L * 1000L;
         log.info("订单未支付超时,关闭订单");
         List<BaseOrder> ordersToClose = this.orderApi.queryUnpaidOrder();
         log.info("订单未支付超时,关闭订单,订单数量:{}", ordersToClose.size());

         for (BaseOrder order : ordersToClose) {
            if (order.getCreateTime().getTime() + orderTimeout < System.currentTimeMillis()) {
               log.info("订单未支付超时,关闭订单,订单编号:{}", order.getOrderNo());
               order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
               this.orderApi.updateById(order);
            }
         }

         Date expireBefore = new Date(System.currentTimeMillis() - orderTimeout);
         int closedRechargeCount = this.rechargeBalanceApi.expireUnpaidRecharges(expireBefore);
         log.info("未支付充值单超时关闭,数量:{}", closedRechargeCount);
      }
   }
}
