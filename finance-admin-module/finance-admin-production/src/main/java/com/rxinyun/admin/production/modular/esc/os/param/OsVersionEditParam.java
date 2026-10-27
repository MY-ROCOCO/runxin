package com.rxinyun.admin.production.modular.esc.os.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

public class OsVersionEditParam implements Serializable {
   @Schema(
      description = "操作系统版本ID"
   )
   private String id;
   @Schema(
      description = "操作系统类型ID"
   )
   private String osClassId;
   @Schema(
      description = "操作系统类型名称"
   )
   private String osClassName;
   @Schema(
      description = "操作系统名称"
   )
   private String osName;
   @Schema(
      description = "默认用户名"
   )
   private String defaultUserName;
   @Schema(
      description = "默认端口号"
   )
   private int defaultPort;
   @Schema(
      description = "端口模式：FIXED 固定，RANDOM 随机"
   )
   private String portMode;
   @Schema(
      description = "端口范围，随机模式使用，如：10000-20000"
   )
   private String portRange;
   @Schema(
      description = "镜像目录名称"
   )
   private String imageDirectoryName;
   @Schema(
      description = "状态"
   )
   private String status;
   @Schema(
      description = "支持的可用区"
   )
   private String supportedAvailabilityZone;
   @Schema(
      description = "支持的产品类型"
   )
   private String supportedProductType;
   @Schema(
      description = "受控类型(多选): 1=轻舟 Hyper-V受控 2=魔方云受控 3=RkHyper-v受控"
   )
   private List<String> controlledType;
   @Schema(
      description = "魔方云系统ID"
   )
   private String mfcSystemId;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;

   public String getId() {
      return this.id;
   }

   public String getOsClassId() {
      return this.osClassId;
   }

   public String getOsClassName() {
      return this.osClassName;
   }

   public String getOsName() {
      return this.osName;
   }

   public String getDefaultUserName() {
      return this.defaultUserName;
   }

   public int getDefaultPort() {
      return this.defaultPort;
   }

   public String getPortMode() {
      return this.portMode;
   }

   public String getPortRange() {
      return this.portRange;
   }

   public String getImageDirectoryName() {
      return this.imageDirectoryName;
   }

   public String getStatus() {
      return this.status;
   }

   public String getSupportedAvailabilityZone() {
      return this.supportedAvailabilityZone;
   }

   public String getSupportedProductType() {
      return this.supportedProductType;
   }

   public List<String> getControlledType() {
      return this.controlledType;
   }

   public String getMfcSystemId() {
      return this.mfcSystemId;
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

   public void setOsClassId(final String osClassId) {
      this.osClassId = osClassId;
   }

   public void setOsClassName(final String osClassName) {
      this.osClassName = osClassName;
   }

   public void setOsName(final String osName) {
      this.osName = osName;
   }

   public void setDefaultUserName(final String defaultUserName) {
      this.defaultUserName = defaultUserName;
   }

   public void setDefaultPort(final int defaultPort) {
      this.defaultPort = defaultPort;
   }

   public void setPortMode(final String portMode) {
      this.portMode = portMode;
   }

   public void setPortRange(final String portRange) {
      this.portRange = portRange;
   }

   public void setImageDirectoryName(final String imageDirectoryName) {
      this.imageDirectoryName = imageDirectoryName;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setSupportedAvailabilityZone(final String supportedAvailabilityZone) {
      this.supportedAvailabilityZone = supportedAvailabilityZone;
   }

   public void setSupportedProductType(final String supportedProductType) {
      this.supportedProductType = supportedProductType;
   }

   public void setControlledType(final List<String> controlledType) {
      this.controlledType = controlledType;
   }

   public void setMfcSystemId(final String mfcSystemId) {
      this.mfcSystemId = mfcSystemId;
   }

   public void setType(final Integer type) {
      this.type = type;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }
}
