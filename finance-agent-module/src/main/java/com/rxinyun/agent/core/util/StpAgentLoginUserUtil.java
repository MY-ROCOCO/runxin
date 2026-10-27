package com.rxinyun.agent.core.util;

import com.rxinyun.agent.modular.auth.result.AgentLoginUser;

public class StpAgentLoginUserUtil {
   private StpAgentLoginUserUtil() {
   }

   public static AgentLoginUser getLoginUser() {
      return (AgentLoginUser)StpAgentUtil.getTokenSession().get("loginUser");
   }

   public static void updateLoginUser(AgentLoginUser loginUser) {
      StpAgentUtil.getTokenSession().set("loginUser", loginUser);
   }
}
