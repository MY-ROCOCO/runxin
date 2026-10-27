package com.rxinyun.admin.dev.modular.update.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "下载参数"
)
public class DownloadParam {
   @Schema(
      description = "更新的版本号"
   )
   private String version;

   public String getVersion() {
      return this.version;
   }

   public void setVersion(final String version) {
      this.version = version;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DownloadParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$version = this.getVersion();
         Object other$version = other.getVersion();
         return this$version == null ? other$version == null : this$version.equals(other$version);
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof DownloadParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $version = this.getVersion();
      return result * 59 + ($version == null ? 43 : $version.hashCode());
   }

   @Override
   public String toString() {
      return "DownloadParam(version=" + this.getVersion() + ")";
   }
}
