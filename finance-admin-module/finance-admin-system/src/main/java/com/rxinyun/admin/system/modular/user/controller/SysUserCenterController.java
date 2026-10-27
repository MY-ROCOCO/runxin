package com.rxinyun.admin.system.modular.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.org.entity.SysOrg;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.param.SysUserFindPwdByEmailParam;
import com.rxinyun.admin.system.modular.user.param.SysUserFindPwdByPhoneParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGetEmailValidCodeParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGetPhoneValidCodeParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdListParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserMessageIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserMessagePageParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSignatureParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdateInfoParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdatePwdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserUpdateWorkbenchParam;
import com.rxinyun.admin.system.modular.user.result.SysUserMessageDetailResult;
import com.rxinyun.admin.system.modular.user.result.SysUserMessageResult;
import com.rxinyun.admin.system.modular.user.result.SysUserPicValidCodeResult;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
   name = "用户个人控制器"
)
@RestController
@Validated
public class SysUserCenterController {
   @Resource
   private SysUserService sysUserService;

   @Operation(
      summary = "获取图片验证码"
   )
   @GetMapping({"/admin/sys/userCenter/getPicCaptcha"})
   public CommonResult<SysUserPicValidCodeResult> getPicCaptcha() {
      return CommonResult.data(this.sysUserService.getPicCaptcha());
   }

   @Operation(
      summary = "找回密码获取手机验证码"
   )
   @GetMapping({"/admin/sys/userCenter/findPasswordGetPhoneValidCode"})
   public CommonResult<String> findPasswordGetPhoneValidCode(@Valid SysUserGetPhoneValidCodeParam sysUserGetPhoneValidCodeParam) {
      return CommonResult.data(this.sysUserService.findPasswordGetPhoneValidCode(sysUserGetPhoneValidCodeParam));
   }

   @Operation(
      summary = "找回密码获取邮箱验证码"
   )
   @GetMapping({"/admin/sys/userCenter/findPasswordGetEmailValidCode"})
   public CommonResult<String> findPasswordGetEmailValidCode(@Valid SysUserGetEmailValidCodeParam sysUserGetEmailValidCodeParam) {
      return CommonResult.data(this.sysUserService.findPasswordGetEmailValidCode(sysUserGetEmailValidCodeParam));
   }

