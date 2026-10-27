package com.rxinyun.member.business.modular.esc.param;

public class MemberApiParam {
   private int code;
   private String sign;
   private MemberApiData data;
   private String callbackParam;
   private String action;
   private String timestamp;

   public int getCode() {
      return this.code;
   }

   public String getSign() {
      return this.sign;
   }

   public MemberApiData getData() {
      return this.data;
   }

   public String getCallbackParam() {
      return this.callbackParam;
   }

   public String getAction() {
      return this.action;
   }

   public String getTimestamp() {
      return this.timestamp;
   }

   public void setCode(final int code) {
      this.code = code;
   }

   public void setSign(final String sign) {
      this.sign = sign;
   }

   public void setData(final MemberApiData data) {
      this.data = data;
   }

   public void setCallbackParam(final String callbackParam) {
      this.callbackParam = callbackParam;
   }

   public void setAction(final String action) {
      this.action = action;
   }

   public void setTimestamp(final String timestamp) {
      this.timestamp = timestamp;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MemberApiParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getCode() != other.getCode()) {
         return false;
      } else {
         Object this$sign = this.getSign();
         Object other$sign = other.getSign();
         if (this$sign == null ? other$sign == null : this$sign.equals(other$sign)) {
            Object this$data = this.getData();
            Object other$data = other.getData();
            if (this$data == null ? other$data == null : this$data.equals(other$data)) {
               Object this$callbackParam = this.getCallbackParam();
               Object other$callbackParam = other.getCallbackParam();
               if (this$callbackParam == null ? other$callbackParam == null : this$callbackParam.equals(other$callbackParam)) {
                  Object this$action = this.getAction();
                  Object other$action = other.getAction();
                  if (this$action == null ? other$action == null : this$action.equals(other$action)) {
                     Object this$timestamp = this.getTimestamp();
                     Object other$timestamp = other.getTimestamp();
                     return this$timestamp == null ? other$timestamp == null : this$timestamp.equals(other$timestamp);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof MemberApiParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getCode();
      Object $sign = this.getSign();
      result = result * 59 + ($sign == null ? 43 : $sign.hashCode());
      Object $data = this.getData();
      result = result * 59 + ($data == null ? 43 : $data.hashCode());
      Object $callbackParam = this.getCallbackParam();
      result = result * 59 + ($callbackParam == null ? 43 : $callbackParam.hashCode());
      Object $action = this.getAction();
      result = result * 59 + ($action == null ? 43 : $action.hashCode());
      Object $timestamp = this.getTimestamp();
      return result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
   }

   @Override
   public String toString() {
      return "MemberApiParam(code="
         + this.getCode()
         + ", sign="
         + this.getSign()
         + ", data="
         + this.getData()
         + ", callbackParam="
         + this.getCallbackParam()
         + ", action="
         + this.getAction()
         + ", timestamp="
         + this.getTimestamp()
         + ")";
   }
}
