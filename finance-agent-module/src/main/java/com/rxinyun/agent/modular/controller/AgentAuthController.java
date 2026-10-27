package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.rxinyun.agent.core.util.StpAgentUtil;
import com.rxinyun.agent.modular.auth.result.AgentLoginUser;
import com.rxinyun.agent.modular.param.AgentLoginParam;
import com.rxinyun.agent.modular.param.AgentUpdatePasswordParam;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.common.pojo.CommonResult;
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
   name = "代理端登录控制器"
)
@RestController
@RequestMapping({"/agent/auth"})
@Validated
public class AgentAuthController {
   @Resource
   private AgentUserService agentUserService;

   @Operation(
      summary = "代理账号密码登录"
   )
   @PostMapping({"/login"})
   public CommonResult<String> login(@RequestBody @Valid AgentLoginParam loginParam) {
      return CommonResult.data(this.agentUserService.doLogin(loginParam));
   }

   @Operation(
      summary = "代理退出登录"
   )
   @SaCheckLogin(
      type = "A"
   )
   @GetMapping({"/logout"})
   public CommonResult<String> logout() {
      StpAgentUtil.logout();
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取代理登录信息"
   )
   @SaCheckLogin(
      type = "A"
   )
   @GetMapping({"/getLoginUser"})
   public CommonResult<AgentLoginUser> getLoginUser() {
      return CommonResult.data(this.agentUserService.getLoginUser());
   }

   @Operation(
      summary = "代理修改密码"
   )
   @SaCheckLogin(
      type = "A"
   )
   @PostMapping({"/updatePassword"})
   public CommonResult<String> updatePassword(@RequestBody @Valid AgentUpdatePasswordParam updatePasswordParam) {
      this.agentUserService.updatePassword(updatePasswordParam);
      return CommonResult.ok();
   }
}
