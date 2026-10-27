package com.rxinyun.admin.member.modular.real.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;

public class CompanyRealInfoAuditParam {
   @Schema(
      description = "会员编号",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "会员编号不能为空"
   )
   private Long memberNo;
   @Schema(
      description = "审核状态：APPROVED-通过, REJECTED-拒绝",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "审核状态不能为空"
   )
   private String status;
   @Schema(
      description = "拒绝原因，状态为 REJECTED 时填写"
   )
   private String reason;

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getStatus() {
      return this.status;
   }

   public String getReason() {
      return this.reason;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setReason(final String reason) {
      this.reason = reason;
   }
}
