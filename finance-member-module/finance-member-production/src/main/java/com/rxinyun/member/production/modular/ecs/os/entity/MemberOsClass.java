package com.rxinyun.member.production.modular.ecs.os.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "OS_CLASS",
   autoResultMap = true
)
public class MemberOsClass extends CommonEntity {
   private static final long serialVersionUID = 810305140759422960L;
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
   @TableField("TYPE")
   private Integer type;
   @Schema(
      description = "资源池ID"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;
   @Schema(
      description = "资源池类型（当来自资源池时显示）"
   )
   @TableField(
      exist = false
   )
   private String resourcePoolType;
   @Schema(
      description = "资源池名称（当来自资源池时显示）"
   )
   @TableField(
      exist = false
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
