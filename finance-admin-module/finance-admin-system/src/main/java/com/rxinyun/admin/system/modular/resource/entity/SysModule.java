package com.rxinyun.admin.system.modular.resource.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("SYS_RESOURCE")
public class SysModule extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "标题"
   )
   private String title;
   @Schema(
      description = "编码"
   )
   private String code;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "图标"
   )
   private String icon;
   @Schema(
      description = "颜色"
   )
   private String color;
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

   public String getTitle() {
      return this.title;
   }

   public String getCode() {
      return this.code;
   }

   public String getCategory() {
      return this.category;
   }

   public String getIcon() {
      return this.icon;
   }

   public String getColor() {
      return this.color;
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

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setColor(final String color) {
      this.color = color;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
