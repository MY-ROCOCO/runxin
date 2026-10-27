package com.rxinyun.member.core.util;

import cn.hutool.core.collection.CollectionUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import java.util.List;

public class StpClientLoginUserUtil {
   public static SaBaseClientLoginUser getClientLoginUser() {
      return (SaBaseClientLoginUser)StpClientUtil.getTokenSession().get("loginUser");
   }

   public static List<String> getLoginUserDataScope() {
      return CollectionUtil.newArrayList(new String[0]);
   }

   public static void updateClientLoginUser(SaBaseClientLoginUser saBaseClientLoginUser) {
      StpClientUtil.getTokenSession().set("loginUser", saBaseClientLoginUser);
   }
}
