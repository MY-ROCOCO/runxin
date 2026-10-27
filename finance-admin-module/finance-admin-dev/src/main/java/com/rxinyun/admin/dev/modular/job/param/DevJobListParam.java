package com.rxinyun.admin.dev.modular.job.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevJobListParam {
   @Schema(
      description = "任务分类"
   )
   private String category;
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;
   @Schema(
      description = "任务状态"
   )
   private String jobStatus;

   public String getCategory() {
      return this.category;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getJobStatus() {
      return this.jobStatus;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setJobStatus(final String jobStatus) {
      this.jobStatus = jobStatus;
   }
}
