package com.rxinyun.admin.biz.modular.dict.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("DEV_DICT")
public class BizDict extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "父id"
   )
   private String parentId;
   @Schema(
      description = "字典文字"
   )
   private String dictLabel;
   @Schema(
      description = "字典值"
   )
   private String dictValue;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "排序码"
   )
   private Integer sortCode;
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

   public String getParentId() {
      return this.parentId;
   }

   public String getDictLabel() {
      return this.dictLabel;
   }

   public String getDictValue() {
      return this.dictValue;
   }

   public String getCategory() {
      return this.category;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setParentId(final String parentId) {
      this.parentId = parentId;
   }

   public void setDictLabel(final String dictLabel) {
      this.dictLabel = dictLabel;
   }

   public void setDictValue(final String dictValue) {
      this.dictValue = dictValue;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
