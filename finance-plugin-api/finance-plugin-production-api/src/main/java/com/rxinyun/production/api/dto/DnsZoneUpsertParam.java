package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "DNS托管域名下发参数"
)
public class DnsZoneUpsertParam implements Serializable {
   @Schema(
      description = "托管域名ID"
   )
   private String zoneId;
   @Schema(
      description = "DNS套餐实例ID"
   )
   private String packageInstanceId;
   @Schema(
      description = "托管域名"
   )
   private String domainName;
   @Schema(
      description = "托管域名联系人邮箱"
   )
   private String adminEmail;
   @Schema(
      description = "是否启用"
   )
   private Boolean enabled;

   public String getZoneId() {
      return this.zoneId;
   }

   public String getPackageInstanceId() {
      return this.packageInstanceId;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getAdminEmail() {
      return this.adminEmail;
   }

   public Boolean getEnabled() {
      return this.enabled;
   }

   public void setZoneId(final String zoneId) {
      this.zoneId = zoneId;
   }

   public void setPackageInstanceId(final String packageInstanceId) {
      this.packageInstanceId = packageInstanceId;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setAdminEmail(final String adminEmail) {
      this.adminEmail = adminEmail;
   }

   public void setEnabled(final Boolean enabled) {
      this.enabled = enabled;
   }
}
