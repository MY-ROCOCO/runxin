package com.rxinyun.admin.dev.modular.update.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "下载结果"
)
public class DownloadResult {
   @Schema(
      description = "是否下载成功"
   )
   private boolean success;
   @Schema(
      description = "下载文件路径"
   )
   private String filePath;
   @Schema(
      description = "文件类型"
   )
   private String fileType;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;

   DownloadResult(final boolean success, final String filePath, final String fileType, final String errorMessage) {
      this.success = success;
      this.filePath = filePath;
      this.fileType = fileType;
      this.errorMessage = errorMessage;
   }

   public static DownloadResult.DownloadResultBuilder builder() {
      return new DownloadResult.DownloadResultBuilder();
   }

   public boolean isSuccess() {
      return this.success;
   }

   public String getFilePath() {
      return this.filePath;
   }

   public String getFileType() {
      return this.fileType;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public void setSuccess(final boolean success) {
      this.success = success;
   }

   public void setFilePath(final String filePath) {
      this.filePath = filePath;
   }

   public void setFileType(final String fileType) {
      this.fileType = fileType;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DownloadResult other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isSuccess() != other.isSuccess()) {
         return false;
      } else {
         Object this$filePath = this.getFilePath();
         Object other$filePath = other.getFilePath();
         if (this$filePath == null ? other$filePath == null : this$filePath.equals(other$filePath)) {
            Object this$fileType = this.getFileType();
            Object other$fileType = other.getFileType();
            if (this$fileType == null ? other$fileType == null : this$fileType.equals(other$fileType)) {
               Object this$errorMessage = this.getErrorMessage();
               Object other$errorMessage = other.getErrorMessage();
               return this$errorMessage == null ? other$errorMessage == null : this$errorMessage.equals(other$errorMessage);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof DownloadResult;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isSuccess() ? 79 : 97);
      Object $filePath = this.getFilePath();
      result = result * 59 + ($filePath == null ? 43 : $filePath.hashCode());
      Object $fileType = this.getFileType();
      result = result * 59 + ($fileType == null ? 43 : $fileType.hashCode());
      Object $errorMessage = this.getErrorMessage();
      return result * 59 + ($errorMessage == null ? 43 : $errorMessage.hashCode());
   }

   @Override
   public String toString() {
      return "DownloadResult(success="
         + this.isSuccess()
         + ", filePath="
         + this.getFilePath()
         + ", fileType="
         + this.getFileType()
         + ", errorMessage="
         + this.getErrorMessage()
         + ")";
   }

   public static class DownloadResultBuilder {
      private boolean success;
      private String filePath;
      private String fileType;
      private String errorMessage;

      DownloadResultBuilder() {
      }

      public DownloadResult.DownloadResultBuilder success(final boolean success) {
         this.success = success;
         return this;
      }

      public DownloadResult.DownloadResultBuilder filePath(final String filePath) {
         this.filePath = filePath;
         return this;
      }

      public DownloadResult.DownloadResultBuilder fileType(final String fileType) {
         this.fileType = fileType;
         return this;
      }

      public DownloadResult.DownloadResultBuilder errorMessage(final String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      public DownloadResult build() {
         return new DownloadResult(this.success, this.filePath, this.fileType, this.errorMessage);
      }

      @Override
      public String toString() {
         return "DownloadResult.DownloadResultBuilder(success="
            + this.success
            + ", filePath="
            + this.filePath
            + ", fileType="
            + this.fileType
            + ", errorMessage="
            + this.errorMessage
            + ")";
      }
   }
}
