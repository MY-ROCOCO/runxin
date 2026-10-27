package com.rxinyun.admin.system.core.listener;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONArray;
import com.rxinyun.admin.system.core.enums.SysDataTypeEnum;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.listener.CommonDataChangeListener;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SysDataChangeListener implements CommonDataChangeListener {
   @Resource
   private CommonCacheOperator commonCacheOperator;

   public void doAddWithDataIdList(String dataType, List<String> dataIdList) {
      if (dataType.equals(SysDataTypeEnum.ORG.getValue())) {
         this.commonCacheOperator.remove(new String[]{"sys-org:all"});
         SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
         saBaseLoginUser.getDataScopeList().forEach(dataScope -> dataScope.getDataScope().addAll(dataIdList));
         saBaseLoginUser.setDataScopeList(saBaseLoginUser.getDataScopeList());
         StpUtil.getTokenSession().set("loginUser", saBaseLoginUser);
      }

      if (dataType.equals(SysDataTypeEnum.USER.getValue())) {
         this.commonCacheOperator.remove(new String[]{"sys-user:all"});
      }
   }

   public void doAddWithDataList(String dataType, JSONArray jsonArray) {
   }

   public void doUpdateWithDataIdList(String dataType, List<String> dataIdList) {
      if (dataType.equals(SysDataTypeEnum.ORG.getValue())) {
         this.commonCacheOperator.remove(new String[]{"sys-org:all"});
      }

      if (dataType.equals(SysDataTypeEnum.USER.getValue())) {
         this.commonCacheOperator.remove(new String[]{"sys-user:all"});
      }
   }

   public void doUpdateWithDataList(String dataType, JSONArray jsonArray) {
   }

   public void doDeleteWithDataIdList(String dataType, List<String> dataIdList) {
      if (dataType.equals(SysDataTypeEnum.ORG.getValue())) {
         this.commonCacheOperator.remove(new String[]{"sys-org:all"});
      }

      if (dataType.equals(SysDataTypeEnum.USER.getValue())) {
         this.commonCacheOperator.remove(new String[]{"sys-user:all"});
         dataIdList.forEach(StpUtil::kickout);
      }
   }
}
