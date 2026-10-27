package com.rxinyun.admin.auth.modular.login.listener;

import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.auth.api.SaBaseLoginUserApi;
import com.rxinyun.auth.core.enums.SaClientTypeEnum;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.dev.api.DevLogApi;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthListener implements SaTokenListener {
   private static final Logger log = LoggerFactory.getLogger(AuthListener.class);
   @Resource
   private SaBaseLoginUserApi loginUserApi;
   @Resource
   private DevLogApi devLogApi;

   public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
      try {
         if (SaClientTypeEnum.B.getValue().equals(loginType)) {
            this.loginUserApi.updateUserLoginInfo(Convert.toStr(loginId), loginModel.getDevice());
            Object name = loginModel.getExtra("name");
            if (ObjectUtil.isNotEmpty(name)) {
               this.devLogApi.executeLoginLog(Convert.toStr(name));
            } else {
               this.devLogApi.executeLoginLog(null);
            }
         }
      } catch (Exception var6) {
         log.info(">>> 登录监听器异常：" + var6.getMessage());
      }
   }

   public void doLogout(String loginType, Object loginId, String tokenValue) {
      if (SaClientTypeEnum.B.getValue().equals(loginType)) {
         SaBaseLoginUser saBaseLoginUser = this.loginUserApi.getUserById(Convert.toStr(loginId));
         if (ObjectUtil.isNotEmpty(saBaseLoginUser)) {
            this.devLogApi.executeLogoutLog(saBaseLoginUser.getName());
         } else {
            this.devLogApi.executeLogoutLog(null);
         }
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
