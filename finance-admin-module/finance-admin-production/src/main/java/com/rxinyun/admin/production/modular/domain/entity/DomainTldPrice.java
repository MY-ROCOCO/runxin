package com.rxinyun.admin.production.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@TableName(
   value = "DOMAIN_TLD_PRICE",
   autoResultMap = true
)
public class DomainTldPrice extends CommonEntity {
   @Schema(
      description = "ID"
   )
   private String id;
   @Schema(
      description = "后缀，如 com、cn"
   )
   private String tld;
   @Schema(
      description = "注册价格"
   )
   private BigDecimal registerPrice;
   @Schema(
      description = "续费价格"
   )
   private BigDecimal renewPrice;
   @Schema(
      description = "转入价格"
   )
   private BigDecimal transferPrice;
   @Schema(
      description = "可选年限，如 1,2,3,5"
   )
   private String years;
   @Schema(
      description = "状态：0 关闭 1 开启"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getTld() {
      return this.tld;
   }

   public BigDecimal getRegisterPrice() {
      return this.registerPrice;
   }

   public BigDecimal getRenewPrice() {
      return this.renewPrice;
   }

   public BigDecimal getTransferPrice() {
      return this.transferPrice;
   }

   public String getYears() {
      return this.years;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTld(final String tld) {
      this.tld = tld;
   }

   public void setRegisterPrice(final BigDecimal registerPrice) {
      this.registerPrice = registerPrice;
   }

   public void setRenewPrice(final BigDecimal renewPrice) {
      this.renewPrice = renewPrice;
   }

   public void setTransferPrice(final BigDecimal transferPrice) {
      this.transferPrice = transferPrice;
   }

   public void setYears(final String years) {
      this.years = years;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
