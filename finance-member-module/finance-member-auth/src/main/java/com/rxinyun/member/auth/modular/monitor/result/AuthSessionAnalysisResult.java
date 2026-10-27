package com.rxinyun.member.auth.modular.monitor.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthSessionAnalysisResult {
   @Schema(
      description = "当前会话总数量"
   )
   private String currentSessionTotalCount;
   @Schema(
      description = "最大签发令牌数"
   )
   private String maxTokenCount;
   @Schema(
      description = "最近1小时会话数"
   )
   private String oneHourNewlyAdded;
   @Schema(
      description = "BC端会话比例"
   )
   private String proportionOfBAndC;

   public String getCurrentSessionTotalCount() {
      return this.currentSessionTotalCount;
   }

   public String getMaxTokenCount() {
      return this.maxTokenCount;
   }

   public String getOneHourNewlyAdded() {
      return this.oneHourNewlyAdded;
   }

   public String getProportionOfBAndC() {
      return this.proportionOfBAndC;
   }

   public void setCurrentSessionTotalCount(final String currentSessionTotalCount) {
      this.currentSessionTotalCount = currentSessionTotalCount;
   }

   public void setMaxTokenCount(final String maxTokenCount) {
      this.maxTokenCount = maxTokenCount;
   }

   public void setOneHourNewlyAdded(final String oneHourNewlyAdded) {
      this.oneHourNewlyAdded = oneHourNewlyAdded;
   }

   public void setProportionOfBAndC(final String proportionOfBAndC) {
      this.proportionOfBAndC = proportionOfBAndC;
   }
}
