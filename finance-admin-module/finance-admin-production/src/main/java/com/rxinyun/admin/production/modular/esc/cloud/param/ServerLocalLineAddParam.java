package com.rxinyun.admin.production.modular.esc.cloud.param;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerDiscounts;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLineSku;
import com.rxinyun.common.handler.IntegerListTypeHandler;
import com.rxinyun.common.handler.ListToStringTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import javax.validation.constraints.Min;
import org.apache.ibatis.type.JdbcType;

public class ServerLocalLineAddParam {
   @Schema(
      description = "ID"
   )
   private String id;
   @Schema(
      description = "可用区域ID"
   )
   private String availableId;
   @Schema(
      description = "可用区域名称"
   )
   @TableField(
      exist = false
   )
   private String availableName;
   @Schema(
      description = "线路名称"
   )
   private String lineName;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "销售状态: 0 关闭销售 1 开放销售"
   )
   private Integer status;
   @Schema(
      description = "升级状态: 0 关闭升级 1 开放升级"
   )
   private Integer upgradeStatus;
   @Schema(
      description = "续费状态: 0 关闭续费 1 开放续费"
   )
   private Integer renewStatus;
   @Schema(
      description = "最大购买台数"
   )
   private Integer maxOpenNum;
   @Schema(
      description = "可选购买时长"
   )
   @TableField(
      jdbcType = JdbcType.VARCHAR,
      insertStrategy = FieldStrategy.NOT_NULL,
      typeHandler = IntegerListTypeHandler.class
   )
   private List<Integer> purchaseDurations;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "宿主机分配规则: 1 默认按宿主机权重比例分配, 2 开通实例少优先, 3 磁盘剩余多优先, 4 内存剩余多优先"
   )
   private Integer masterIndex;
   @Schema(
      description = "IP分配规则: 0 顺序分配 1 乱序分配"
   )
   private Integer ipRand;
   @Schema(
      description = "快照开放状态: 0 关闭 1 开启"
   )
   private Integer snapPriceType;
   @Schema(
      description = "最大快照数量"
   )
   private Integer snapOpenMax;
   @Schema(
      description = "每块最小"
   )
   private Integer hardDiskMinSize;
   @Schema(
      description = "每块最大"
   )
   private Integer hardDiskMaxSize;
   @Schema(
      description = "磁盘大小必须是G的倍数"
   )
   private Integer hardDiskStep;
   @Schema(
      description = "系统盘最小"
   )
   @Min(
      value = 0L,
      message = "系统盘最小不能小于0"
   )
   private Integer systemDiskMinSize;
   @Schema(
      description = "系统盘最大"
   )
   @Min(
      value = 0L,
      message = "系统盘最大不能小于0"
   )
   private Integer systemDiskMaxSize;
   @Schema(
      description = "系统盘赠送大小"
   )
   private Integer systemDiskFreeSize;
   @Schema(
      description = "系统盘/日单价"
   )
   private BigDecimal systemDiskDayPrice;
   @Schema(
      description = "系统盘/月单价"
   )
   private BigDecimal systemDiskMonthPrice;
   @Schema(
      description = "系统盘大小必须是G的倍数"
   )
   private Integer systemDiskStep;
   @Schema(
      description = "数据盘每块最小"
   )
   @Min(
      value = 0L,
      message = "数据盘每块最小不能小于0"
   )
   private Integer dataDiskMinSize;
   @Schema(
      description = "数据盘每块最大"
   )
   private Integer dataDiskMaxSize;
   @Schema(
      description = "数据盘大小必须是G的倍数"
   )
   private Integer dataDiskStep;
   @Schema(
      description = "数据盘赠送大小"
   )
   private Integer dataDiskFreeSize;
   @Schema(
      description = "数据盘价格/日"
   )
   private BigDecimal dataDiskDayPrice;
   @Schema(
      description = "数据盘价格/月"
   )
   private BigDecimal dataDiskMonthPrice;
   @Schema(
      description = "带宽最小"
   )
   @Min(
      value = 0L,
      message = "带宽最小不能小于0"
   )
   private Integer bandwidthMinSize;
   @Schema(
      description = "带宽最大"
   )
   @Min(
      value = 0L,
      message = "带宽最大不能小于0"
   )
   private Integer bandwidthMaxSize;
   @Schema(
      description = "带宽赠送值"
   )
   private Integer bandwidthFreeSize;
   @Schema(
      description = "带宽下行限制类型: 0 固定值 1 对等阈值"
   )
   private Integer bandwidthInType;
   @Schema(
      description = "带宽下行限制值"
   )
   private Integer bandwidthInValue;
   @Schema(
      description = "带宽价格/日"
   )
   private BigDecimal bandwidthDayPrice;
   @Schema(
      description = "带宽价格/月"
   )
   private BigDecimal bandwidthPrice;
   @Schema(
      description = "弹性IP开关: 0 关闭 1 开启"
   )
   private Integer appendIpStatus;
   @Schema(
      description = "弹性IP价格"
   )
   private BigDecimal appendIpPrice;
   @Schema(
      description = "IP数量最小"
   )
   @Min(
      value = 1L,
      message = "IP数量最小不能小于1"
   )
   private Integer ipMinSize;
   @Schema(
      description = "IP数量最大"
   )
   @Min(
      value = 1L,
      message = "IP数量最大不能小于1"
   )
   private Integer ipMaxSize;
   @Schema(
      description = "IP赠送数量"
   )
   private Integer ipFreeSize;
   @Schema(
      description = "防御值选项"
   )
   private String defenseSize;
   @Schema(
      description = "赠送防御"
   )
   private Integer defenseFreeSize;
   @Schema(
      description = "防御价格/日"
   )
   private BigDecimal defenseDayPrice;
   @Schema(
      description = "防御价格/月"
   )
   private BigDecimal defensePrice;
   @Schema(
      description = "客户机DNS"
   )
   @TableField(
      jdbcType = JdbcType.VARCHAR,
      insertStrategy = FieldStrategy.NOT_NULL,
      typeHandler = ListToStringTypeHandler.class
   )
   private List<String> autoDns;
   @Schema(
      description = "描述"
   )
   private String descript;
   @Schema(
      description = "机房介绍连接"
   )
   private String introUrl;
   @Schema(
      description = "黑洞中间件"
   )
   private Integer blockMiddlewareId;
   @Schema(
      description = "黑洞默认封堵时长"
   )
   private Integer defaultBlockTime;
   @Schema(
      description = "是否支持提前解封: 0 不支持 1 支持"
   )
   private Integer bgFreeStatus;
   @Schema(
      description = "黑洞解封单价"
   )
   private BigDecimal bgFreePrice;
   @Schema(
      description = "申请解封前进入黑洞时长限制"
   )
   private Integer bgFreeMinuteLimit;
   @Schema(
      description = "折扣模板ID"
   )
   private String discountTemplateId;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID（当线路来自资源池时，关联resource_pool.id）"
   )
   private String resourcePoolId;
   @Schema(
      description = "是否可对接: 0 否 1 是"
   )
   private Integer dockingStatus;
   @Schema(
      description = "核心价格"
   )
   private List<ServerLineSku> serverLineSkuList;
   @Schema(
      description = "优惠折扣"
   )
   private List<ServerDiscounts> serverDiscountsList;

   public String getId() {
      return this.id;
   }

   public String getAvailableId() {
      return this.availableId;
   }

   public String getAvailableName() {
      return this.availableName;
   }

   public String getLineName() {
      return this.lineName;
   }

   public String getRemark() {
      return this.remark;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getUpgradeStatus() {
      return this.upgradeStatus;
   }

   public Integer getRenewStatus() {
      return this.renewStatus;
   }

   public Integer getMaxOpenNum() {
      return this.maxOpenNum;
   }

   public List<Integer> getPurchaseDurations() {
      return this.purchaseDurations;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getMasterIndex() {
      return this.masterIndex;
   }

   public Integer getIpRand() {
      return this.ipRand;
   }

   public Integer getSnapPriceType() {
      return this.snapPriceType;
   }

   public Integer getSnapOpenMax() {
      return this.snapOpenMax;
   }

   public Integer getHardDiskMinSize() {
      return this.hardDiskMinSize;
   }

   public Integer getHardDiskMaxSize() {
      return this.hardDiskMaxSize;
   }

   public Integer getHardDiskStep() {
      return this.hardDiskStep;
   }

   public Integer getSystemDiskMinSize() {
      return this.systemDiskMinSize;
   }

   public Integer getSystemDiskMaxSize() {
      return this.systemDiskMaxSize;
   }

   public Integer getSystemDiskFreeSize() {
      return this.systemDiskFreeSize;
   }

   public BigDecimal getSystemDiskDayPrice() {
      return this.systemDiskDayPrice;
   }

   public BigDecimal getSystemDiskMonthPrice() {
      return this.systemDiskMonthPrice;
   }

   public Integer getSystemDiskStep() {
      return this.systemDiskStep;
   }

   public Integer getDataDiskMinSize() {
      return this.dataDiskMinSize;
   }

   public Integer getDataDiskMaxSize() {
      return this.dataDiskMaxSize;
   }

   public Integer getDataDiskStep() {
      return this.dataDiskStep;
   }

   public Integer getDataDiskFreeSize() {
      return this.dataDiskFreeSize;
   }

   public BigDecimal getDataDiskDayPrice() {
      return this.dataDiskDayPrice;
   }

   public BigDecimal getDataDiskMonthPrice() {
      return this.dataDiskMonthPrice;
   }

   public Integer getBandwidthMinSize() {
      return this.bandwidthMinSize;
   }

   public Integer getBandwidthMaxSize() {
      return this.bandwidthMaxSize;
   }

   public Integer getBandwidthFreeSize() {
      return this.bandwidthFreeSize;
   }

   public Integer getBandwidthInType() {
      return this.bandwidthInType;
   }

   public Integer getBandwidthInValue() {
      return this.bandwidthInValue;
   }

   public BigDecimal getBandwidthDayPrice() {
      return this.bandwidthDayPrice;
   }

   public BigDecimal getBandwidthPrice() {
      return this.bandwidthPrice;
   }

   public Integer getAppendIpStatus() {
      return this.appendIpStatus;
   }

   public BigDecimal getAppendIpPrice() {
      return this.appendIpPrice;
   }

   public Integer getIpMinSize() {
      return this.ipMinSize;
   }

   public Integer getIpMaxSize() {
      return this.ipMaxSize;
   }

   public Integer getIpFreeSize() {
      return this.ipFreeSize;
   }

   public String getDefenseSize() {
      return this.defenseSize;
   }

   public Integer getDefenseFreeSize() {
      return this.defenseFreeSize;
   }

   public BigDecimal getDefenseDayPrice() {
      return this.defenseDayPrice;
   }

   public BigDecimal getDefensePrice() {
      return this.defensePrice;
   }

   public List<String> getAutoDns() {
      return this.autoDns;
   }

   public String getDescript() {
      return this.descript;
   }

   public String getIntroUrl() {
      return this.introUrl;
   }

   public Integer getBlockMiddlewareId() {
      return this.blockMiddlewareId;
   }

   public Integer getDefaultBlockTime() {
      return this.defaultBlockTime;
   }

   public Integer getBgFreeStatus() {
      return this.bgFreeStatus;
   }

   public BigDecimal getBgFreePrice() {
      return this.bgFreePrice;
   }

   public Integer getBgFreeMinuteLimit() {
      return this.bgFreeMinuteLimit;
   }

   public String getDiscountTemplateId() {
      return this.discountTemplateId;
   }

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public Integer getDockingStatus() {
      return this.dockingStatus;
   }

   public List<ServerLineSku> getServerLineSkuList() {
      return this.serverLineSkuList;
   }

   public List<ServerDiscounts> getServerDiscountsList() {
      return this.serverDiscountsList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAvailableId(final String availableId) {
      this.availableId = availableId;
   }

   public void setAvailableName(final String availableName) {
      this.availableName = availableName;
   }

   public void setLineName(final String lineName) {
      this.lineName = lineName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setUpgradeStatus(final Integer upgradeStatus) {
      this.upgradeStatus = upgradeStatus;
   }

   public void setRenewStatus(final Integer renewStatus) {
      this.renewStatus = renewStatus;
   }

   public void setMaxOpenNum(final Integer maxOpenNum) {
      this.maxOpenNum = maxOpenNum;
   }

   public void setPurchaseDurations(final List<Integer> purchaseDurations) {
      this.purchaseDurations = purchaseDurations;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setMasterIndex(final Integer masterIndex) {
      this.masterIndex = masterIndex;
   }

   public void setIpRand(final Integer ipRand) {
      this.ipRand = ipRand;
   }

   public void setSnapPriceType(final Integer snapPriceType) {
      this.snapPriceType = snapPriceType;
   }

   public void setSnapOpenMax(final Integer snapOpenMax) {
      this.snapOpenMax = snapOpenMax;
   }

   public void setHardDiskMinSize(final Integer hardDiskMinSize) {
      this.hardDiskMinSize = hardDiskMinSize;
   }

   public void setHardDiskMaxSize(final Integer hardDiskMaxSize) {
      this.hardDiskMaxSize = hardDiskMaxSize;
   }

   public void setHardDiskStep(final Integer hardDiskStep) {
      this.hardDiskStep = hardDiskStep;
   }

   public void setSystemDiskMinSize(final Integer systemDiskMinSize) {
      this.systemDiskMinSize = systemDiskMinSize;
   }

   public void setSystemDiskMaxSize(final Integer systemDiskMaxSize) {
      this.systemDiskMaxSize = systemDiskMaxSize;
   }

   public void setSystemDiskFreeSize(final Integer systemDiskFreeSize) {
      this.systemDiskFreeSize = systemDiskFreeSize;
   }

   public void setSystemDiskDayPrice(final BigDecimal systemDiskDayPrice) {
      this.systemDiskDayPrice = systemDiskDayPrice;
   }

   public void setSystemDiskMonthPrice(final BigDecimal systemDiskMonthPrice) {
      this.systemDiskMonthPrice = systemDiskMonthPrice;
   }

   public void setSystemDiskStep(final Integer systemDiskStep) {
      this.systemDiskStep = systemDiskStep;
   }

   public void setDataDiskMinSize(final Integer dataDiskMinSize) {
      this.dataDiskMinSize = dataDiskMinSize;
   }

   public void setDataDiskMaxSize(final Integer dataDiskMaxSize) {
      this.dataDiskMaxSize = dataDiskMaxSize;
   }

   public void setDataDiskStep(final Integer dataDiskStep) {
      this.dataDiskStep = dataDiskStep;
   }

   public void setDataDiskFreeSize(final Integer dataDiskFreeSize) {
      this.dataDiskFreeSize = dataDiskFreeSize;
   }

   public void setDataDiskDayPrice(final BigDecimal dataDiskDayPrice) {
      this.dataDiskDayPrice = dataDiskDayPrice;
   }

   public void setDataDiskMonthPrice(final BigDecimal dataDiskMonthPrice) {
      this.dataDiskMonthPrice = dataDiskMonthPrice;
   }

   public void setBandwidthMinSize(final Integer bandwidthMinSize) {
      this.bandwidthMinSize = bandwidthMinSize;
   }

   public void setBandwidthMaxSize(final Integer bandwidthMaxSize) {
      this.bandwidthMaxSize = bandwidthMaxSize;
   }

   public void setBandwidthFreeSize(final Integer bandwidthFreeSize) {
      this.bandwidthFreeSize = bandwidthFreeSize;
   }

   public void setBandwidthInType(final Integer bandwidthInType) {
      this.bandwidthInType = bandwidthInType;
   }

   public void setBandwidthInValue(final Integer bandwidthInValue) {
      this.bandwidthInValue = bandwidthInValue;
   }

   public void setBandwidthDayPrice(final BigDecimal bandwidthDayPrice) {
      this.bandwidthDayPrice = bandwidthDayPrice;
   }

   public void setBandwidthPrice(final BigDecimal bandwidthPrice) {
      this.bandwidthPrice = bandwidthPrice;
   }

   public void setAppendIpStatus(final Integer appendIpStatus) {
      this.appendIpStatus = appendIpStatus;
   }

   public void setAppendIpPrice(final BigDecimal appendIpPrice) {
      this.appendIpPrice = appendIpPrice;
   }

   public void setIpMinSize(final Integer ipMinSize) {
      this.ipMinSize = ipMinSize;
   }

   public void setIpMaxSize(final Integer ipMaxSize) {
      this.ipMaxSize = ipMaxSize;
   }

   public void setIpFreeSize(final Integer ipFreeSize) {
      this.ipFreeSize = ipFreeSize;
   }

   public void setDefenseSize(final String defenseSize) {
      this.defenseSize = defenseSize;
   }

   public void setDefenseFreeSize(final Integer defenseFreeSize) {
      this.defenseFreeSize = defenseFreeSize;
   }

   public void setDefenseDayPrice(final BigDecimal defenseDayPrice) {
      this.defenseDayPrice = defenseDayPrice;
   }

   public void setDefensePrice(final BigDecimal defensePrice) {
      this.defensePrice = defensePrice;
   }

   public void setAutoDns(final List<String> autoDns) {
      this.autoDns = autoDns;
   }

   public void setDescript(final String descript) {
      this.descript = descript;
   }

   public void setIntroUrl(final String introUrl) {
      this.introUrl = introUrl;
   }

   public void setBlockMiddlewareId(final Integer blockMiddlewareId) {
      this.blockMiddlewareId = blockMiddlewareId;
   }

   public void setDefaultBlockTime(final Integer defaultBlockTime) {
      this.defaultBlockTime = defaultBlockTime;
   }

   public void setBgFreeStatus(final Integer bgFreeStatus) {
      this.bgFreeStatus = bgFreeStatus;
   }

   public void setBgFreePrice(final BigDecimal bgFreePrice) {
      this.bgFreePrice = bgFreePrice;
   }

   public void setBgFreeMinuteLimit(final Integer bgFreeMinuteLimit) {
      this.bgFreeMinuteLimit = bgFreeMinuteLimit;
   }

   public void setDiscountTemplateId(final String discountTemplateId) {
      this.discountTemplateId = discountTemplateId;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setDockingStatus(final Integer dockingStatus) {
      this.dockingStatus = dockingStatus;
   }

   public void setServerLineSkuList(final List<ServerLineSku> serverLineSkuList) {
      this.serverLineSkuList = serverLineSkuList;
   }

   public void setServerDiscountsList(final List<ServerDiscounts> serverDiscountsList) {
      this.serverDiscountsList = serverDiscountsList;
   }
}
