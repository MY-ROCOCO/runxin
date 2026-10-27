package com.rxinyun.admin.production.modular.independent.machine.param;

import com.rxinyun.admin.production.modular.independent.machine.entity.Machine;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class MachineVO extends Machine {
   @Schema(
      description = "机型 绑定的 线路名集合"
   )
   private List<String> lineName;
   @Schema(
      description = "机型 绑定的 地域名集合"
   )
   private List<String> regionName;
   @Schema(
      description = "资源池ID（当type=1资源池时，关联resource_pool.id）"
   )
   private String resourcePoolId;
   @Schema(
      description = "资源池类型（当type=1资源池时显示）"
   )
   private String resourcePoolType;
   @Schema(
      description = "资源池名称（当type=1资源池时显示）"
   )
   private String resourcePoolName;

   public List<String> getLineName() {
      return this.lineName;
   }

   public List<String> getRegionName() {
      return this.regionName;
   }

   @Override
   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getResourcePoolType() {
      return this.resourcePoolType;
   }

   public String getResourcePoolName() {
      return this.resourcePoolName;
   }

   public void setLineName(final List<String> lineName) {
      this.lineName = lineName;
   }

   public void setRegionName(final List<String> regionName) {
      this.regionName = regionName;
   }

   @Override
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
