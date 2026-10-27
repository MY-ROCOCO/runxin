package com.rxinyun.member.auth.modular.login.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.rxinyun.dev.api.MemberLogApi;
import com.rxinyun.member.core.enums.SaClientTypeEnum;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberAuthListener implements SaTokenListener {
   private static final Logger log = LoggerFactory.getLogger(MemberAuthListener.class);
   @Resource
   private MemberSaBaseLoginUserApi clientLoginUserApi;
   @Resource
   private MemberLogApi memberLogApi;

   public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
      log.info(">>> 会员登录触发：{}, {}", Convert.toLong(loginId), JSON.toJSONString(loginModel));
      if (SaClientTypeEnum.C.getValue().equals(loginType)) {
         this.clientLoginUserApi.updateUserLoginInfo(Convert.toLong(loginId), loginModel.getDevice());
         Object name = loginModel.getExtra("account");
         if (ObjectUtil.isNotEmpty(name)) {
            this.memberLogApi.executeLoginLog(String.valueOf(loginId), Convert.toStr(name));
         } else {
            this.memberLogApi.executeLoginLog(String.valueOf(loginId), null);
         }
      }
   }

   public void doLogout(String loginType, Object loginId, String tokenValue) {
      if (SaClientTypeEnum.C.getValue().equals(loginType)) {
         this.memberLogApi.executeLogoutLog(String.valueOf(loginId), null);
      }
   }

   public void doKickout(String loginType, Object loginId, String tokenValue) {
   }

   public void doReplaced(String loginType, Object loginId, String tokenValue) {
   }

   public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
   }

   public void doUntieDisable(String loginType, Object loginId, String service) {
   }

   public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
   }

   public void doCloseSafe(String loginType, String tokenValue, String service) {
   }

   public void doCreateSession(String id) {
   }

   public void doLogoutSession(String id) {
   }

   public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
   }
}
