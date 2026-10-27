package com.rxinyun.admin.system.modular.relation.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("SYS_RELATION")
public class SysRelation {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "对象id"
   )
   private String objectId;
   @Schema(
      description = "目标id"
   )
   private String targetId;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "扩展信息"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getObjectId() {
      return this.objectId;
   }

   public String getTargetId() {
      return this.targetId;
   }

   public String getCategory() {
      return this.category;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setObjectId(final String objectId) {
      this.objectId = objectId;
   }

   public void setTargetId(final String targetId) {
      this.targetId = targetId;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
