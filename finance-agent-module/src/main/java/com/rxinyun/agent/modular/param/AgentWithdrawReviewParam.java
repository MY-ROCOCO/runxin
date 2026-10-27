package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class AgentWithdrawReviewParam {
   @Schema(
      description = "提现记录ID",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "提现记录ID不能为空"
   )
   private String id;
   @Schema(
      description = "审核结果：APPROVED/REJECTED",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "审核结果不能为空"
   )
   private String status;
   @Schema(
      description = "审核备注"
   )
   private String reviewRemark;

   public String getId() {
      return this.id;
   }

   public String getStatus() {
      return this.status;
   }

   public String getReviewRemark() {
      return this.reviewRemark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setReviewRemark(final String reviewRemark) {
      this.reviewRemark = reviewRemark;
   }
}
