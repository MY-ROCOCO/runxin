package com.rxinyun.admin.system.modular.index.result;

public class SysOpDataCountResult {
   private Long jobCount;
   private Long sysDictCount;
   private Long bizDictCount;
   private Long backUserSessionCount;
   private Long clientUserSessionCount;
   private Long thirdUserCount;

   public Long getJobCount() {
      return this.jobCount;
   }

   public Long getSysDictCount() {
      return this.sysDictCount;
   }

   public Long getBizDictCount() {
      return this.bizDictCount;
   }

   public Long getBackUserSessionCount() {
      return this.backUserSessionCount;
   }

   public Long getClientUserSessionCount() {
      return this.clientUserSessionCount;
   }

   public Long getThirdUserCount() {
      return this.thirdUserCount;
   }

   public void setJobCount(final Long jobCount) {
      this.jobCount = jobCount;
   }

   public void setSysDictCount(final Long sysDictCount) {
      this.sysDictCount = sysDictCount;
   }

   public void setBizDictCount(final Long bizDictCount) {
      this.bizDictCount = bizDictCount;
   }

   public void setBackUserSessionCount(final Long backUserSessionCount) {
      this.backUserSessionCount = backUserSessionCount;
   }

   public void setClientUserSessionCount(final Long clientUserSessionCount) {
      this.clientUserSessionCount = clientUserSessionCount;
   }

   public void setThirdUserCount(final Long thirdUserCount) {
      this.thirdUserCount = thirdUserCount;
   }
}
