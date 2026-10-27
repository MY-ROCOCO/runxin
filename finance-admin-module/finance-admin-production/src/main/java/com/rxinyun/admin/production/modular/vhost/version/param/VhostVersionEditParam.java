package com.rxinyun.admin.production.modular.vhost.version.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class VhostVersionEditParam {
   @Schema(
      description = "版本ID"
   )
   @NotBlank(
      message = "版本ID不能为空"
   )
   private String id;
   @Schema(
      description = "版本名称"
   )
   @NotBlank(
      message = "版本名称不能为空"
   )
   private String name;
   @Schema(
      description = "当前版本月价格"
   )
   @NotNull(
      message = "月价格不能为空"
   )
   @DecimalMin(
      value = "0",
      inclusive = false,
      message = "版本月价格必须大于0"
   )
   private BigDecimal monthPrice;
   @Schema(
      description = "最小购买流量，单位G"
   )
   @NotNull(
      message = "最小购买流量不能为空"
   )
   @Min(
      value = 0L,
      message = "最小购买流量不能小于0"
   )
   private Integer trafficMinSize;
   @Schema(
      description = "最大购买流量，单位G"
   )
   @NotNull(
      message = "最大购买流量不能为空"
   )
   @Min(
      value = 0L,
      message = "最大购买流量不能小于0"
   )
   private Integer trafficMaxSize;
   @Schema(
      description = "赠送流量，单位G"
   )
   @NotNull(
      message = "赠送流量不能为空"
   )
   @Min(
      value = 0L,
      message = "赠送流量不能小于0"
   )
   private Integer trafficFreeSize;
   @Schema(
      description = "流量价格，单位元/G"
   )
   @NotNull(
      message = "流量价格不能为空"
   )
   @DecimalMin(
      value = "0",
      inclusive = false,
      message = "流量价格必须大于0"
   )
   private BigDecimal trafficPrice;
   @Schema(
      description = "支持的PHP版本，英文逗号分隔，例如 7.4,8.0,8.1"
   )
   private String phpVersions;
   @Schema(
      description = "支持的MySQL版本，英文逗号分隔，例如 5.7,8.0"
   )
   private String mysqlVersions;
   @Schema(
      description = "绑定折扣模板ID，传空则清空"
   )
   private String discountTemplateId;
   @Schema(
      description = "排序，默认0"
   )
   private Integer sortId;
   @Schema(
      description = "状态：1启用 0禁用，默认1"
   )
   private Integer status;
   @Schema(
      description = "绑定线路ID列表，编辑时按该列表整体替换"
   )
   private List<String> lineIds;

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

   public List<String> getLineIds() {
      return this.lineIds;
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

   public void setLineIds(final List<String> lineIds) {
      this.lineIds = lineIds;
   }
}
