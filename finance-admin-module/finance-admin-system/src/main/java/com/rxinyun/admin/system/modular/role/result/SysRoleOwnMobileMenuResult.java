package com.rxinyun.admin.system.modular.role.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class SysRoleOwnMobileMenuResult {
   @Schema(
      description = "角色id"
   )
   private String id;
   @Schema(
      description = "已授权移动端菜单信息"
   )
   private List<SysRoleOwnMobileMenuResult.SysRoleOwnMobileMenu> grantInfoList;

   public String getId() {
      return this.id;
   }

   public List<SysRoleOwnMobileMenuResult.SysRoleOwnMobileMenu> getGrantInfoList() {
      return this.grantInfoList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGrantInfoList(final List<SysRoleOwnMobileMenuResult.SysRoleOwnMobileMenu> grantInfoList) {
      this.grantInfoList = grantInfoList;
   }

   public static class SysRoleOwnMobileMenu {
      @Schema(
         description = "菜单id"
      )
      private String menuId;
      @Schema(
         description = "按钮id集合"
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
