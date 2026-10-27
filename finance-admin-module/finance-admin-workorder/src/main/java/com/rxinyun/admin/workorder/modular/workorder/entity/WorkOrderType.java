package com.rxinyun.admin.workorder.modular.workorder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "WORK_ORDER_TYPE",
   autoResultMap = true
)
public class WorkOrderType {
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
   @Schema(
      description = "工单类型状态  (0:不可见(默认) , 1:可见)"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getTypeName() {
      return this.typeName;
   }

   public Integer getIsRelevance() {
      return this.isRelevance;
   }

   public Integer getStatus() {
      return this.status;
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

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
