package com.rxinyun.admin.dev.modular.update.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "更新检查结果"
)
public class UpdateCheckResult {
   @Schema(
      description = "是否有更新"
   )
   private boolean hasUpdate;
   @Schema(
      description = "当前版本号"
   )
   private String currentVersion;
   @Schema(
      description = "当前版本描述"
   )
   private String currentDescription;
   @Schema(
      description = "最新版本号"
   )
   private String latestVersion;
   @Schema(
      description = "最新版本描述"
   )
   private String latestDescription;
   @Schema(
      description = "下一个版本号"
   )
   private String nextVersion;
   @Schema(
      description = "下一个版本描述"
   )
   private String nextDescription;
   @Schema(
      description = "版本描述"
   )
   private String description;

   UpdateCheckResult(
      final boolean hasUpdate,
      final String currentVersion,
      final String currentDescription,
      final String latestVersion,
      final String latestDescription,
      final String nextVersion,
      final String nextDescription,
      final String description
   ) {
      this.hasUpdate = hasUpdate;
      this.currentVersion = currentVersion;
      this.currentDescription = currentDescription;
      this.latestVersion = latestVersion;
      this.latestDescription = latestDescription;
      this.nextVersion = nextVersion;
      this.nextDescription = nextDescription;
      this.description = description;
   }

   public static UpdateCheckResult.UpdateCheckResultBuilder builder() {
      return new UpdateCheckResult.UpdateCheckResultBuilder();
   }

   public boolean isHasUpdate() {
      return this.hasUpdate;
   }

   public String getCurrentVersion() {
      return this.currentVersion;
   }

   public String getCurrentDescription() {
      return this.currentDescription;
   }

   public String getLatestVersion() {
      return this.latestVersion;
   }

   public String getLatestDescription() {
      return this.latestDescription;
   }

   public String getNextVersion() {
      return this.nextVersion;
   }

   public String getNextDescription() {
      return this.nextDescription;
   }

   public String getDescription() {
      return this.description;
   }

   public void setHasUpdate(final boolean hasUpdate) {
      this.hasUpdate = hasUpdate;
   }

   public void setCurrentVersion(final String currentVersion) {
      this.currentVersion = currentVersion;
   }

   public void setCurrentDescription(final String currentDescription) {
      this.currentDescription = currentDescription;
   }

   public void setLatestVersion(final String latestVersion) {
      this.latestVersion = latestVersion;
   }

   public void setLatestDescription(final String latestDescription) {
      this.latestDescription = latestDescription;
   }

   public void setNextVersion(final String nextVersion) {
      this.nextVersion = nextVersion;
   }

