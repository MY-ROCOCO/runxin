package com.rxinyun.admin.workbench.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class BusinessVO {
   @Schema(
      description = "云服务器数量"
   )
   private Long serverNum;
   @Schema(
      description = "独立服务器数量"
   )
   private Long independentServerNum;
   @Schema(
      description = "域名数量"
   )
   private Long domainNum;

   public Long getServerNum() {
      return this.serverNum;
   }

   public Long getIndependentServerNum() {
      return this.independentServerNum;
   }

   public Long getDomainNum() {
      return this.domainNum;
   }

   public void setServerNum(final Long serverNum) {
      this.serverNum = serverNum;
   }

   public void setIndependentServerNum(final Long independentServerNum) {
      this.independentServerNum = independentServerNum;
   }

   public void setDomainNum(final Long domainNum) {
      this.domainNum = domainNum;
   }
}
