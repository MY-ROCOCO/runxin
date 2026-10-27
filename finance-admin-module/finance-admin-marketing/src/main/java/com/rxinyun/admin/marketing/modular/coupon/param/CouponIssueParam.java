package com.rxinyun.admin.marketing.modular.coupon.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Schema(
   description = "优惠券发放参数"
)
public class CouponIssueParam {
   @NotBlank(
      message = "模板ID不能为空"
   )
   @Schema(
      description = "模板ID",
      requiredMode = RequiredMode.REQUIRED,
      example = "2039000000000000001"
   )
   private String templateId;
   @Schema(
      description = "是否发券给全部启用会员，true时忽略 memberNos",
      example = "false"
   )
   private Boolean issueToAll = false;
   @Schema(
      description = "会员编号列表，按指定会员发券时必传",
      example = "[10001,10002]"
   )
   private List<Long> memberNos;

   public String getTemplateId() {
      return this.templateId;
   }

   public Boolean getIssueToAll() {
      return this.issueToAll;
   }

   public List<Long> getMemberNos() {
      return this.memberNos;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setIssueToAll(final Boolean issueToAll) {
      this.issueToAll = issueToAll;
   }

   public void setMemberNos(final List<Long> memberNos) {
      this.memberNos = memberNos;
   }
}
