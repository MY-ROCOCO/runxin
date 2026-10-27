package com.rxinyun.admin.dev.modular.job.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("DEV_JOB")
public class DevJob extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
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
      description = "任务类名"
   )
   private String actionClass;
   @Schema(
      description = "cron表达式"
   )
   private String cronExpression;
   @Schema(
      description = "任务状态"
   )
   private String jobStatus;
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

   public String getName() {
      return this.name;
   }

   public String getCode() {
      return this.code;
   }

   public String getCategory() {
      return this.category;
   }

   public String getActionClass() {
      return this.actionClass;
   }

   public String getCronExpression() {
      return this.cronExpression;
   }

   public String getJobStatus() {
      return this.jobStatus;
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

   public void setName(final String name) {
      this.name = name;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setActionClass(final String actionClass) {
      this.actionClass = actionClass;
   }

   public void setCronExpression(final String cronExpression) {
      this.cronExpression = cronExpression;
   }

   public void setJobStatus(final String jobStatus) {
      this.jobStatus = jobStatus;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
