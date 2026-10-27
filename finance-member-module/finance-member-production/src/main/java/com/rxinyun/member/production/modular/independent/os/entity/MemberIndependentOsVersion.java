package com.rxinyun.member.production.modular.independent.os.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "independent_os_version",
   autoResultMap = true
)
public class MemberIndependentOsVersion extends CommonEntity {
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
   private int defaultPort;
   @Schema(
      description = "默认用户名"
   )
   private String defaultUserName;
   @Schema(
      description = "操作系统唯一标识"
   )
   private String imageDirectoryName;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
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
      description = "资源池ID"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;

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

   public int getDefaultPort() {
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

   public void setDefaultPort(final int defaultPort) {
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
}
