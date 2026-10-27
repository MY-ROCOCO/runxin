package com.rxinyun.member.workorder.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class WorkOrderTypeList {
   @Schema(
      description = "工单类型ID"
   )
   private String id;
   @Schema(
      description = "工单类型名称"
   )
   private String typeName;
   @Schema(
      description = "工单是否关联产品  (0:不关联(默认)  ,1:关联 ) "
   )
   private Integer isRelevance;

   public String getId() {
      return this.id;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public Integer getIsRelevance() {
      return this.isRelevance;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTypeName(final String typeName) {
      this.typeName = typeName;
   }

   public void setIsRelevance(final Integer isRelevance) {
      this.isRelevance = isRelevance;
   }
}
