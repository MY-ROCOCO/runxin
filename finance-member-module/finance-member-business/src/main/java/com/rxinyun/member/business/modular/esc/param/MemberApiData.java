package com.rxinyun.member.business.modular.esc.param;

public class MemberApiData {
   private int code;
   private String msg;

   public int getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public void setCode(final int code) {
      this.code = code;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MemberApiData other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getCode() != other.getCode()) {
         return false;
      } else {
         Object this$msg = this.getMsg();
         Object other$msg = other.getMsg();
         return this$msg == null ? other$msg == null : this$msg.equals(other$msg);
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof MemberApiData;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getCode();
      Object $msg = this.getMsg();
      return result * 59 + ($msg == null ? 43 : $msg.hashCode());
   }

   @Override
   public String toString() {
      return "MemberApiData(code=" + this.getCode() + ", msg=" + this.getMsg() + ")";
   }
}
