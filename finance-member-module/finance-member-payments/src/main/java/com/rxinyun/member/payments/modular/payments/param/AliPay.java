package com.rxinyun.member.payments.modular.payments.param;

public class AliPay {
   private String traceNo;
   private double totalAmount;
   private String subject;
   private String alipayTraceNo;

   public String getTraceNo() {
      return this.traceNo;
   }

   public double getTotalAmount() {
      return this.totalAmount;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getAlipayTraceNo() {
      return this.alipayTraceNo;
   }

   public void setTraceNo(final String traceNo) {
      this.traceNo = traceNo;
   }

   public void setTotalAmount(final double totalAmount) {
      this.totalAmount = totalAmount;
   }

   public void setSubject(final String subject) {
      this.subject = subject;
   }

   public void setAlipayTraceNo(final String alipayTraceNo) {
      this.alipayTraceNo = alipayTraceNo;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AliPay other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (Double.compare(this.getTotalAmount(), other.getTotalAmount()) != 0) {
         return false;
      } else {
         Object this$traceNo = this.getTraceNo();
         Object other$traceNo = other.getTraceNo();
         if (this$traceNo == null ? other$traceNo == null : this$traceNo.equals(other$traceNo)) {
            Object this$subject = this.getSubject();
            Object other$subject = other.getSubject();
            if (this$subject == null ? other$subject == null : this$subject.equals(other$subject)) {
               Object this$alipayTraceNo = this.getAlipayTraceNo();
               Object other$alipayTraceNo = other.getAlipayTraceNo();
               return this$alipayTraceNo == null ? other$alipayTraceNo == null : this$alipayTraceNo.equals(other$alipayTraceNo);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AliPay;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      long $totalAmount = Double.doubleToLongBits(this.getTotalAmount());
      result = result * 59 + (int)($totalAmount >>> 32 ^ $totalAmount);
      Object $traceNo = this.getTraceNo();
      result = result * 59 + ($traceNo == null ? 43 : $traceNo.hashCode());
      Object $subject = this.getSubject();
      result = result * 59 + ($subject == null ? 43 : $subject.hashCode());
      Object $alipayTraceNo = this.getAlipayTraceNo();
      return result * 59 + ($alipayTraceNo == null ? 43 : $alipayTraceNo.hashCode());
   }

   @Override
   public String toString() {
      return "AliPay(traceNo="
         + this.getTraceNo()
         + ", totalAmount="
         + this.getTotalAmount()
         + ", subject="
         + this.getSubject()
         + ", alipayTraceNo="
         + this.getAlipayTraceNo()
         + ")";
   }
}
