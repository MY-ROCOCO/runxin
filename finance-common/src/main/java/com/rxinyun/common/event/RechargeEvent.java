package com.rxinyun.common.event;

import java.util.Date;
import org.springframework.context.ApplicationEvent;

public class RechargeEvent extends ApplicationEvent {
   private static final long serialVersionUID = 1L;
   private final String outTradeNo;
   private final Date payTime;
   private final String alipayPayNo;

   public RechargeEvent(Object source, String outTradeNo, Date payTime, String alipayPayNo) {
      super(source);
      this.outTradeNo = outTradeNo;
      this.payTime = payTime;
      this.alipayPayNo = alipayPayNo;
   }

   public String getOutTradeNo() {
      return this.outTradeNo;
   }

   public Date getPayTime() {
      return this.payTime;
   }

   public String getAlipayPayNo() {
      return this.alipayPayNo;
   }
}
