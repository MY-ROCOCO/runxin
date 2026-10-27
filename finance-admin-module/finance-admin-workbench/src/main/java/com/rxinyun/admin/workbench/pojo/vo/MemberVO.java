package com.rxinyun.admin.workbench.pojo.vo;

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
      description = "个人实名认证数量"
   )
   private Long personReal;
   @Schema(
      description = "企业实名认证数量"
   )
   private Long companyReal;

   public Long getRegister() {
      return this.register;
   }

   public Long getDeal() {
      return this.deal;
   }

   public Long getPersonReal() {
      return this.personReal;
   }

   public Long getCompanyReal() {
      return this.companyReal;
   }

   public void setRegister(final Long register) {
      this.register = register;
   }

   public void setDeal(final Long deal) {
      this.deal = deal;
   }

   public void setPersonReal(final Long personReal) {
      this.personReal = personReal;
   }

   public void setCompanyReal(final Long companyReal) {
      this.companyReal = companyReal;
   }
}
