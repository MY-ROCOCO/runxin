package com.rxinyun.member.production.modular.vhost.version.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@TableName(
   value = "VHOST_VERSION",
   autoResultMap = true
)
public class MemberVhostVersion extends CommonEntity {
   @Schema(
      description = "版本ID"
   )
   private String id;
   @Schema(
      description = "版本名称"
   )
   private String name;
   @Schema(
      description = "当前版本月价格"
   )
   private BigDecimal monthPrice;
   @Schema(
      description = "最小购买流量，单位G"
   )
   private Integer trafficMinSize;
   @Schema(
      description = "最大购买流量，单位G"
   )
   private Integer trafficMaxSize;
   @Schema(
      description = "赠送流量，单位G"
   )
   private Integer trafficFreeSize;
   @Schema(
      description = "流量价格，单位元/G"
   )
   private BigDecimal trafficPrice;
   @Schema(
      description = "支持的PHP版本，英文逗号分隔"
   )
   private String phpVersions;
   @Schema(
      description = "支持的MySQL版本，英文逗号分隔"
   )
   private String mysqlVersions;
   @Schema(
      description = "绑定折扣模板ID"
   )
   private String discountTemplateId;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "状态：1启用 0禁用"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public BigDecimal getMonthPrice() {
      return this.monthPrice;
   }

   public Integer getTrafficMinSize() {
      return this.trafficMinSize;
   }

   public Integer getTrafficMaxSize() {
      return this.trafficMaxSize;
   }

   public Integer getTrafficFreeSize() {
      return this.trafficFreeSize;
   }

   public BigDecimal getTrafficPrice() {
      return this.trafficPrice;
   }

   public String getPhpVersions() {
      return this.phpVersions;
   }

   public String getMysqlVersions() {
      return this.mysqlVersions;
   }

   public String getDiscountTemplateId() {
      return this.discountTemplateId;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setMonthPrice(final BigDecimal monthPrice) {
      this.monthPrice = monthPrice;
   }

   public void setTrafficMinSize(final Integer trafficMinSize) {
      this.trafficMinSize = trafficMinSize;
   }

   public void setTrafficMaxSize(final Integer trafficMaxSize) {
      this.trafficMaxSize = trafficMaxSize;
   }

   public void setTrafficFreeSize(final Integer trafficFreeSize) {
      this.trafficFreeSize = trafficFreeSize;
   }

   public void setTrafficPrice(final BigDecimal trafficPrice) {
      this.trafficPrice = trafficPrice;
   }

   public void setPhpVersions(final String phpVersions) {
      this.phpVersions = phpVersions;
   }

   public void setMysqlVersions(final String mysqlVersions) {
      this.mysqlVersions = mysqlVersions;
   }

   public void setDiscountTemplateId(final String discountTemplateId) {
      this.discountTemplateId = discountTemplateId;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
