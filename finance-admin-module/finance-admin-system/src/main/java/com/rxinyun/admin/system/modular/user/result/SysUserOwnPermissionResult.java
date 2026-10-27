package com.rxinyun.admin.system.modular.user.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class SysUserOwnPermissionResult {
   @Schema(
      description = "角色id"
   )
   private String id;
   @Schema(
      description = "已授权权限信息"
   )
   private List<SysUserOwnPermissionResult.SysUserOwnPermission> grantInfoList;

   public String getId() {
      return this.id;
   }

   public List<SysUserOwnPermissionResult.SysUserOwnPermission> getGrantInfoList() {
      return this.grantInfoList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGrantInfoList(final List<SysUserOwnPermissionResult.SysUserOwnPermission> grantInfoList) {
      this.grantInfoList = grantInfoList;
   }

   public static class SysUserOwnPermission {
      @Schema(
         description = "接口地址"
      )
      private String apiUrl;
      @Schema(
         description = "数据范围分类"
      )
      private String scopeCategory;
      @Schema(
         description = "自定义范围组织id集合"
      )
      private List<String> scopeDefineOrgIdList;

      public String getApiUrl() {
         return this.apiUrl;
      }

      public String getScopeCategory() {
         return this.scopeCategory;
      }

      public List<String> getScopeDefineOrgIdList() {
         return this.scopeDefineOrgIdList;
      }

      public void setApiUrl(final String apiUrl) {
         this.apiUrl = apiUrl;
      }

      public void setScopeCategory(final String scopeCategory) {
         this.scopeCategory = scopeCategory;
      }

      public void setScopeDefineOrgIdList(final List<String> scopeDefineOrgIdList) {
         this.scopeDefineOrgIdList = scopeDefineOrgIdList;
      }
   }
}
