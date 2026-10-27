package com.rxinyun.member.auth.modular.login.provider;

import com.rxinyun.member.auth.modular.login.service.MemberAuthService;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.production.api.CurrentMemberIdApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CurrentMemberIdApiProvider implements CurrentMemberIdApi {
   @Resource
   private MemberAuthService memberAuthService;

   public String getCurrentMemberId() {
      SaBaseClientLoginUser user = this.memberAuthService.getClientLoginUser();
      return user != null && user.getId() != null ? String.valueOf(user.getId()) : null;
   }
}
