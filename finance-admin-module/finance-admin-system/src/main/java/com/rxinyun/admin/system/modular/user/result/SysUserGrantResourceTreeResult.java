package com.rxinyun.admin.system.modular.user.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class SysUserGrantResourceTreeResult {
   @Schema(
      description = "模块id"
   )
   private String id;
   @Schema(
      description = "模块名称"
   )
   private String title;
   @Schema(
      description = "模块图标"
   )
   private String icon;
   @Schema(
      description = "模块下菜单集合"
   )
   private List<SysUserGrantResourceTreeResult.SysUserGrantResourceMenuResult> menu;

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public String getIcon() {
      return this.icon;
   }

   public List<SysUserGrantResourceTreeResult.SysUserGrantResourceMenuResult> getMenu() {
      return this.menu;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setIcon(final String icon) {
      this.icon = icon;
   }

   public void setMenu(final List<SysUserGrantResourceTreeResult.SysUserGrantResourceMenuResult> menu) {
      this.menu = menu;
   }

   public static class SysUserGrantResourceMenuResult {
      @Schema(
         description = "菜单id"
      )
      private String id;
      @Schema(
         description = "父id"
      )
      private String parentId;
      @Schema(
         description = "菜单名称"
      )
      private String parentName;
      @Schema(
         description = "菜单标题"
      )
      private String title;
      @Schema(
         description = "菜单模块"
      )
      private String module;
      @Schema(
         description = "菜单下按钮集合"
      )
      private List<SysUserGrantResourceTreeResult.SysUserGrantResourceMenuResult.SysUserGrantResourceButtonResult> button;

      public String getId() {
         return this.id;
      }

      public String getParentId() {
         return this.parentId;
      }

      public String getParentName() {
         return this.parentName;
      }

      public String getTitle() {
         return this.title;
      }

      public String getModule() {
         return this.module;
      }

      public List<SysUserGrantResourceTreeResult.SysUserGrantResourceMenuResult.SysUserGrantResourceButtonResult> getButton() {
         return this.button;
      }

      public void setId(final String id) {
         this.id = id;
      }

      public void setParentId(final String parentId) {
         this.parentId = parentId;
      }

      public void setParentName(final String parentName) {
         this.parentName = parentName;
      }

      public void setTitle(final String title) {
         this.title = title;
      }

      public void setModule(final String module) {
         this.module = module;
      }

      public void setButton(final List<SysUserGrantResourceTreeResult.SysUserGrantResourceMenuResult.SysUserGrantResourceButtonResult> button) {
         this.button = button;
      }

      public static class SysUserGrantResourceButtonResult {
         @Schema(
            description = "按钮id"
         )
         private String id;
         @Schema(
            description = "按钮标题"
         )
         private String title;

         public String getId() {
            return this.id;
         }

         public String getTitle() {
            return this.title;
         }

         public void setId(final String id) {
            this.id = id;
         }

         public void setTitle(final String title) {
            this.title = title;
         }
      }
   }
}
