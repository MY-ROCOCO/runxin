package com.rxinyun.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(
   description = "DNS NS记录查询结果"
)
public class DnsNsRecordResult {
   @Schema(
      description = "查询域名"
   )
   private String domainName;
   @Schema(
      description = "DNS服务器"
   )
   private String dnsServer;
   @Schema(
      description = "是否查询成功"
   )
   private Boolean success;
   @Schema(
      description = "NS记录列表"
   )
   private List<String> nsRecords;
   @Schema(
      description = "错误信息"
   )
   private String errorMessage;

   public String getDomainName() {
      return this.domainName;
   }

   public String getDnsServer() {
      return this.dnsServer;
   }

   public Boolean getSuccess() {
      return this.success;
   }

   public List<String> getNsRecords() {
      return this.nsRecords;
   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setDnsServer(final String dnsServer) {
      this.dnsServer = dnsServer;
   }

   public void setSuccess(final Boolean success) {
      this.success = success;
   }

   public void setNsRecords(final List<String> nsRecords) {
      this.nsRecords = nsRecords;
   }

   public void setErrorMessage(final String errorMessage) {
      this.errorMessage = errorMessage;
   }
}
