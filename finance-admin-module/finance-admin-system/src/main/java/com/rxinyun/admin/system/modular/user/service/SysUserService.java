package com.rxinyun.admin.system.modular.user.service;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.param.SysUserAddParam;
import com.rxinyun.admin.system.modular.user.param.SysUserAdminUpdatePwdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserEditParam;
import com.rxinyun.admin.system.modular.user.param.SysUserExportParam;
import com.rxinyun.admin.system.modular.user.param.SysUserFindPwdByEmailParam;
import com.rxinyun.admin.system.modular.user.param.SysUserFindPwdByPhoneParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGetEmailValidCodeParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGetPhoneValidCodeParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantPermissionParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantResourceParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdListParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserMessageIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserMessagePageParam;
import com.rxinyun.admin.system.modular.user.param.SysUserPageParam;
import com.rxinyun.admin.system.modular.user.param.SysUserResetPasswordParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorOrgListParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorPositionParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorUserParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSignatureParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdateInfoParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdatePwdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdateWorkbenchParam;
import com.rxinyun.admin.system.modular.user.result.SysLoginUser;
import com.rxinyun.admin.system.modular.user.result.SysUserMessageDetailResult;
import com.rxinyun.admin.system.modular.user.result.SysUserMessageResult;
import com.rxinyun.admin.system.modular.user.result.SysUserOwnPermissionResult;
import com.rxinyun.admin.system.modular.user.result.SysUserOwnResourceResult;
import com.rxinyun.admin.system.modular.user.result.SysUserPicValidCodeResult;
import com.rxinyun.admin.system.modular.user.result.SysUserPositionResult;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface SysUserService extends IService<SysUser> {
   SysLoginUser getUserById(String id);

   SysLoginUser getUserByAccount(String account);

   SysLoginUser getUserByPhone(String phone);

   SysLoginUser getUserByEmail(String email);

   Page<SysUser> page(SysUserPageParam sysUserPageParam);

   void add(SysUserAddParam sysUserAddParam);

   void edit(SysUserEditParam sysUserEditParam);

   void delete(List<SysUserIdParam> sysUserIdParamList);

   SysUser detail(SysUserIdParam sysUserIdParam);

   SysUser queryEntity(String id);

   void disableUser(SysUserIdParam sysUserIdParam);

   void enableUser(SysUserIdParam sysUserIdParam);

   void resetPassword(SysUserResetPasswordParam sysUserResetPasswordParam);

   SysUserPicValidCodeResult getPicCaptcha();

   String findPasswordGetPhoneValidCode(SysUserGetPhoneValidCodeParam sysUserGetPhoneValidCodeParam);

   String findPasswordGetEmailValidCode(SysUserGetEmailValidCodeParam sysUserGetEmailValidCodeParam);

   void findPasswordByPhone(SysUserFindPwdByPhoneParam sysUserFindPwdByPhoneParam);

   void findPasswordByEmail(SysUserFindPwdByEmailParam sysUserFindPwdByEmailParam);

   void updatePassword(SysUserAdminUpdatePwdParam sysUserAdminUpdatePwdParam);

   void updatePassword(SysUserUpdatePwdParam sysUserUpdatePwdParam);

   String updateAvatar(MultipartFile file);

   void updateSignature(SysUserSignatureParam sysUserSignatureParam);

   void updateUserLoginInfo(String userId, String device);

   List<Tree<String>> ownMenu(SysUserIdParam sysUserIdParam);

   List<Tree<String>> ownMobileMenu(SysUserIdParam sysUserIdParam);

   List<String> ownRole(SysUserIdParam sysUserIdParam);

   void grantRole(SysUserGrantRoleParam sysUserGrantRoleParam);

   SysUserOwnResourceResult ownResource(SysUserIdParam sysUserIdParam);

   void grantResource(SysUserGrantResourceParam sysUserGrantResourceParam);

   SysUserOwnPermissionResult ownPermission(SysUserIdParam sysUserIdParam);

   void grantPermission(SysUserGrantPermissionParam sysUserGrantPermissionParam);

   List<Tree<String>> loginOrgTree(SysUserIdParam sysUserIdParam);

   void updateUserInfo(SysUserUpdateInfoParam sysUserUpdateInfoParam);

   void updateUserWorkbench(SysUserUpdateWorkbenchParam sysUserUpdateWorkbenchParam);

   String loginWorkbench(SysUserIdParam sysUserIdParam);

   List<JSONObject> getRoleList(String userId);

   List<String> getButtonCodeList(List<String> userAndRoleIdList);

   List<String> getMobileButtonCodeList(List<String> userAndRoleIdList);

   List<JSONObject> getPermissionList(List<String> userAndRoleIdList, String orgId);

   void downloadImportUserTemplate(HttpServletResponse response) throws IOException;

   JSONObject importUser(MultipartFile file);

   void exportUser(SysUserExportParam sysUserExportParam, HttpServletResponse response) throws IOException;

   void exportUserInfo(SysUserIdParam sysUserIdParam, HttpServletResponse response) throws IOException;

   List<SysUserPositionResult> loginPositionInfo(SysUserIdParam sysUserIdParam);

   Page<SysUser> getAllUserSelectorList();

   List<Tree<String>> orgTreeSelector();

   Page<SysOrg> orgListSelector(SysUserSelectorOrgListParam sysUserSelectorOrgListParam);

   Page<SysPosition> positionSelector(SysUserSelectorPositionParam sysUserSelectorPositionParam);

   Page<SysRole> roleSelector(SysUserSelectorRoleParam sysUserSelectorRoleParam);

   Page<SysUser> userSelector(SysUserSelectorUserParam sysUserSelectorUserParam);

   Page<SysUserMessageResult> loginMessagePage(SysUserMessagePageParam sysUserMessagePageParam);

   SysUserMessageDetailResult loginMessageDetail(SysUserMessageIdParam sysUserMessageIdParam);

   List<SysOrg> getOrgListByIdList(SysUserIdListParam sysUserIdListParam);

   List<SysUser> getUserListByIdList(SysUserIdListParam sysUserIdListParam);

   List<SysPosition> getPositionListByIdList(SysUserIdListParam sysUserIdListParam);

   List<SysRole> getRoleListByIdList(SysUserIdListParam sysUserIdListParam);
}
