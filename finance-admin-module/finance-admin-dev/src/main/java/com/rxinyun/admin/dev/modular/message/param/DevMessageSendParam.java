package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class DevMessageSendParam {
   @Schema(
      description = "主题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "subject不能为空"
   )
   private String subject;
   @Schema(
      description = "接收人id集合",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotEmpty(
      message = "receiverIdList不能为空"
   )
   private List<String> receiverIdList;
   @Schema(
      description = "正文"
   )
   private String content;
   @Schema(
      description = "分类",
      hidden = true
   )
   @NotBlank(
      message = "category不能为空"
   )
   private String category;

   public String getSubject() {
      return this.subject;
   }

   public List<String> getReceiverIdList() {
      return this.receiverIdList;
   }

   public String getContent() {
      return this.content;
   }

   public String getCategory() {
      return this.category;
   }

   public void setSubject(final String subject) {
      this.subject = subject;
   }

   public void setReceiverIdList(final List<String> receiverIdList) {
      this.receiverIdList = receiverIdList;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setCategory(final String category) {
      this.category = category;
   }
}
