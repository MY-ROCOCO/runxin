package com.rxinyun.admin.business.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DnsRecordParam {
   public static class IdParam {
      @NotBlank(
         message = "id不能为空"
      )
      @Schema(
         description = "解析记录ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class PageParam {
      @Schema(
         description = "当前页码"
      )
      private Integer current;
      @Schema(
         description = "每页条数"
      )
      private Integer size;
      @Schema(
         description = "排序字段，字段驼峰名称"
      )
      private String sortField;
      @Schema(
         description = "排序方式，升序：ASCEND；降序：DESCEND"
      )
      private String sortOrder;
      @Schema(
         description = "关键词，匹配主机记录、完整域名、记录值"
      )
      private String searchKey;
      @Schema(
         description = "关联托管域名ID"
      )
      private String zoneId;
      @Schema(
         description = "记录类型"
      )
      private String type;
      @Schema(
         description = "是否启用：1启用 0暂停"
      )
      private Integer enabled;

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

      public String getZoneId() {
         return this.zoneId;
      }

      public String getType() {
         return this.type;
      }

      public Integer getEnabled() {
         return this.enabled;
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

      public void setZoneId(final String zoneId) {
         this.zoneId = zoneId;
      }

      public void setType(final String type) {
         this.type = type;
      }

      public void setEnabled(final Integer enabled) {
         this.enabled = enabled;
      }
   }

   public static class StatusParam extends DnsRecordParam.IdParam {
      @NotNull(
         message = "启用状态不能为空"
      )
      @Schema(
         description = "是否启用：1启用 0暂停",
         requiredMode = RequiredMode.REQUIRED
      )
      private Integer enabled;

      public Integer getEnabled() {
         return this.enabled;
      }

      public void setEnabled(final Integer enabled) {
         this.enabled = enabled;
      }
   }
}
