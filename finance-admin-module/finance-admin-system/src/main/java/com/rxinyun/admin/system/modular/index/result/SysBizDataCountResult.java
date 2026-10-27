package com.rxinyun.admin.system.modular.index.result;

public class SysBizDataCountResult {
   private Long userCount;
   private Long roleCount;
   private Long orgCount;
   private Long positionCount;

   public Long getUserCount() {
      return this.userCount;
   }

   public Long getRoleCount() {
      return this.roleCount;
   }

   public Long getOrgCount() {
      return this.orgCount;
   }

   public Long getPositionCount() {
      return this.positionCount;
   }

   public void setUserCount(final Long userCount) {
      this.userCount = userCount;
   }

   public void setRoleCount(final Long roleCount) {
      this.roleCount = roleCount;
   }

   public void setOrgCount(final Long orgCount) {
      this.orgCount = orgCount;
   }

   public void setPositionCount(final Long positionCount) {
      this.positionCount = positionCount;
   }
}
