package com.rxinyun.admin.system.modular.user.provider;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.system.modular.user.result.SysLoginUser;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.auth.api.SaBaseLoginUserApi;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service("loginUserApi")
public class SysLoginUserApiProvider implements SaBaseLoginUserApi {
   @Resource
   private SysUserService sysUserService;

   public SaBaseLoginUser getUserById(String id) {
      return this.sysUserService.getUserById(id);
   }

   public SysLoginUser getUserByAccount(String account) {
      return this.sysUserService.getUserByAccount(account);
   }

   public SaBaseLoginUser getUserByPhone(String phone) {
      return this.sysUserService.getUserByPhone(phone);
   }

   public List<JSONObject> listUserByUserIdList(List<String> userIdList) {
      return this.sysUserService.listByIds(userIdList).stream().<JSONObject>map(JSONUtil::parseObj).collect(Collectors.toList());
   }

   public List<JSONObject> getRoleListByUserId(String userId) {
      return this.sysUserService.getRoleList(userId);
   }

   public List<String> getButtonCodeListListByUserAndRoleIdList(List<String> userAndRoleIdList) {
      return this.sysUserService.getButtonCodeList(userAndRoleIdList);
   }

   public List<String> getMobileButtonCodeListListByUserIdAndRoleIdList(List<String> userAndRoleIdList) {
      return this.sysUserService.getMobileButtonCodeList(userAndRoleIdList);
   }

   public List<JSONObject> getPermissionListByUserIdAndRoleIdList(List<String> userAndRoleIdList, String orgId) {
      return this.sysUserService.getPermissionList(userAndRoleIdList, orgId);
   }

   public void updateUserLoginInfo(String userId, String device) {
      this.sysUserService.updateUserLoginInfo(userId, device);
   }
}
