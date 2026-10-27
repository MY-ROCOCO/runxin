package com.rxinyun.admin.system.modular.resource.param.button;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SysButtonEditParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "父id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "parentId不能为空"
   )
   private String parentId;
   @Schema(
      description = "标题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "title不能为空"
   )
   private String title;
   @Schema(
      description = "编码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "code不能为空"
   )
   private String code;
   @Schema(
      description = "排序码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;
   @Schema(
      description = "扩展JSON"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getParentId() {
      return this.parentId;
   }

   public String getTitle() {
      return this.title;
   }

   public String getCode() {
      return this.code;
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

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
