package com.rxinyun.member.order.modular.invoice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class MemberOrderInvoiceApplyParam {
   @Schema(
      description = "勾选的订单号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotEmpty(
      message = "请至少选择一笔订单"
   )
   private List<String> orderNos;
   @Schema(
      description = "发票抬头",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "发票抬头不能为空"
   )
   private String title;
   @Schema(
      description = "税号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "税号不能为空"
   )
   private String taxNo;
   @Schema(
      description = "接收邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "接收邮箱不能为空"
   )
   @Email(
      message = "接收邮箱格式不正确"
   )
   private String email;

   public List<String> getOrderNos() {
      return this.orderNos;
   }

   public String getTitle() {
      return this.title;
   }

   public String getTaxNo() {
      return this.taxNo;
   }

   public String getEmail() {
      return this.email;
   }

   public void setOrderNos(final List<String> orderNos) {
      this.orderNos = orderNos;
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
