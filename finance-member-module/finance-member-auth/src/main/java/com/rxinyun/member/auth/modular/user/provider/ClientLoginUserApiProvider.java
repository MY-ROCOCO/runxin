package com.rxinyun.member.auth.modular.user.provider;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.member.auth.modular.user.result.ClientLoginUser;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ClientLoginUserApiProvider implements MemberSaBaseLoginUserApi {
   @Resource
   private ClientUserService clientUserService;

   public SaBaseClientLoginUser getClientUserById(Long id) {
      return this.clientUserService.getUserById(id);
   }

   public ClientLoginUser getClientUserByAccount(String account) {
      return this.clientUserService.getUserByAccount(account);
   }

   public SaBaseClientLoginUser getClientUserByPhone(String phone) {
      return this.clientUserService.getUserByPhone(phone);
   }

   public List<JSONObject> listUserByUserIdList(List<String> userIdList) {
      return this.clientUserService.listByIds(userIdList).stream().<JSONObject>map(JSONUtil::parseObj).collect(Collectors.toList());
   }

   public List<JSONObject> getRoleListByUserId(String userId) {
      return CollectionUtil.newArrayList(new JSONObject[0]);
   }

   public List<String> getButtonCodeListListByUserAndRoleIdList(List<String> userAndRoleIdList) {
      return CollectionUtil.newArrayList(new String[0]);
   }

   public List<String> getMobileButtonCodeListListByUserIdAndRoleIdList(List<String> userAndRoleIdList) {
      return CollectionUtil.newArrayList(new String[0]);
   }

   public List<JSONObject> getPermissionListByUserIdAndRoleIdList(List<String> userAndRoleIdList, String orgId) {
      return CollectionUtil.newArrayList(new JSONObject[0]);
   }

   public void updateUserLoginInfo(Long userId, String device) {
      this.clientUserService.updateUserLoginInfo(userId, device);
   }
}