   public void setNextDescription(final String nextDescription) {
      this.nextDescription = nextDescription;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof UpdateCheckResult other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else if (this.isHasUpdate() != other.isHasUpdate()) {
         return false;
      } else {
         Object this$currentVersion = this.getCurrentVersion();
         Object other$currentVersion = other.getCurrentVersion();
         if (this$currentVersion == null ? other$currentVersion == null : this$currentVersion.equals(other$currentVersion)) {
            Object this$currentDescription = this.getCurrentDescription();
            Object other$currentDescription = other.getCurrentDescription();
            if (this$currentDescription == null ? other$currentDescription == null : this$currentDescription.equals(other$currentDescription)) {
               Object this$latestVersion = this.getLatestVersion();
               Object other$latestVersion = other.getLatestVersion();
               if (this$latestVersion == null ? other$latestVersion == null : this$latestVersion.equals(other$latestVersion)) {
                  Object this$latestDescription = this.getLatestDescription();
                  Object other$latestDescription = other.getLatestDescription();
                  if (this$latestDescription == null ? other$latestDescription == null : this$latestDescription.equals(other$latestDescription)) {
                     Object this$nextVersion = this.getNextVersion();
                     Object other$nextVersion = other.getNextVersion();
                     if (this$nextVersion == null ? other$nextVersion == null : this$nextVersion.equals(other$nextVersion)) {
                        Object this$nextDescription = this.getNextDescription();
                        Object other$nextDescription = other.getNextDescription();
                        if (this$nextDescription == null ? other$nextDescription == null : this$nextDescription.equals(other$nextDescription)) {
                           Object this$description = this.getDescription();
                           Object other$description = other.getDescription();
                           return this$description == null ? other$description == null : this$description.equals(other$description);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
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
      return other instanceof UpdateCheckResult;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + (this.isHasUpdate() ? 79 : 97);
      Object $currentVersion = this.getCurrentVersion();
      result = result * 59 + ($currentVersion == null ? 43 : $currentVersion.hashCode());
      Object $currentDescription = this.getCurrentDescription();
      result = result * 59 + ($currentDescription == null ? 43 : $currentDescription.hashCode());
      Object $latestVersion = this.getLatestVersion();
      result = result * 59 + ($latestVersion == null ? 43 : $latestVersion.hashCode());
      Object $latestDescription = this.getLatestDescription();
      result = result * 59 + ($latestDescription == null ? 43 : $latestDescription.hashCode());
      Object $nextVersion = this.getNextVersion();
      result = result * 59 + ($nextVersion == null ? 43 : $nextVersion.hashCode());
      Object $nextDescription = this.getNextDescription();
      result = result * 59 + ($nextDescription == null ? 43 : $nextDescription.hashCode());
      Object $description = this.getDescription();
      return result * 59 + ($description == null ? 43 : $description.hashCode());
   }

   @Override
   public String toString() {
      return "UpdateCheckResult(hasUpdate="
         + this.isHasUpdate()
         + ", currentVersion="
         + this.getCurrentVersion()
         + ", currentDescription="
         + this.getCurrentDescription()
         + ", latestVersion="
         + this.getLatestVersion()
         + ", latestDescription="
         + this.getLatestDescription()
         + ", nextVersion="
         + this.getNextVersion()
         + ", nextDescription="
         + this.getNextDescription()
         + ", description="
         + this.getDescription()
         + ")";
   }

   public static class UpdateCheckResultBuilder {
      private boolean hasUpdate;
      private String currentVersion;
      private String currentDescription;
      private String latestVersion;
      private String latestDescription;
      private String nextVersion;
      private String nextDescription;
      private String description;

      UpdateCheckResultBuilder() {
      }

      public UpdateCheckResult.UpdateCheckResultBuilder hasUpdate(final boolean hasUpdate) {
         this.hasUpdate = hasUpdate;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder currentVersion(final String currentVersion) {
         this.currentVersion = currentVersion;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder currentDescription(final String currentDescription) {
         this.currentDescription = currentDescription;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder latestVersion(final String latestVersion) {
         this.latestVersion = latestVersion;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder latestDescription(final String latestDescription) {
         this.latestDescription = latestDescription;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder nextVersion(final String nextVersion) {
         this.nextVersion = nextVersion;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder nextDescription(final String nextDescription) {
         this.nextDescription = nextDescription;
         return this;
      }

      public UpdateCheckResult.UpdateCheckResultBuilder description(final String description) {
         this.description = description;
         return this;
      }

      public UpdateCheckResult build() {
         return new UpdateCheckResult(
            this.hasUpdate,
            this.currentVersion,
            this.currentDescription,
            this.latestVersion,
            this.latestDescription,
            this.nextVersion,
            this.nextDescription,
            this.description
         );
      }

      @Override
      public String toString() {
         return "UpdateCheckResult.UpdateCheckResultBuilder(hasUpdate="
            + this.hasUpdate
            + ", currentVersion="
            + this.currentVersion
            + ", currentDescription="
            + this.currentDescription
            + ", latestVersion="
            + this.latestVersion
            + ", latestDescription="
            + this.latestDescription
            + ", nextVersion="
            + this.nextVersion
            + ", nextDescription="
            + this.nextDescription
            + ", description="
            + this.description
            + ")";
      }
   }
}
