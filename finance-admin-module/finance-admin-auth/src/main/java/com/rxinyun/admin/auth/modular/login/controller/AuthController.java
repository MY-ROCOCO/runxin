package com.rxinyun.admin.auth.modular.login.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.rxinyun.admin.auth.modular.login.param.AuthAccountPasswordLoginParam;
import com.rxinyun.admin.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.rxinyun.admin.auth.modular.login.param.AuthPhoneValidCodeLoginParam;
import com.rxinyun.admin.auth.modular.login.result.AuthPicValidCodeResult;
import com.rxinyun.admin.auth.modular.login.service.AuthService;
import com.rxinyun.auth.core.enums.SaClientTypeEnum;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "B端登录控制器"
)
@RestController
@Validated
public class AuthController {
   @Resource
   private AuthService authService;

   @Operation(
      summary = "B端获取图片验证码"
   )
   @GetMapping({"/admin/auth/b/getPicCaptcha"})
   public CommonResult<AuthPicValidCodeResult> getPicCaptcha() {
      return CommonResult.data(this.authService.getPicCaptcha(SaClientTypeEnum.B.getValue()));
   }

   @Operation(
      summary = "B端获取手机验证码"
   )
   @GetMapping({"/admin/auth/b/getPhoneValidCode"})
   public CommonResult<String> getPhoneValidCode(@Valid AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
      return CommonResult.data(this.authService.getPhoneValidCode(authGetPhoneValidCodeParam, SaClientTypeEnum.B.getValue()));
   }

   @Operation(
      summary = "B端账号密码登录"
   )
   @PostMapping({"/admin/auth/b/doLogin"})
   public CommonResult<String> doLogin(@RequestBody @Valid AuthAccountPasswordLoginParam authAccountPasswordLoginParam) {
      return CommonResult.data(this.authService.doLogin(authAccountPasswordLoginParam, SaClientTypeEnum.B.getValue()));
   }

   @Operation(
      summary = "B端手机验证码登录"
   )
   @PostMapping({"/admin/auth/b/doLoginByPhone"})
   public CommonResult<String> doLoginByPhone(@RequestBody @Valid AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam) {
      return CommonResult.data(this.authService.doLoginByPhone(authPhoneValidCodeLoginParam, SaClientTypeEnum.B.getValue()));
   }

   @Operation(
      summary = "B端退出"
   )
   @SaCheckLogin
   @GetMapping({"/admin/auth/b/doLogout"})
   public CommonResult<String> doLogout() {
      StpUtil.logout();
      return CommonResult.ok();
   }

   @Operation(
      summary = "B端获取用户信息"
   )
   @SaCheckLogin
   @GetMapping({"/admin/auth/b/getLoginUser"})
   public CommonResult<SaBaseLoginUser> getLoginUser() {
      return CommonResult.data(this.authService.getLoginUser());
   }
}
