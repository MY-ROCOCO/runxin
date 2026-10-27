package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ToolsNotificationPageParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：userName"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "发送类型(1:用户,2:管理员)"
   )
   private Integer targetType;
   @Schema(
      description = "通知类型"
   )
   private String type;
   @Schema(
      description = "配置类型：VERIFY_CODE=验证码类型，NOTICE=通知类型"
   )
   private String configType;

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

   public Integer getTargetType() {
      return this.targetType;
   }

   public String getType() {
      return this.type;
   }

   public String getConfigType() {
      return this.configType;
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

   public void setTargetType(final Integer targetType) {
      this.targetType = targetType;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setConfigType(final String configType) {
      this.configType = configType;
   }
}
