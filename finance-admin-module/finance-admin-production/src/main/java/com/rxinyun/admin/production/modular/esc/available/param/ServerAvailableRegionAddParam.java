package com.rxinyun.admin.production.modular.esc.available.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerAvailableRegionAddParam {
   @Schema(
      description = "ID"
   )
   private String id;
   @Schema(
      description = "可用区域名称"
   )
   private String availableName;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
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
   private Integer type;
   @Schema(
      description = "资源池ID（当可用区来自资源池时，关联resource_pool.id）"
   )
   private String resourcePoolId;

   public String getId() {
      return this.id;
   }

   public String getAvailableName() {
      return this.availableName;
   }

   public String getRegionId() {
      return this.regionId;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setAvailableName(final String availableName) {
      this.availableName = availableName;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
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
}
