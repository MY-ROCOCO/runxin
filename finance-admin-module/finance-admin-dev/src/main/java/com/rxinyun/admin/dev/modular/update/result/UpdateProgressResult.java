package com.rxinyun.admin.dev.modular.update.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class UpdateProgressResult {
   @Schema(
      description = "下载进度（0-100）"
   )
   private int progress;
   @Schema(
      description = "类型: 1-下载 2-升级"
   )
   private Integer type;
   @Schema(
      description = "当前正在下载的项目：FRONTEND, BACKEND, ADMIN_FRONTEND"
   )
   private String currentDownloading;
   @Schema(
      description = "未开始 NOT_STARTED, 下载中 DOWNLOADING, 下载完成 COMPLETED, 下载失败 FAILED, 空闲中 IDLE"
   )
   private String status;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;

   public static UpdateProgressResult.UpdateProgressResultBuilder builder() {
      return new UpdateProgressResult.UpdateProgressResultBuilder();
   }

   public int getProgress() {
      return this.progress;
   }

   public Integer getType() {
      return this.type;
   }

   public String getCurrentDownloading() {
      return this.currentDownloading;
   }

   public String getStatus() {
      return this.status;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public void setProgress(final int progress) {
      this.progress = progress;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setCurrentDownloading(final String currentDownloading) {
      this.currentDownloading = currentDownloading;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UpdateProgressResult other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.getProgress() != other.getProgress()) {
         return false;
      } else {
         Object this$type = this.getType();
         Object other$type = other.getType();
         if (this$type == null ? other$type == null : this$type.equals(other$type)) {
            Object this$currentDownloading = this.getCurrentDownloading();
            Object other$currentDownloading = other.getCurrentDownloading();
            if (this$currentDownloading == null ? other$currentDownloading == null : this$currentDownloading.equals(other$currentDownloading)) {
               Object this$status = this.getStatus();
               Object other$status = other.getStatus();
               if (this$status == null ? other$status == null : this$status.equals(other$status)) {
                  Object this$errorMessage = this.getErrorMessage();
                  Object other$errorMessage = other.getErrorMessage();
                  return this$errorMessage == null ? other$errorMessage == null : this$errorMessage.equals(other$errorMessage);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof UpdateProgressResult;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getProgress();
      Object $type = this.getType();
      result = result * 59 + ($type == null ? 43 : $type.hashCode());
      Object $currentDownloading = this.getCurrentDownloading();
      result = result * 59 + ($currentDownloading == null ? 43 : $currentDownloading.hashCode());
      Object $status = this.getStatus();
      result = result * 59 + ($status == null ? 43 : $status.hashCode());
      Object $errorMessage = this.getErrorMessage();
      return result * 59 + ($errorMessage == null ? 43 : $errorMessage.hashCode());
   }

   @Override
   public String toString() {
      return "UpdateProgressResult(progress="
         + this.getProgress()
         + ", type="
         + this.getType()
         + ", currentDownloading="
         + this.getCurrentDownloading()
         + ", status="
         + this.getStatus()
         + ", errorMessage="
         + this.getErrorMessage()
         + ")";
   }

   public UpdateProgressResult() {
   }

   public UpdateProgressResult(final int progress, final Integer type, final String currentDownloading, final String status, final String errorMessage) {
      this.progress = progress;
      this.type = type;
      this.currentDownloading = currentDownloading;
      this.status = status;
      this.errorMessage = errorMessage;
   }

   public static class UpdateProgressResultBuilder {
      private int progress;
      private Integer type;
      private String currentDownloading;
      private String status;
      private String errorMessage;

      UpdateProgressResultBuilder() {
      }

      public UpdateProgressResult.UpdateProgressResultBuilder progress(final int progress) {
         this.progress = progress;
         return this;
      }

      public UpdateProgressResult.UpdateProgressResultBuilder type(final Integer type) {
         this.type = type;
         return this;
      }

      public UpdateProgressResult.UpdateProgressResultBuilder currentDownloading(final String currentDownloading) {
         this.currentDownloading = currentDownloading;
         return this;
      }

      public UpdateProgressResult.UpdateProgressResultBuilder status(final String status) {
         this.status = status;
         return this;
      }

      public UpdateProgressResult.UpdateProgressResultBuilder errorMessage(final String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      public UpdateProgressResult build() {
         return new UpdateProgressResult(this.progress, this.type, this.currentDownloading, this.status, this.errorMessage);
      }

      @Override
      public String toString() {
         return "UpdateProgressResult.UpdateProgressResultBuilder(progress="
            + this.progress
            + ", type="
            + this.type
            + ", currentDownloading="
            + this.currentDownloading
            + ", status="
            + this.status
            + ", errorMessage="
            + this.errorMessage
            + ")";
      }
   }
}
