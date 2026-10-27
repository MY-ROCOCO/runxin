package com.rxinyun.admin.production.modular.esc.cloud.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class ServerLineSkuAddParam {
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "型号名称"
   )
   private String name;
   @Schema(
      description = "CPU核心数"
   )
   private int cpu;
   @Schema(
      description = "内存大小(单位: GB)"
   )
   private int ram;
   @Schema(
      description = "核心配置的价格/月"
   )
   private BigDecimal coreMonthPrice;
   @Schema(
      description = "系统盘大小(单位: GB)"
   )
   private int osHardDiskSize;
   @Schema(
      description = "数据盘大小(单位: GB)"
   )
   private int hardDiskSize;
   @Schema(
      description = "状态"
   )
   private int status;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "推荐状态"
   )
   private int recommendStatus;
   @Schema(
      description = "推荐内容"
   )
   private String recommendContent;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   @TableField("TYPE")
   private Integer type;
   @Schema(
      description = "资源池ID（当SKU来自资源池时，关联resource_pool.id）"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;

   public String getLineId() {
      return this.lineId;
   }

   public String getName() {
      return this.name;
   }

   public int getCpu() {
      return this.cpu;
   }

   public int getRam() {
      return this.ram;
   }

   public BigDecimal getCoreMonthPrice() {
      return this.coreMonthPrice;
   }

   public int getOsHardDiskSize() {
      return this.osHardDiskSize;
   }

   public int getHardDiskSize() {
      return this.hardDiskSize;
   }

   public int getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public int getRecommendStatus() {
      return this.recommendStatus;
   }

   public String getRecommendContent() {
      return this.recommendContent;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setCpu(final int cpu) {
      this.cpu = cpu;
   }

   public void setRam(final int ram) {
      this.ram = ram;
   }

   public void setCoreMonthPrice(final BigDecimal coreMonthPrice) {
      this.coreMonthPrice = coreMonthPrice;
   }

   public void setOsHardDiskSize(final int osHardDiskSize) {
      this.osHardDiskSize = osHardDiskSize;
   }

   public void setHardDiskSize(final int hardDiskSize) {
      this.hardDiskSize = hardDiskSize;
   }

   public void setStatus(final int status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setRecommendStatus(final int recommendStatus) {
      this.recommendStatus = recommendStatus;
   }

   public void setRecommendContent(final String recommendContent) {
      this.recommendContent = recommendContent;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
