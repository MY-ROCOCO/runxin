package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SysUserGrantRoleParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "角色id集合",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "roleIdList不能为空"
   )
   private List<String> roleIdList;

   public String getId() {
      return this.id;
   }

   public List<String> getRoleIdList() {
      return this.roleIdList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setRoleIdList(final List<String> roleIdList) {
      this.roleIdList = roleIdList;
   }
}
