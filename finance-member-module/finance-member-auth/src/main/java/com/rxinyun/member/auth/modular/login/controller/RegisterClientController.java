package com.rxinyun.member.auth.modular.login.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.login.param.AuthGetEmailValidCodeParam;
import com.rxinyun.member.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.rxinyun.member.auth.modular.login.param.AuthRegisterByEmailParam;
import com.rxinyun.member.auth.modular.login.param.AuthRegisterParam;
import com.rxinyun.member.auth.modular.login.service.MemberAuthService;
import com.rxinyun.member.core.enums.SaClientTypeEnum;
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
public class RegisterClientController {
   @Resource
   private MemberAuthService authService;

   @Operation(
      summary = "C端会员用户注册"
   )
   @PostMapping({"/auth/c/register"})
   public CommonResult<String> register(@RequestBody @Valid AuthRegisterParam authRegisterParam) {
      this.authService.register(authRegisterParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端获取用户注册手机验证码"
   )
   @GetMapping({"/auth/c/getRegisterValidCode"})
   public CommonResult<String> getRegisterValidCode(@Valid AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam) {
      return CommonResult.data(this.authService.getRegisterValidCode(authGetPhoneValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端获取用户注册邮箱验证码"
   )
   @GetMapping({"/auth/c/getRegisterEmailValidCode"})
   public CommonResult<String> getRegisterEmailValidCode(@Valid AuthGetEmailValidCodeParam authGetEmailValidCodeParam) {
      return CommonResult.data(this.authService.getRegisterEmailValidCode(authGetEmailValidCodeParam, SaClientTypeEnum.C.getValue()));
   }

   @Operation(
      summary = "C端会员用户邮箱注册"
   )
   @PostMapping({"/auth/c/registerByEmail"})
   public CommonResult<String> registerByEmail(@RequestBody @Valid AuthRegisterByEmailParam authRegisterByEmailParam) {
      this.authService.registerByEmail(authRegisterByEmailParam, SaClientTypeEnum.C.getValue());
      return CommonResult.ok();
   }
}
