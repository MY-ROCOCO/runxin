package com.rxinyun.admin.dev.modular.dict.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DevDictEditParam {
   @Schema(
      description = "id"
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "父id"
   )
   @NotBlank(
      message = "parentId不能为空"
   )
   private String parentId;
   @Schema(
      description = "字典文字"
   )
   @NotBlank(
      message = "dictLabel不能为空"
   )
   private String dictLabel;
   @Schema(
      description = "字典值"
   )
   @NotBlank(
      message = "dictValue不能为空"
   )
   private String dictValue;
   @Schema(
      description = "分类"
   )
   @NotBlank(
      message = "category不能为空"
   )
   private String category;
   @Schema(
      description = "排序码"
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展信息"
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
