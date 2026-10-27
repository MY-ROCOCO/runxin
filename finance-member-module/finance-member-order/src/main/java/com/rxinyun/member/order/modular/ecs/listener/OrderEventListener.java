package com.rxinyun.member.order.modular.ecs.listener;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.event.OrderEvent;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Order(1)
@Component
@Transactional
public class OrderEventListener {
   private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
   @Resource
   private MemberOrderService memberOrderService;

   @EventListener({OrderEvent.class})
   public void listen(OrderEvent event) {
      long startTime = System.currentTimeMillis();
      String orderNo = event.getOrderNo();

      try {
         log.info("开始处理支付成功事件，订单号: {}", orderNo);
         MemberOrder order = (MemberOrder)this.memberOrderService.getOne((Wrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, orderNo));
         if (order == null) {
            log.error("订单不存在，订单号: {}", orderNo);
            this.recordEventFailure(orderNo, "订单不存在");
         } else if (OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
            log.warn("订单已处理过，跳过重复处理，订单号: {}", orderNo);
         } else {
            long eventReceivedTime = System.currentTimeMillis();
            long paymentTime = event.getPayTime().getTime();
            long processingDelay = eventReceivedTime - paymentTime;
            log.info("支付状态更新延迟监控 - 订单号: {}, 延迟: {}ms", orderNo, processingDelay);
            this.memberOrderService.paySuccess(orderNo, event.getPayTime(), event.getAlipayPayNo(), event.getPayType());
            long duration = System.currentTimeMillis() - startTime;
            log.info("支付成功事件处理完成，订单号: {}, 耗时: {}ms", orderNo, duration);
         }
      } catch (Exception var14) {
         log.error("处理支付成功事件异常，订单号: {}, 错误: {}", orderNo, var14.getMessage());
         this.recordEventFailure(orderNo, var14.getMessage());
         throw new RuntimeException("处理支付事件失败", var14);
      }
   }

   private void recordEventFailure(String orderNo, String errorMsg) {
      log.error("支付事件处理失败记录 - 订单号: {}, 错误信息: {}", orderNo, errorMsg);
   }
}
