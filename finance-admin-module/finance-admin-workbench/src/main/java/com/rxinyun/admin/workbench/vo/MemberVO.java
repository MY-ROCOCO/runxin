package com.rxinyun.admin.workbench.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberVO {
   @Schema(
      description = "注册量"
   )
   private Long register;
   @Schema(
      description = "成交用户数量"
   )
   private Long deal;
   @Schema(
      description = "实名认证数量"
   )
   private Long real;

   public Long getRegister() {
      return this.register;
   }

   public Long getDeal() {
      return this.deal;
   }

   public Long getReal() {
      return this.real;
   }

   public void setRegister(final Long register) {
      this.register = register;
   }

   public void setDeal(final Long deal) {
      this.deal = deal;
   }

   public void setReal(final Long real) {
      this.real = real;
   }
}
