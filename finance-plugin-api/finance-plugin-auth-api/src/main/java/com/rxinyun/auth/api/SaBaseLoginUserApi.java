package com.rxinyun.auth.api;

import cn.hutool.json.JSONObject;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import java.util.List;

public interface SaBaseLoginUserApi {
   SaBaseLoginUser getUserById(String id);

   SaBaseLoginUser getUserByAccount(String account);

   SaBaseLoginUser getUserByPhone(String phone);

   List<JSONObject> listUserByUserIdList(List<String> userIdList);

   List<JSONObject> getRoleListByUserId(String userId);

   List<String> getButtonCodeListListByUserAndRoleIdList(List<String> userAndRoleIdList);

   List<String> getMobileButtonCodeListListByUserIdAndRoleIdList(List<String> userAndRoleIdList);

   List<JSONObject> getPermissionListByUserIdAndRoleIdList(List<String> userAndRoleIdList, String orgId);

   void updateUserLoginInfo(String userId, String device);
}
