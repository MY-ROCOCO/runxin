package com.rxinyun.admin.system.modular.resource.param.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SysMenuAddParam {
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
      description = "菜单类型",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "menuType不能为空"
   )
   private String menuType;
   @Schema(
      description = "模块"
   )
   private String module;
   @Schema(
      description = "路径",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "path不能为空"
   )
   private String path;
   @Schema(
      description = "排序码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;
   @Schema(
      description = "别名"
   )
   private String name;
   @Schema(
      description = "组件"
   )
   private String component;
   @Schema(
      description = "图标"
   )
   private String icon;
   @Schema(
      description = "是否可见"
   )
   private String visible;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getParentId() {
      return this.parentId;
   }

   public String getTitle() {
      return this.title;
   }

   public String getMenuType() {
      return this.menuType;
   }

   public String getModule() {
      return this.module;
   }

   public String getPath() {
      return this.path;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public String getName() {
      return this.name;
   }

   public String getComponent() {
      return this.component;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getVisible() {
      return this.visible;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setParentId(final String parentId) {
      this.parentId = parentId;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setMenuType(final String menuType) {
      this.menuType = menuType;
   }

   public void setModule(final String module) {
      this.module = module;
   }

   public void setPath(final String path) {
      this.path = path;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setComponent(final String component) {
      this.component = component;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setVisible(final String visible) {
      this.visible = visible;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
