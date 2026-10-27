package com.rxinyun.member.production.modular.domain.template.service;

public class XinwangSubmitResult {
   private boolean success;
   private String code;
   private String msg;
   private String requestId;

   public boolean isSuccess() {
      return this.success;
   }

   public String getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public void setSuccess(final boolean success) {
      this.success = success;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }

   public void setRequestId(final String requestId) {
      this.requestId = requestId;
   }
}
