package com.rxinyun.admin.system.modular.position.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("SYS_POSITION")
public class SysPosition extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "组织id"
   )
   private String orgId;
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

   public String getOrgId() {
      return this.orgId;
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

   public void setOrgId(final String orgId) {
      this.orgId = orgId;
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
