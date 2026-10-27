package com.rxinyun.admin.system.modular.role.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class SysRoleSelectorRoleParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "组织id"
   )
   private String orgId;
   @Schema(
      description = "角色分类"
   )
   private String category;
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;
   @Schema(
      description = "数据范围信息"
   )
   private List<String> dataScopeList;
   @Schema(
      description = "是否排除超管"
   )
   private boolean excludeSuperAdmin = false;

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getOrgId() {
      return this.orgId;
   }

   public String getCategory() {
      return this.category;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public List<String> getDataScopeList() {
      return this.dataScopeList;
   }

   public boolean isExcludeSuperAdmin() {
      return this.excludeSuperAdmin;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setOrgId(final String orgId) {
      this.orgId = orgId;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setDataScopeList(final List<String> dataScopeList) {
      this.dataScopeList = dataScopeList;
   }

   public void setExcludeSuperAdmin(final boolean excludeSuperAdmin) {
      this.excludeSuperAdmin = excludeSuperAdmin;
   }
}
