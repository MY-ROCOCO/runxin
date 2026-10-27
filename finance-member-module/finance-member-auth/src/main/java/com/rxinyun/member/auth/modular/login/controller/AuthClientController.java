package com.rxinyun.member.auth.modular.login.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevTelegramBindingApi;
import com.rxinyun.dev.pojo.DevTelegramBindLinkInfo;
import com.rxinyun.member.auth.modular.login.param.AuthAccountPasswordLoginParam;
import com.rxinyun.member.auth.modular.login.param.AuthBindEmailParam;
import com.rxinyun.member.auth.modular.login.param.AuthBindPhoneParam;
import com.rxinyun.member.auth.modular.login.param.AuthCheckAccountParam;
import com.rxinyun.member.auth.modular.login.param.AuthGetEmailValidCodeParam;
import com.rxinyun.member.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.rxinyun.member.auth.modular.login.param.AuthPhoneValidCodeLoginParam;
import com.rxinyun.member.auth.modular.login.param.AuthResetLoginPasswordParam;
import com.rxinyun.member.auth.modular.login.param.AuthResetPasswordByEmailParam;
import com.rxinyun.member.auth.modular.login.param.AuthSwitchLoginParam;
import com.rxinyun.member.auth.modular.login.param.AuthUpdateLoginPasswordParam;
import com.rxinyun.member.auth.modular.login.result.AuthPicValidCodeResult;
import com.rxinyun.member.auth.modular.login.service.MemberAuthService;
import com.rxinyun.member.auth.modular.user.entity.ClientUser;
import com.rxinyun.member.core.enums.SaClientTypeEnum;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "C端登录控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class AuthClientController {
   @Resource
   private MemberAuthService authService;
   @Resource
   private DevTelegramBindingApi devTelegramBindingApi;

   @Operation(
      summary = "C端获取图片验证码"
   )
   @GetMapping({"/auth/c/getPicCaptcha"})
   public CommonResult<AuthPicValidCodeResult> getPicCaptcha() {
      return CommonResult.data(this.authService.getPicCaptcha(SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端手机登录验证码获取"
   )
   @GetMapping({"/auth/c/getLoginValidCode"})
   public CommonResult<String> getLoginValidCode(@Valid AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
      return CommonResult.data(this.authService.getLoginValidCode(authGetPhoneValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端重置密码获取手机验证码"
   )
   @GetMapping({"/auth/c/getResetLoginValidCode"})
   public CommonResult<String> getResetLoginValidCode(@Valid AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
      return CommonResult.data(this.authService.getResetLoginValidCode(authGetPhoneValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端重置密码获取邮箱验证码"
   )
   @GetMapping({"/auth/c/getResetPasswordEmailValidCode"})
   public CommonResult<String> getResetPasswordEmailValidCode(@Valid AuthGetEmailValidCodeParam authGetEmailValidCodeParam) {
      return CommonResult.data(this.authService.getResetPasswordEmailValidCode(authGetEmailValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端账号密码登录"
   )
   @PostMapping({"/auth/c/doLogin"})
   public CommonResult<String> doLogin(@RequestBody @Valid AuthAccountPasswordLoginParam authAccountPasswordLoginParam) {
      return CommonResult.data(this.authService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端修改登录密码"
   )
   @PostMapping({"/auth/c/updatePassword"})
   public CommonResult<String> updatePassword(@RequestBody @Valid AuthUpdateLoginPasswordParam AuthUpdateLoginPasswordParam) {
      this.authService.updatePassword(AuthUpdateLoginPasswordParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端手机验证码登录"
   )
   @PostMapping({"/auth/c/doLoginByPhone"})
   public CommonResult<String> doLoginByPhone(@RequestBody @Valid AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam) {
      return CommonResult.data(this.authService.doLoginByPhone(authPhoneValidCodeLoginParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "后台切换会员前台登录"
   )
   @PostMapping({"/auth/switch-login"})
   public CommonResult<String> switchLogin(@RequestBody @Valid AuthSwitchLoginParam param) {
      return CommonResult.data(this.authService.switchLogin(param));
   }

   @Operation(
      summary = "C端退出"
   )
   @GetMapping({"/auth/c/doLogout"})
   public CommonResult<String> doLogout() {
      StpClientUtil.logout();
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端获取用户信息"
   )
   @GetMapping({"/auth/c/getLoginUser"})
   public CommonResult<SaBaseClientLoginUser> getLoginUser() {
      return CommonResult.data(this.authService.getClientLoginUser());
   }

   @Operation(
      summary = "C端生成Telegram绑定链接"
   )
   @PostMapping({"/auth/c/telegram/binding/currentMemberLink"})
   public CommonResult<DevTelegramBindLinkInfo> currentMemberTelegramLink() {
      String userId = String.valueOf(StpClientLoginUserUtil.getClientLoginUser().getId());
      return CommonResult.data(this.devTelegramBindingApi.createBindLink("MEMBER", userId));
   }

   @Operation(
      summary = "C端找回密码查询账号是否存在"
   )
   @GetMapping({"/auth/c/checkAccount"})
   public CommonResult<ClientUser> checkAccount(AuthCheckAccountParam authCheckAccountParam) {
      return this.authService.checkAccount(authCheckAccountParam, SaClientTypeEnum.C.getValue());
   }

   @Operation(
      summary = "C端找回密码"
   )
   @PostMapping({"/auth/c/resetPassword"})
   public CommonResult<String> resetPassword(@RequestBody @Valid AuthResetLoginPasswordParam authResetLoginPasswordParam) {
      this.authService.resetPassword(authResetLoginPasswordParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端邮箱找回密码"
   )
   @PostMapping({"/auth/c/resetPasswordByEmail"})
   public CommonResult<String> resetPasswordByEmail(@RequestBody @Valid AuthResetPasswordByEmailParam authResetPasswordByEmailParam) {
      this.authService.resetPasswordByEmail(authResetPasswordByEmailParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端获取绑定手机验证码"
   )
   @GetMapping({"/auth/c/getBindPhoneValidCode"})
   public CommonResult<String> getBindPhoneValidCode(@Valid AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
      return CommonResult.data(this.authService.getBindPhoneValidCode(authGetPhoneValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端绑定手机号"
   )
   @PostMapping({"/auth/c/bindPhone"})
   public CommonResult<String> bindPhone(@RequestBody @Valid AuthBindPhoneParam authBindPhoneParam) {
      this.authService.bindPhone(authBindPhoneParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端获取绑定邮箱验证码"
   )
   @GetMapping({"/auth/c/getBindEmailValidCode"})
   public CommonResult<String> getBindEmailValidCode(@Valid AuthGetEmailValidCodeParam authGetEmailValidCodeParam) {
      return CommonResult.data(this.authService.getBindEmailValidCode(authGetEmailValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端绑定邮箱"
   )
   @PostMapping({"/auth/c/bindEmail"})
   public CommonResult<String> bindEmail(@RequestBody @Valid AuthBindEmailParam authBindEmailParam) {
      this.authService.bindEmail(authBindEmailParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }
}
