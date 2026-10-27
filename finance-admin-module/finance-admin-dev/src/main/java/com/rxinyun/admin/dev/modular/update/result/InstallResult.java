package com.rxinyun.admin.dev.modular.update.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "安装结果"
)
public class InstallResult {
   @Schema(
      description = "是否安装成功"
   )
   private boolean success;
   @Schema(
      description = "安装版本"
   )
   private String version;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;

   InstallResult(final boolean success, final String version, final String errorMessage) {
      this.success = success;
      this.version = version;
      this.errorMessage = errorMessage;
   }

   public static InstallResult.InstallResultBuilder builder() {
      return new InstallResult.InstallResultBuilder();
   }

   public boolean isSuccess() {
      return this.success;
   }

   public String getVersion() {
      return this.version;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public void setSuccess(final boolean success) {
      this.success = success;
   }

   public void setVersion(final String version) {
      this.version = version;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof InstallResult other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isSuccess() != other.isSuccess()) {
         return false;
      } else {
         Object this$version = this.getVersion();
         Object other$version = other.getVersion();
         if (this$version == null ? other$version == null : this$version.equals(other$version)) {
            Object this$errorMessage = this.getErrorMessage();
            Object other$errorMessage = other.getErrorMessage();
            return this$errorMessage == null ? other$errorMessage == null : this$errorMessage.equals(other$errorMessage);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof InstallResult;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isSuccess() ? 79 : 97);
      Object $version = this.getVersion();
      result = result * 59 + ($version == null ? 43 : $version.hashCode());
      Object $errorMessage = this.getErrorMessage();
      return result * 59 + ($errorMessage == null ? 43 : $errorMessage.hashCode());
   }

   @Override
   public String toString() {
      return "InstallResult(success=" + this.isSuccess() + ", version=" + this.getVersion() + ", errorMessage=" + this.getErrorMessage() + ")";
   }

   public static class InstallResultBuilder {
      private boolean success;
      private String version;
      private String errorMessage;

      InstallResultBuilder() {
      }

      public InstallResult.InstallResultBuilder success(final boolean success) {
         this.success = success;
         return this;
      }

      public InstallResult.InstallResultBuilder version(final String version) {
         this.version = version;
         return this;
      }

      public InstallResult.InstallResultBuilder errorMessage(final String errorMessage) {
         this.errorMessage = errorMessage;
         return this;
      }

      public InstallResult build() {
         return new InstallResult(this.success, this.version, this.errorMessage);
      }

      @Override
      public String toString() {
         return "InstallResult.InstallResultBuilder(success=" + this.success + ", version=" + this.version + ", errorMessage=" + this.errorMessage + ")";
      }
   }
}
