package com.rxinyun.common.event;

import java.util.Date;
import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {
   private static final long serialVersionUID = 1L;
   private final String orderNo;
   private final Date payTime;
   private final String alipayPayNo;
   private final Integer payType;

   public OrderEvent(Object source, String orderNo, Date payTime, String alipayPayNo, Integer payType) {
      super(source);
      this.orderNo = orderNo;
      this.payTime = payTime;
      this.alipayPayNo = alipayPayNo;
      this.payType = payType;
   }

   public OrderEvent(Object source, String orderNo, Date payTime, String alipayPayNo) {
      this(source, orderNo, payTime, alipayPayNo, null);
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Date getPayTime() {
      return this.payTime;
   }

   public String getAlipayPayNo() {
      return this.alipayPayNo;
   }

   public Integer getPayType() {
      return this.payType;
   }
}
