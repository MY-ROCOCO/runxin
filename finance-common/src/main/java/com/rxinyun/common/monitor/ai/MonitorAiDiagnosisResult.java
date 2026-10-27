package com.rxinyun.common.monitor.ai;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

@Schema(
   description = "云服务器监控AI诊断结果"
)
public class MonitorAiDiagnosisResult {
   @Schema(
      description = "风险等级：normal/warning/critical"
   )
   private String level = "normal";
   @Schema(
      description = "诊断摘要"
   )
   private String summary = "";
   @Schema(
      description = "异常点"
   )
   private List<String> anomalies = new ArrayList<>();
   @Schema(
      description = "处理建议"
   )
   private List<String> suggestions = new ArrayList<>();
   @Schema(
      description = "生成时间"
   )
   private String generatedAt = "";

   public String getLevel() {
      return this.level;
   }

   public String getSummary() {
      return this.summary;
   }

   public List<String> getAnomalies() {
      return this.anomalies;
   }

   public List<String> getSuggestions() {
      return this.suggestions;
   }

   public String getGeneratedAt() {
      return this.generatedAt;
   }

   public void setLevel(final String level) {
      this.level = level;
   }

   public void setSummary(final String summary) {
      this.summary = summary;
   }

   public void setAnomalies(final List<String> anomalies) {
      this.anomalies = anomalies;
   }

   public void setSuggestions(final List<String> suggestions) {
      this.suggestions = suggestions;
   }

   public void setGeneratedAt(final String generatedAt) {
      this.generatedAt = generatedAt;
   }
}
