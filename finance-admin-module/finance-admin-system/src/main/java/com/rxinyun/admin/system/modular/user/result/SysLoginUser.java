package com.rxinyun.admin.system.modular.user.result;

import com.rxinyun.admin.system.modular.user.enums.SysUserStatusEnum;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;

public class SysLoginUser extends SaBaseLoginUser {
   public Boolean getEnabled() {
      return this.getUserStatus().equals(SysUserStatusEnum.ENABLE.getValue());
   }
}
