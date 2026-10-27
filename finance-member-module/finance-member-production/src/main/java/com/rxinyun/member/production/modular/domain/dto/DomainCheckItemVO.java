package com.rxinyun.member.production.modular.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "域名可注册查询单项结果"
)
public class DomainCheckItemVO {
   @Schema(
      description = "域名"
   )
   private String domainName;
   @Schema(
      description = "是否可注册：0-否 1-是 2-查询超时"
   )
   private Integer canReg;
   @Schema(
      description = "域名词性，如 standard-普通词 premium-溢价词"
   )
   private String feeLabel;
   @Schema(
      description = "溢价词价格"
   )
   private String feePrice;
   @Schema(
      description = "备注"
   )
   private String comment;

   public String getDomainName() {
      return this.domainName;
   }

   public Integer getCanReg() {
      return this.canReg;
   }

   public String getFeeLabel() {
      return this.feeLabel;
   }

   public String getFeePrice() {
      return this.feePrice;
   }

   public String getComment() {
      return this.comment;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setCanReg(final Integer canReg) {
      this.canReg = canReg;
   }

   public void setFeeLabel(final String feeLabel) {
      this.feeLabel = feeLabel;
   }

   public void setFeePrice(final String feePrice) {
      this.feePrice = feePrice;
   }

   public void setComment(final String comment) {
      this.comment = comment;
   }
}
