package com.rxinyun.member.production.modular.domain.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "DOMAIN_TEMPLATE",
   autoResultMap = true
)
public class DomainTemplate extends CommonEntity {
   @Schema(
      description = "ID"
   )
   private String id;
   @Schema(
      description = "会员ID"
   )
   private String memberId;
   @Schema(
      description = "模板名称"
   )
   private String name;
   @Schema(
      description = "状态：0 禁用 1 启用（本地是否启用）"
   )
   private Integer status;
   @Schema(
      description = "审核状态：0 未提交 1 审核中 2 审核通过 3 审核拒绝（与上游同步）"
   )
   private Integer auditStatus;
   @Schema(
      description = "审核失败原因（上游返回的 reason，审核通过时为空）"
   )
   private String auditReason;
   @Schema(
      description = "扩展信息，存新网创建模板参数字段 JSON"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public String getName() {
      return this.name;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getAuditStatus() {
      return this.auditStatus;
   }

   public String getAuditReason() {
      return this.auditReason;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setAuditStatus(final Integer auditStatus) {
      this.auditStatus = auditStatus;
   }

   public void setAuditReason(final String auditReason) {
      this.auditReason = auditReason;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
