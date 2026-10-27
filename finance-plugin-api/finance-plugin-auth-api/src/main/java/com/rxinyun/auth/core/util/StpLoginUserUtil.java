package com.rxinyun.auth.core.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.common.util.CommonServletUtil;
import java.util.List;

public class StpLoginUserUtil {
   public static SaBaseLoginUser getLoginUser() {
      return (SaBaseLoginUser)StpUtil.getTokenSession().get("loginUser");
   }

   public static List<String> getLoginUserDataScope() {
      List<String> resultList = CollectionUtil.newArrayList(new String[0]);
      getLoginUser().getDataScopeList().forEach(dataScope -> {
         if (dataScope.getApiUrl().equals(CommonServletUtil.getRequest().getServletPath())) {
            resultList.addAll(dataScope.getDataScope());
         }
      });
      return resultList;
   }
}
