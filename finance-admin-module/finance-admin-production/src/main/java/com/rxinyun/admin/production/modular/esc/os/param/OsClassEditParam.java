package com.rxinyun.admin.production.modular.esc.os.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class OsClassEditParam {
   @Schema(
      description = "操作系统类型标识"
   )
   private String id;
   @Schema(
      description = "操作系统类型名称"
   )
   private String osClassName;
   @Schema(
      description = "备注"
   )
   private String remark;
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

   public String getId() {
      return this.id;
   }

   public String getOsClassName() {
      return this.osClassName;
   }

   public String getRemark() {
      return this.remark;
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

   public void setOsClassName(final String osClassName) {
      this.osClassName = osClassName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
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
