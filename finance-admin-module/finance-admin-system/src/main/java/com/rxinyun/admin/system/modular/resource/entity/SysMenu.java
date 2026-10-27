package com.rxinyun.admin.system.modular.resource.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("SYS_RESOURCE")
public class SysMenu extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "父id"
   )
   private String parentId;
   @Schema(
      description = "标题"
   )
   private String title;
   @Schema(
      description = "别名"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String name;
   @Schema(
      description = "编码"
   )
   private String code;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "模块"
   )
   private String module;
   @Schema(
      description = "菜单类型"
   )
   private String menuType;
   @Schema(
      description = "路径"
   )
   private String path;
   @Schema(
      description = "组件"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String component;
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   @Schema(
      description = "图标"
   )
   private String icon;
   @Schema(
      description = "颜色"
   )
   private String color;
   @Schema(
      description = "是否可见"
   )
   private String visible;
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

   public String getTitle() {
      return this.title;
   }

   public String getName() {
      return this.name;
   }

   public String getCode() {
      return this.code;
   }

   public String getCategory() {
      return this.category;
   }

   public String getModule() {
      return this.module;
   }

   public String getMenuType() {
      return this.menuType;
   }

   public String getPath() {
      return this.path;
   }

   public String getComponent() {
      return this.component;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getColor() {
      return this.color;
   }

   public String getVisible() {
      return this.visible;
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

   public void setName(final String name) {
      this.name = name;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setModule(final String module) {
      this.module = module;
   }

   public void setMenuType(final String menuType) {
      this.menuType = menuType;
   }

   public void setPath(final String path) {
      this.path = path;
   }

   public void setComponent(final String component) {
      this.component = component;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setColor(final String color) {
      this.color = color;
   }

   public void setVisible(final String visible) {
      this.visible = visible;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
