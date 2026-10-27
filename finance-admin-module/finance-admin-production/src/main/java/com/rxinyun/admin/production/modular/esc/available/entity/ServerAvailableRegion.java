package com.rxinyun.admin.production.modular.esc.available.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "SERVER_AVAILABLE_REGION",
   autoResultMap = true
)
public class ServerAvailableRegion extends CommonEntity {
   @Schema(
      description = "可用区域标识"
   )
   private String id;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "地域名称"
   )
   @TableField(
      exist = false
   )
   private String regionName;
   @Schema(
      description = "可用区域名称"
   )
   private String availableName;
   @Schema(
      description = "最大年数"
   )
   private Integer maxYear;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "地址"
   )
   private String address;
   @Schema(
      description = "DCIM 控制器标识"
   )
   private String dcimControllerGuid;
   @Schema(
      description = "IPMI 控制器标识"
   )
   private String ipmiControllerGuid;
   @Schema(
      description = "服务器控制器标识"
   )
   private String serverControllerGuid;
   @Schema(
      description = "NOVNC 控制器标识"
   )
   private String novncControllerGuid;
   @Schema(
      description = "ICP 状态"
   )
   private Integer icpStatus;
   @Schema(
      description = "白名单状态"
   )
   private Integer whiteListStatus;
   @Schema(
      description = "随机VLAN状态"
   )
   private Integer casualVlanStatus;
   @Schema(
      description = "随机VLAN ID"
   )
   private String casualVlanId;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   @TableField("TYPE")
   private Integer type;
   @Schema(
      description = "资源池ID（当可用区来自资源池时，关联resource_pool.id）"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;
   @Schema(
      description = "资源池类型（当可用区来自资源池时显示）"
   )
   @TableField(
      exist = false
   )
   private String resourcePoolType;
   @Schema(
      description = "资源池名称（当可用区来自资源池时显示）"
   )
   @TableField(
      exist = false
   )
   private String resourcePoolName;

   public String getId() {
      return this.id;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getRegionName() {
      return this.regionName;
   }

   public String getAvailableName() {
      return this.availableName;
   }

   public Integer getMaxYear() {
      return this.maxYear;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getAddress() {
      return this.address;
   }

   public String getDcimControllerGuid() {
      return this.dcimControllerGuid;
   }

   public String getIpmiControllerGuid() {
      return this.ipmiControllerGuid;
   }

   public String getServerControllerGuid() {
      return this.serverControllerGuid;
   }

   public String getNovncControllerGuid() {
      return this.novncControllerGuid;
   }

   public Integer getIcpStatus() {
      return this.icpStatus;
   }

   public Integer getWhiteListStatus() {
      return this.whiteListStatus;
   }

   public Integer getCasualVlanStatus() {
      return this.casualVlanStatus;
   }

   public String getCasualVlanId() {
      return this.casualVlanId;
   }

   public Integer getSortId() {
      return this.sortId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getType() {
      return this.type;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getResourcePoolType() {
      return this.resourcePoolType;
   }

   public String getResourcePoolName() {
      return this.resourcePoolName;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setAvailableName(final String availableName) {
      this.availableName = availableName;
   }

   public void setMaxYear(final Integer maxYear) {
      this.maxYear = maxYear;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setAddress(final String address) {
      this.address = address;
   }

   public void setDcimControllerGuid(final String dcimControllerGuid) {
      this.dcimControllerGuid = dcimControllerGuid;
   }

   public void setIpmiControllerGuid(final String ipmiControllerGuid) {
      this.ipmiControllerGuid = ipmiControllerGuid;
   }

   public void setServerControllerGuid(final String serverControllerGuid) {
      this.serverControllerGuid = serverControllerGuid;
   }

   public void setNovncControllerGuid(final String novncControllerGuid) {
      this.novncControllerGuid = novncControllerGuid;
   }

   public void setIcpStatus(final Integer icpStatus) {
      this.icpStatus = icpStatus;
   }

   public void setWhiteListStatus(final Integer whiteListStatus) {
      this.whiteListStatus = whiteListStatus;
   }

   public void setCasualVlanStatus(final Integer casualVlanStatus) {
      this.casualVlanStatus = casualVlanStatus;
   }

   public void setCasualVlanId(final String casualVlanId) {
      this.casualVlanId = casualVlanId;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setResourcePoolType(final String resourcePoolType) {
      this.resourcePoolType = resourcePoolType;
   }

   public void setResourcePoolName(final String resourcePoolName) {
      this.resourcePoolName = resourcePoolName;
   }
}
