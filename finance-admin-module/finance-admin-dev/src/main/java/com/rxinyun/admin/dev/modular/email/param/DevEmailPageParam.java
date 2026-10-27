package com.rxinyun.admin.dev.modular.email.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevEmailPageParam {
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
      description = "邮件引擎"
   )
   private String engine;
   @Schema(
      description = "发送状态：PENDING-待发送/SENDING-发送中/SUCCESS-发送成功/FAILED-发送失败"
   )
   private String sendStatus;
   @Schema(
      description = "邮件主题关键词"
   )
   private String searchKey;

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

   public String getEngine() {
      return this.engine;
   }

   public String getSendStatus() {
      return this.sendStatus;
   }

   public String getSearchKey() {
      return this.searchKey;
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

   public void setEngine(final String engine) {
      this.engine = engine;
   }

   public void setSendStatus(final String sendStatus) {
      this.sendStatus = sendStatus;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
