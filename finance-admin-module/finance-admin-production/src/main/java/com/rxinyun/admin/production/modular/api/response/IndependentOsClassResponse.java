package com.rxinyun.admin.production.modular.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(Include.ALWAYS)
@Schema(
   description = "独立服务器操作系统类别信息"
)
public class IndependentOsClassResponse {
   @Schema(
      description = "操作系统类别ID"
   )
   private String id;
   @Schema(
      description = "操作系统类别名称"
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

   public String getResourcePoolType() {
      return this.resourcePoolType;
   }

   public String getResourcePoolName() {
      return this.resourcePoolName;
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

   public void setResourcePoolType(final String resourcePoolType) {
      this.resourcePoolType = resourcePoolType;
   }

   public void setResourcePoolName(final String resourcePoolName) {
      this.resourcePoolName = resourcePoolName;
   }
}