   @Operation(
      summary = "通过手机号找回用户密码"
   )
   @CommonLog("通过手机号找回用户密码")
   @PostMapping({"/admin/sys/userCenter/findPasswordByPhone"})
   public CommonResult<String> findPasswordByPhone(@RequestBody @Valid SysUserFindPwdByPhoneParam sysUserFindPwdByPhoneParam) {
      this.sysUserService.findPasswordByPhone(sysUserFindPwdByPhoneParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "通过邮箱找回用户密码"
   )
   @CommonLog("通过邮箱找回用户密码")
   @PostMapping({"/admin/sys/userCenter/findPasswordByEmail"})
   public CommonResult<String> findPasswordByEmail(@RequestBody @Valid SysUserFindPwdByEmailParam sysUserFindPwdByEmailParam) {
      this.sysUserService.findPasswordByEmail(sysUserFindPwdByEmailParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改用户密码"
   )
   @CommonLog("修改用户密码")
   @PostMapping({"/admin/sys/userCenter/updatePassword"})
   public CommonResult<String> updatePassword(@RequestBody @Valid SysUserUpdatePwdParam sysUserUpdatePwdParam) {
      this.sysUserService.updatePassword(sysUserUpdatePwdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改用户头像"
   )
   @CommonLog("修改用户头像")
   @PostMapping({"/admin/sys/userCenter/updateAvatar"})
   public CommonResult<String> updateAvatar(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.sysUserService.updateAvatar(file));
   }

   @Operation(
      summary = "修改用户签名图片"
   )
   @CommonLog("修改用户签名图片")
   @PostMapping({"/admin/sys/userCenter/updateSignature"})
   public CommonResult<String> updateSignature(@RequestBody @Valid SysUserSignatureParam sysUserSignatureParam) {
      this.sysUserService.updateSignature(sysUserSignatureParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取登录用户PC端菜单"
   )
   @GetMapping({"/admin/sys/userCenter/loginMenu"})
   public CommonResult<List<Tree<String>>> loginMenu() {
      SysUserIdParam sysUserIdParam = new SysUserIdParam();
      sysUserIdParam.setId(StpUtil.getLoginIdAsString());
      return CommonResult.data(this.sysUserService.ownMenu(sysUserIdParam));
   }

   @Operation(
      summary = "获取登录用户移动端菜单"
   )
   @GetMapping({"/admin/sys/userCenter/loginMobileMenu"})
   public CommonResult<List<Tree<String>>> loginMobileMenu() {
      SysUserIdParam sysUserIdParam = new SysUserIdParam();
      sysUserIdParam.setId(StpUtil.getLoginIdAsString());
      return CommonResult.data(this.sysUserService.ownMobileMenu(sysUserIdParam));
   }

   @Operation(
      summary = "编辑个人信息"
   )
   @CommonLog("编辑个人信息")
   @PostMapping({"/admin/sys/userCenter/updateUserInfo"})
   public CommonResult<String> updateUserInfo(@RequestBody @Valid SysUserUpdateInfoParam sysUserUpdateInfoParam) {
      this.sysUserService.updateUserInfo(sysUserUpdateInfoParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑个人工作台"
   )
   @CommonLog("编辑个人工作台")
   @PostMapping({"/admin/sys/userCenter/updateUserWorkbench"})
   public CommonResult<String> updateUserWorkbench(@RequestBody @Valid SysUserUpdateWorkbenchParam sysUserUpdateWorkbenchParam) {
      this.sysUserService.updateUserWorkbench(sysUserUpdateWorkbenchParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取登录用户的工作台"
   )
   @GetMapping({"/admin/sys/userCenter/loginWorkbench"})
   public CommonResult<String> loginWorkbench() {
      SysUserIdParam sysUserIdParam = new SysUserIdParam();
      sysUserIdParam.setId(StpUtil.getLoginIdAsString());
      return CommonResult.data(this.sysUserService.loginWorkbench(sysUserIdParam));
   }

   @Operation(
      summary = "获取登录用户的站内信分页"
   )
   @GetMapping({"/admin/sys/userCenter/loginUnreadMessagePage"})
   public CommonResult<Page<SysUserMessageResult>> loginMessagePage(SysUserMessagePageParam sysUserMessagePageParam) {
      return CommonResult.data(this.sysUserService.loginMessagePage(sysUserMessagePageParam));
   }

   @Operation(
      summary = "读取登录用户站内信详情"
   )
   @GetMapping({"/admin/sys/userCenter/loginUnreadMessageDetail"})
   public CommonResult<SysUserMessageDetailResult> loginMessageDetail(@Valid SysUserMessageIdParam sysUserMessageIdParam) {
      return CommonResult.data(this.sysUserService.loginMessageDetail(sysUserMessageIdParam));
   }

   @Operation(
      summary = "根据id集合获取组织集合"
   )
   @PostMapping({"/admin/sys/userCenter/getOrgListByIdList"})
   public CommonResult<List<SysOrg>> getOrgListByIdList(@RequestBody @Valid SysUserIdListParam sysUserIdListParam) {
      return CommonResult.data(this.sysUserService.getOrgListByIdList(sysUserIdListParam));
   }

   @Operation(
      summary = "根据id集合获取用户集合"
   )
   @PostMapping({"/admin/sys/userCenter/getUserListByIdList"})
   public CommonResult<List<SysUser>> getUserListByIdList(@RequestBody @Valid SysUserIdListParam sysUserIdListParam) {
      return CommonResult.data(this.sysUserService.getUserListByIdList(sysUserIdListParam));
   }

   @Operation(
      summary = "根据id集合获取职位集合"
   )
   @PostMapping({"/admin/sys/userCenter/getPositionListByIdList"})
   public CommonResult<List<SysPosition>> getPositionListByIdList(@RequestBody @Valid SysUserIdListParam sysUserIdListParam) {
      return CommonResult.data(this.sysUserService.getPositionListByIdList(sysUserIdListParam));
   }

   @Operation(
      summary = "根据id集合获取角色集合"
   )
   @PostMapping({"/admin/sys/userCenter/getRoleListByIdList"})
   public CommonResult<List<SysRole>> getRoleListByIdList(@RequestBody @Valid SysUserIdListParam sysUserIdListParam) {
      return CommonResult.data(this.sysUserService.getRoleListByIdList(sysUserIdListParam));
   }
}
