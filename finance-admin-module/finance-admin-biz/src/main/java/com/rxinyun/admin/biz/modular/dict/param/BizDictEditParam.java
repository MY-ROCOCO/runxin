package com.rxinyun.admin.biz.modular.dict.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BizDictEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "字典文字",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "dictLabel不能为空"
   )
   private String dictLabel;
   @Schema(
      description = "排序码",
      requiredMode = RequiredMode.REQUIRED
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

   public String getDictLabel() {
      return this.dictLabel;
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

   public void setDictLabel(final String dictLabel) {
      this.dictLabel = dictLabel;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
