package com.rxinyun.admin.system.modular.user.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class SysUserMessageResult {
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
   @Schema(
      description = "是否已读"
   )
   private Boolean read;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "创建人"
   )
   private String createUser;
   @Schema(
      description = "更新时间"
   )
   private Date updateTime;
   @Schema(
      description = "更新人"
   )
   private String updateUser;

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

   public Boolean getRead() {
      return this.read;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public String getCreateUser() {
      return this.createUser;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public String getUpdateUser() {
      return this.updateUser;
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

   public void setRead(final Boolean read) {
      this.read = read;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setCreateUser(final String createUser) {
      this.createUser = createUser;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setUpdateUser(final String updateUser) {
      this.updateUser = updateUser;
   }
}
