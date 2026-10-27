package com.rxinyun.admin.workorder.modular.workorder.entity.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class TypeRelevanceParam {
   @Schema(
      description = "工单ID"
   )
   private String id;
   @Schema(
      description = "工单是否关联产品 传入当前正确状态 "
   )
   private Integer isRelevance;

   public String getId() {
      return this.id;
   }

   public Integer getIsRelevance() {
      return this.isRelevance;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setIsRelevance(final Integer isRelevance) {
      this.isRelevance = isRelevance;
   }
}
