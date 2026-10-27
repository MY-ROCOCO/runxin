package com.rxinyun.member.tools.modular.config.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("DEV_CONFIG")
public class MemberConfig extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "配置键"
   )
   private String configKey;
   @Schema(
      description = "配置值"
   )
   private String configValue;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "备注"
   )
   private String remark;
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

   public String getConfigKey() {
      return this.configKey;
   }

   public String getConfigValue() {
      return this.configValue;
   }

   public String getCategory() {
      return this.category;
   }

   public String getRemark() {
      return this.remark;
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

   public void setConfigKey(final String configKey) {
      this.configKey = configKey;
   }

   public void setConfigValue(final String configValue) {
      this.configValue = configValue;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
