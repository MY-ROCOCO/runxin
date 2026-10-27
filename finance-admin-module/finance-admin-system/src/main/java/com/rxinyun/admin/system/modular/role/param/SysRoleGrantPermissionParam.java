package com.rxinyun.admin.system.modular.role.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SysRoleGrantPermissionParam {
   @Schema(
      description = "角色id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Valid
   @Schema(
      description = "授权权限信息",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "grantInfoList不能为空"
   )
   private List<SysRoleGrantPermissionParam.SysRoleGrantPermission> grantInfoList;

   public String getId() {
      return this.id;
   }

   public List<SysRoleGrantPermissionParam.SysRoleGrantPermission> getGrantInfoList() {
      return this.grantInfoList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGrantInfoList(final List<SysRoleGrantPermissionParam.SysRoleGrantPermission> grantInfoList) {
      this.grantInfoList = grantInfoList;
   }

   public static class SysRoleGrantPermission {
      @Schema(
         description = "接口地址"
      )
      @NotBlank(
         message = "apiUrl不能为空"
      )
      private String apiUrl;
      @Schema(
         description = "数据范围分类"
      )
      @NotBlank(
         message = "scopeCategory不能为空"
      )
      private String scopeCategory;
      @Schema(
         description = "自定义范围组织id集合"
      )
      @NotNull(
         message = "scopeDefineOrgIdList不能为空"
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
