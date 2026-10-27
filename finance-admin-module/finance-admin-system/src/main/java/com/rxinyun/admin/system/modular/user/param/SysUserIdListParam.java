package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SysUserIdListParam {
   @Schema(
      description = "id集合",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "idList不能为空"
   )
   private List<String> idList;

   public List<String> getIdList() {
      return this.idList;
   }

   public void setIdList(final List<String> idList) {
      this.idList = idList;
   }
}
