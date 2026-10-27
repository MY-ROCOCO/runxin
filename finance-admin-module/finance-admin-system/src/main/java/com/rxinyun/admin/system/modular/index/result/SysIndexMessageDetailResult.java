package com.rxinyun.admin.system.modular.index.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class SysIndexMessageDetailResult {
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
      description = "接收信息集合"
   )
   private List<SysIndexMessageDetailResult.DevReceiveInfo> receiveInfoList;

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

   public List<SysIndexMessageDetailResult.DevReceiveInfo> getReceiveInfoList() {
      return this.receiveInfoList;
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

   public void setReceiveInfoList(final List<SysIndexMessageDetailResult.DevReceiveInfo> receiveInfoList) {
      this.receiveInfoList = receiveInfoList;
   }

   public static class DevReceiveInfo {
      @Schema(
         description = "接收人ID"
      )
      private String receiveUserId;
      @Schema(
         description = "接收人姓名"
      )
      private String receiveUserName;
      @Schema(
         description = "是否已读"
      )
      private Boolean read;

      public String getReceiveUserId() {
         return this.receiveUserId;
      }

      public String getReceiveUserName() {
         return this.receiveUserName;
      }

      public Boolean getRead() {
         return this.read;
      }

      public void setReceiveUserId(final String receiveUserId) {
         this.receiveUserId = receiveUserId;
      }

      public void setReceiveUserName(final String receiveUserName) {
         this.receiveUserName = receiveUserName;
      }

      public void setRead(final Boolean read) {
         this.read = read;
      }
   }
}
