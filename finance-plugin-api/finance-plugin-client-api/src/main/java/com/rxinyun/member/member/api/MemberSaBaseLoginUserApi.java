package com.rxinyun.member.member.api;

import cn.hutool.json.JSONObject;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import java.util.List;

public interface MemberSaBaseLoginUserApi {
   SaBaseClientLoginUser getClientUserById(Long id);

   SaBaseClientLoginUser getClientUserByAccount(String account);

   SaBaseClientLoginUser getClientUserByPhone(String phone);

   List<JSONObject> listUserByUserIdList(List<String> userIdList);

   List<JSONObject> getRoleListByUserId(String userId);

   List<String> getButtonCodeListListByUserAndRoleIdList(List<String> userAndRoleIdList);

   List<String> getMobileButtonCodeListListByUserIdAndRoleIdList(List<String> userAndRoleIdList);

   List<JSONObject> getPermissionListByUserIdAndRoleIdList(List<String> userAndRoleIdList, String orgId);

   void updateUserLoginInfo(Long userId, String device);
}
