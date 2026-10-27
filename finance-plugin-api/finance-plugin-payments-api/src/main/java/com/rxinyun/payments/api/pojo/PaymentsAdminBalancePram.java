package com.rxinyun.payments.api.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class PaymentsAdminBalancePram {
   @NotBlank
   @Schema(
      description = "是否增加 0扣减额度 1增加额度 "
   )
   private Integer isAdd;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String adminPassword;
   @NotBlank
   @Schema(
      description = "会员编号"
   )
   private String memberNo;
   @NotBlank
   @Schema(
      description = "核对会员的账号"
   )
   private String memberAccount;
   @NotBlank
   @Schema(
      description = "添加/扣减金额"
   )
   private BigDecimal payAmount;
   @NotBlank
   @Schema(
      description = "备注"
   )
   private String remark;

   public Integer getIsAdd() {
      return this.isAdd;
   }

   public String getAdminPassword() {
      return this.adminPassword;
   }

   public String getMemberNo() {
      return this.memberNo;
   }

   public String getMemberAccount() {
      return this.memberAccount;
   }

   public BigDecimal getPayAmount() {
      return this.payAmount;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setIsAdd(final Integer isAdd) {
      this.isAdd = isAdd;
   }

   public void setAdminPassword(final String adminPassword) {
      this.adminPassword = adminPassword;
   }

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setMemberAccount(final String memberAccount) {
      this.memberAccount = memberAccount;
   }

   public void setPayAmount(final BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
