package com.rxinyun.member.business.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class MemberDnsResolveLogParam {
   public static class PageParam {
      @Schema(
         description = "搜索关键字，匹配托管域名、解析记录、记录值"
      )
      private String searchKey;
      @Schema(
         description = "托管域名ID"
      )
      private String zoneId;
      @Schema(
         description = "解析记录ID"
      )
      private String recordId;
      @Schema(
         description = "托管域名"
      )
      private String domainName;
      @Schema(
         description = "完整解析记录"
      )
      private String recordName;
      @Schema(
         description = "记录类型"
      )
      private String recordType;
      @Schema(
         description = "操作类型：ADD新增 EDIT编辑 STATUS状态 DELETE删除"
      )
      private String operateType;
      @Schema(
         description = "开始操作时间"
      )
      private Date startTime;
      @Schema(
         description = "结束操作时间"
      )
      private Date endTime;
      @Schema(
         description = "排序字段"
      )
      private String sortField;
      @Schema(
         description = "排序方式，升序：ASC，降序：DESC"
      )
      private String sortOrder;

      public String getSearchKey() {
         return this.searchKey;
      }

      public String getZoneId() {
         return this.zoneId;
      }

      public String getRecordId() {
         return this.recordId;
      }

      public String getDomainName() {
         return this.domainName;
      }

      public String getRecordName() {
         return this.recordName;
      }

      public String getRecordType() {
         return this.recordType;
      }

      public String getOperateType() {
         return this.operateType;
      }

      public Date getStartTime() {
         return this.startTime;
      }

      public Date getEndTime() {
         return this.endTime;
      }

      public String getSortField() {
         return this.sortField;
      }

      public String getSortOrder() {
         return this.sortOrder;
      }

      public void setSearchKey(final String searchKey) {
         this.searchKey = searchKey;
      }

      public void setZoneId(final String zoneId) {
         this.zoneId = zoneId;
      }

      public void setRecordId(final String recordId) {
         this.recordId = recordId;
      }

      public void setDomainName(final String domainName) {
         this.domainName = domainName;
      }

      public void setRecordName(final String recordName) {
         this.recordName = recordName;
      }

      public void setRecordType(final String recordType) {
         this.recordType = recordType;
      }

      public void setOperateType(final String operateType) {
         this.operateType = operateType;
      }

      public void setStartTime(final Date startTime) {
         this.startTime = startTime;
      }

      public void setEndTime(final Date endTime) {
         this.endTime = endTime;
      }

      public void setSortField(final String sortField) {
         this.sortField = sortField;
      }

      public void setSortOrder(final String sortOrder) {
         this.sortOrder = sortOrder;
      }
   }
}
