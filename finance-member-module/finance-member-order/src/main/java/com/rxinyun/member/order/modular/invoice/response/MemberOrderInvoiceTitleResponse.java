package com.rxinyun.member.order.modular.invoice.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberOrderInvoiceTitleResponse {
   @Schema(
      description = "发票抬头"
   )
   private String title;
   @Schema(
      description = "税号"
   )
   private String taxNo;
   @Schema(
      description = "接收邮箱"
   )
   private String email;

   public String getTitle() {
      return this.title;
   }

   public String getTaxNo() {
      return this.taxNo;
   }

   public String getEmail() {
      return this.email;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setTaxNo(final String taxNo) {
      this.taxNo = taxNo;
   }

   public void setEmail(final String email) {
      this.email = email;
   }
}
