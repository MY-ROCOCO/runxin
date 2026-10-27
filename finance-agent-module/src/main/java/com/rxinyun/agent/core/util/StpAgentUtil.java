package com.rxinyun.agent.core.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;

public class StpAgentUtil {
   public static final String TYPE = "A";

   private StpAgentUtil() {
   }

   private static StpLogic stpLogic() {
      return SaManager.getStpLogic("A");
   }

   public static void login(Object loginId, SaLoginModel loginModel) {
      stpLogic().login(loginId, loginModel);
   }

   public static void logout() {
      stpLogic().logout();
   }

   public static boolean isLogin() {
      return stpLogic().isLogin();
   }

   public static Object getLoginId() {
      return stpLogic().getLoginId();
   }

   public static SaTokenInfo getTokenInfo() {
      return stpLogic().getTokenInfo();
   }

   public static SaSession getTokenSession() {
      return stpLogic().getTokenSession();
   }
}
