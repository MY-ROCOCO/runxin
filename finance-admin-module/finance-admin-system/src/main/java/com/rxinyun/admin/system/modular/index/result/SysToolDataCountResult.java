package com.rxinyun.admin.system.modular.index.result;

public class SysToolDataCountResult {
   private Long fileCount;
   private Long smsCount;
   private Long emailCount;
   private Long messageCount;

   public Long getFileCount() {
      return this.fileCount;
   }

   public Long getSmsCount() {
      return this.smsCount;
   }

   public Long getEmailCount() {
      return this.emailCount;
   }

   public Long getMessageCount() {
      return this.messageCount;
   }

   public void setFileCount(final Long fileCount) {
      this.fileCount = fileCount;
   }

   public void setSmsCount(final Long smsCount) {
      this.smsCount = smsCount;
   }

   public void setEmailCount(final Long emailCount) {
      this.emailCount = emailCount;
   }

   public void setMessageCount(final Long messageCount) {
      this.messageCount = messageCount;
   }
}
