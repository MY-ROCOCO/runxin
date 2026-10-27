package com.rxinyun.admin.system.modular.org.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("SYS_ORG")
public class SysOrg extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "父id"
   )
   private String parentId;
   @Schema(
      description = "主管id"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String directorId;
   @Schema(
      description = "名称"
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

   public String getDirectorId() {
      return this.directorId;
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

   public void setDirectorId(final String directorId) {
      this.directorId = directorId;
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

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
