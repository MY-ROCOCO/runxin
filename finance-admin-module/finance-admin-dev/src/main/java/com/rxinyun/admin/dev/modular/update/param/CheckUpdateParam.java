package com.rxinyun.admin.dev.modular.update.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "检查更新参数"
)
public class CheckUpdateParam {
   @Schema(
      description = "当前版本号"
   )
   private String currentVersion;

   public String getCurrentVersion() {
      return this.currentVersion;
   }

   public void setCurrentVersion(final String currentVersion) {
      this.currentVersion = currentVersion;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CheckUpdateParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$currentVersion = this.getCurrentVersion();
         Object other$currentVersion = other.getCurrentVersion();
         return this$currentVersion == null ? other$currentVersion == null : this$currentVersion.equals(other$currentVersion);
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof CheckUpdateParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $currentVersion = this.getCurrentVersion();
      return result * 59 + ($currentVersion == null ? 43 : $currentVersion.hashCode());
   }

   @Override
   public String toString() {
      return "CheckUpdateParam(currentVersion=" + this.getCurrentVersion() + ")";
   }
}
