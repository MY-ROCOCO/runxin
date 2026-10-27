package com.rxinyun.admin.production.modular.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.ALWAYS)
@Schema(
   description = "独立服务器操作系统版本信息"
)
public class IndependentOsVersionResponse {
   @Schema(
      description = "操作系统版本ID"
   )
   private String id;
   @Schema(
      description = "操作系统名称"
   )
   private String osName;
   @Schema(
      description = "操作系统类型ID"
   )
   private String osClassId;
   @Schema(
      description = "操作系统类型名称"
   )
   private String osClassName;
   @Schema(
      description = "默认端口号"
   )
   private Integer defaultPort;
   @Schema(
      description = "默认用户名"
   )
   private String defaultUserName;
   @Schema(
      description = "操作系统唯一标识"
   )
   private String imageDirectoryName;
   @Schema(
      description = "线路ID（操作系统版本绑定的线路ID）"
   )
   private String lineId;
   @Schema(
      description = "状态: 0 禁用 1 启用"
   )
   private Integer status;
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;
   @Schema(
      description = "资源池类型"
   )
   private String resourcePoolType;
   @Schema(
      description = "资源池名称"
   )
   private String resourcePoolName;

   public String getId() {
      return this.id;
   }

   public String getOsName() {
      return this.osName;
   }

   public String getOsClassId() {
      return this.osClassId;
   }

   public String getOsClassName() {
      return this.osClassName;
   }

   public Integer getDefaultPort() {
      return this.defaultPort;
   }

   public String getDefaultUserName() {
      return this.defaultUserName;
   }

   public String getImageDirectoryName() {
      return this.imageDirectoryName;
   }

   public String getLineId() {
      return this.lineId;
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

   public void setOsName(final String osName) {
      this.osName = osName;
   }

   public void setOsClassId(final String osClassId) {
      this.osClassId = osClassId;
   }

   public void setOsClassName(final String osClassName) {
      this.osClassName = osClassName;
   }

   public void setDefaultPort(final Integer defaultPort) {
      this.defaultPort = defaultPort;
   }

   public void setDefaultUserName(final String defaultUserName) {
      this.defaultUserName = defaultUserName;
   }

   public void setImageDirectoryName(final String imageDirectoryName) {
      this.imageDirectoryName = imageDirectoryName;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
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
