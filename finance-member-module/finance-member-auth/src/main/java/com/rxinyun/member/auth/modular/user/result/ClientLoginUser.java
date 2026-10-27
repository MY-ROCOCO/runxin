package com.rxinyun.member.auth.modular.user.result;

import com.rxinyun.member.auth.modular.user.enums.ClientUserStatusEnum;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;

public class ClientLoginUser extends SaBaseClientLoginUser {
   public Boolean getEnabled() {
      return this.getUserStatus().equals(ClientUserStatusEnum.ENABLE.getValue());
   }
}
