package com.rxinyun.production.api.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "后台域名模板分页参数"
)
public class AdminDomainTemplatePageParam implements Serializable {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段"
   )
   private String sortField;
   @Schema(
      description = "排序方式：ASCEND/DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "关键词，匹配模板名称"
   )
   private String searchKey;
   @Schema(
      description = "会员ID，可选"
   )
   private String memberId;
   @Schema(
      description = "审核状态：0 未提交 1 审核中 2 审核通过 3 审核拒绝，可选"
   )
   private Integer auditStatus;

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public Integer getAuditStatus() {
      return this.auditStatus;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setAuditStatus(final Integer auditStatus) {
      this.auditStatus = auditStatus;
   }
}
