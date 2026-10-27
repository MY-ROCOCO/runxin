package com.rxinyun.member.workorder.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class WorkOrderAddParam {
   @NotBlank
   @Schema(
      description = "工单类型ID"
   )
   private String typeId;
   @NotBlank
   @Schema(
      description = "工单标题"
   )
   private String title;
   @NotBlank
   @Schema(
      description = "工单内容"
   )
   private String content;
   @Schema(
      description = "关联产品分类名：云服务器、独立服务器"
   )
   private String productName;
   @Schema(
      description = "关联产品ID，云服务器或独立服务器业务ID"
   )
   private String productId;

   public String getTypeId() {
      return this.typeId;
   }

   public String getTitle() {
      return this.title;
   }

   public String getContent() {
      return this.content;
   }

   public String getProductName() {
      return this.productName;
   }

   public String getProductId() {
      return this.productId;
   }

   public void setTypeId(final String typeId) {
      this.typeId = typeId;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setProductName(final String productName) {
      this.productName = productName;
   }

   public void setProductId(final String productId) {
      this.productId = productId;
   }
}
