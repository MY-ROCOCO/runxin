package com.rxinyun.admin.system.modular.role.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SysRoleGrantMobileMenuParam {
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
      description = "授权移动端菜单信息",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "grantInfoList不能为空"
   )
   private List<SysRoleGrantMobileMenuParam.SysRoleGrantMobileMenu> grantInfoList;

   public String getId() {
      return this.id;
   }

   public List<SysRoleGrantMobileMenuParam.SysRoleGrantMobileMenu> getGrantInfoList() {
      return this.grantInfoList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGrantInfoList(final List<SysRoleGrantMobileMenuParam.SysRoleGrantMobileMenu> grantInfoList) {
      this.grantInfoList = grantInfoList;
   }

   public static class SysRoleGrantMobileMenu {
      @Schema(
         description = "菜单id"
      )
      @NotBlank(
         message = "menuId不能为空"
      )
      private String menuId;
      @Schema(
         description = "按钮id集合"
      )
      @NotNull(
         message = "buttonInfo不能为空"
      )
      private List<String> buttonInfo;

      public String getMenuId() {
         return this.menuId;
      }

      public List<String> getButtonInfo() {
         return this.buttonInfo;
      }

      public void setMenuId(final String menuId) {
         this.menuId = menuId;
      }

      public void setButtonInfo(final List<String> buttonInfo) {
         this.buttonInfo = buttonInfo;
      }
   }
}
