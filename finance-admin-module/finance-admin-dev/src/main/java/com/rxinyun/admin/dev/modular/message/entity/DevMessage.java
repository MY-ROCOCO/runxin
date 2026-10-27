package com.rxinyun.admin.dev.modular.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("DEV_MESSAGE")
public class DevMessage extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "主题"
   )
   private String subject;
   @Schema(
      description = "正文"
   )
   private String content;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getCategory() {
      return this.category;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getContent() {
      return this.content;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setSubject(final String subject) {
      this.subject = subject;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
