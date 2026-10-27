package com.rxinyun.common.monitor.ai;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "云服务器监控AI对话结果"
)
public class MonitorAiChatResult {
   @Schema(
      description = "AI回复内容"
   )
   private String answer = "";
   @Schema(
      description = "生成时间"
   )
   private String generatedAt = "";

   public String getAnswer() {
      return this.answer;
   }

   public String getGeneratedAt() {
      return this.generatedAt;
   }

   public void setAnswer(final String answer) {
      this.answer = answer;
   }

   public void setGeneratedAt(final String generatedAt) {
      this.generatedAt = generatedAt;
   }
}
