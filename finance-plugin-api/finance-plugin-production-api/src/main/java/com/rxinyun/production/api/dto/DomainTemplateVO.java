package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

@Schema(
   description = "域名模板信息"
)
public class DomainTemplateVO implements Serializable {
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
      description = "状态：0 禁用 1 启用"
   )
   private Integer status;
   @Schema(
      description = "审核状态：0 未提交 1 审核中 2 审核通过 3 审核拒绝"
   )
   private Integer auditStatus;
   @Schema(
      description = "审核失败原因"
   )
   private String auditReason;
   @Schema(
      description = "扩展信息 JSON"
   )
   private String extJson;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "更新时间"
   )
   private Date updateTime;

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

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
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

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public DomainTemplateVO() {
   }

   public DomainTemplateVO(
      final String id,
      final String memberId,
      final String name,
      final Integer status,
      final Integer auditStatus,
      final String auditReason,
      final String extJson,
      final Date createTime,
      final Date updateTime
   ) {
      this.id = id;
      this.memberId = memberId;
      this.name = name;
      this.status = status;
      this.auditStatus = auditStatus;
      this.auditReason = auditReason;
      this.extJson = extJson;
      this.createTime = createTime;
      this.updateTime = updateTime;
   }
}
