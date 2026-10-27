package com.rxinyun.admin.system.modular.role.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SysRoleGrantUserParam {
   @Schema(
      description = "角色id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "授权用户信息",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "grantInfoList不能为空"
   )
   private List<String> grantInfoList;

   public String getId() {
      return this.id;
   }

   public List<String> getGrantInfoList() {
      return this.grantInfoList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGrantInfoList(final List<String> grantInfoList) {
      this.grantInfoList = grantInfoList;
   }
}
