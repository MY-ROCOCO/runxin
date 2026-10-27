package com.rxinyun.member.auth.modular.third.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.third.entity.AuthThirdUser;
import com.rxinyun.member.auth.modular.third.param.AuthThirdCallbackParam;
import com.rxinyun.member.auth.modular.third.param.AuthThirdRenderParam;
import com.rxinyun.member.auth.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.member.auth.modular.third.result.AuthThirdRenderResult;
import com.rxinyun.member.auth.modular.third.service.MemberAuthThirdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "三方登录控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberAuthThirdController {
   @Resource
   private MemberAuthThirdService authThirdService;

   @Operation(
      summary = "第三方登录页面渲染"
   )
   @GetMapping({"/auth/third/render"})
   public CommonResult<AuthThirdRenderResult> render(@Valid AuthThirdRenderParam authThirdRenderParam) {
      return CommonResult.data(this.authThirdService.render(authThirdRenderParam));
   }

   @Operation(
      summary = "第三方登录授权回调"
   )
   @GetMapping({"/auth/third/callback"})
   public CommonResult callback(@Valid AuthThirdCallbackParam authThirdCallbackParam, AuthCallback authCallback) {
      return this.authThirdService.callback(authThirdCallbackParam, authCallback);
   }

   @Operation(
      summary = "获取三方用户分页"
   )
   @GetMapping({"/auth/third/page"})
   public CommonResult<Page<AuthThirdUser>> page(AuthThirdUserPageParam authThirdUserPageParam) {
      return CommonResult.data(this.authThirdService.page(authThirdUserPageParam));
   }

   @Operation(
      summary = "绑定三方用户"
   )
   @PostMapping({"/auth/third/bind"})
   public CommonResult bind(@RequestBody AuthUser authUser) {
      return CommonResult.data(this.authThirdService.bind(authUser));
   }
}
