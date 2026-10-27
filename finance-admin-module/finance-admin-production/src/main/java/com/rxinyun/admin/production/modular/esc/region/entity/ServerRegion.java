package com.rxinyun.admin.production.modular.esc.region.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "SERVER_REGION",
   autoResultMap = true
)
public class ServerRegion extends CommonEntity {
   private static final long serialVersionUID = 810305140759422960L;
   @Schema(
      description = "地域标识"
   )
   private String id;
   @Schema(
      description = "地域名称"
   )
   private String regionName;
   @Schema(
      description = "排序"
   )
   private Integer sortId;
   @Schema(
      description = "备注"
   )
   private String remark;
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
      description = "资源池ID（当地域来自资源池时，关联resource_pool.id）"
   )
   @TableField("RESOURCE_POOL_ID")
   private String resourcePoolId;
   @Schema(
      description = "资源池类型（当地域来自资源池时显示）"
   )
   @TableField(
      exist = false
   )
   private String resourcePoolType;
   @Schema(
      description = "资源池名称（当地域来自资源池时显示）"
   )
   @TableField(
      exist = false
   )
   private String resourcePoolName;

   public String getId() {
      return this.id;
   }

   public String getRegionName() {
      return this.regionName;
   }

   public Integer getSortId() {
      return this.sortId;
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

   public void setRegionName(final String regionName) {
      this.regionName = regionName;
   }

   public void setSortId(final Integer sortId) {
      this.sortId = sortId;
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
