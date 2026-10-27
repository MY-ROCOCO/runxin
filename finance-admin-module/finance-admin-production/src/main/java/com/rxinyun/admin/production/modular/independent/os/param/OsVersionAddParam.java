package com.rxinyun.admin.production.modular.independent.os.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class OsVersionAddParam implements Serializable {
   @NotBlank(
      message = "操作系统名称不能为空"
   )
   @Schema(
      description = "操作系统名称"
   )
   private String osName;
   @NotBlank(
      message = "操作系统类型id不能为空"
   )
   @Schema(
      description = "操作系统类型ID"
   )
   private String osClassId;
   @Schema(
      description = "操作系统类型名称"
   )
   private String osClassName;
   @NotBlank(
      message = "用户名不能为空"
   )
   @Schema(
      description = "默认用户名"
   )
   private String defaultUserName;
   @NotNull(
      message = "端口号不能为空"
   )
   @Schema(
      description = "默认端口号"
   )
   private int defaultPort;
   @NotBlank(
      message = "操作系统唯一标识不能为空"
   )
   @Schema(
      description = "操作系统唯一标识--镜像目录名称"
   )
   private String imageDirectoryName;
   @NotBlank(
      message = "线路id不能为空"
   )
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "状态: 0 禁用 1 启用 默认1"
   )
   private Integer status;
   @NotNull(
      message = "资源类型不能为空"
   )
   @Schema(
      description = "资源类型: 0 自生产 1 资源池"
   )
   private Integer type;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;

   public String getOsName() {
      return this.osName;
   }

   public String getOsClassId() {
      return this.osClassId;
   }

   public String getOsClassName() {
      return this.osClassName;
   }

   public String getDefaultUserName() {
      return this.defaultUserName;
   }

   public int getDefaultPort() {
      return this.defaultPort;
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

   public void setOsName(final String osName) {
      this.osName = osName;
   }

   public void setOsClassId(final String osClassId) {
      this.osClassId = osClassId;
   }

   public void setOsClassName(final String osClassName) {
      this.osClassName = osClassName;
   }

   public void setDefaultUserName(final String defaultUserName) {
      this.defaultUserName = defaultUserName;
   }

   public void setDefaultPort(final int defaultPort) {
      this.defaultPort = defaultPort;
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
}
