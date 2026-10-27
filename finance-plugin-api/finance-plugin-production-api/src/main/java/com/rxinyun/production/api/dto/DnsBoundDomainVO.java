package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "云解析DNS实例绑定域名信息"
)
public class DnsBoundDomainVO implements Serializable {
   @Schema(
      description = "托管域名ID"
   )
   private String id;
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
   private String email;
   @Schema(
      description = "状态：1开启 0暂停"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getPackageInstanceId() {
      return this.packageInstanceId;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getEmail() {
      return this.email;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setPackageInstanceId(final String packageInstanceId) {
      this.packageInstanceId = packageInstanceId;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
