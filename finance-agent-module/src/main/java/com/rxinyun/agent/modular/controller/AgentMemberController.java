package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.agent.modular.auth.result.AgentLoginUser;
import com.rxinyun.agent.modular.param.AgentMemberBalanceAddParam;
import com.rxinyun.agent.modular.param.AgentMemberPageParam;
import com.rxinyun.agent.modular.response.AgentMemberPageResponse;
import com.rxinyun.agent.modular.service.AgentPortalService;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理直属会员控制器"
)
@RestController
@RequestMapping({"/agent/member"})
@SaCheckLogin(
   type = "A"
)
public class AgentMemberController {
   @Resource
   private AgentPortalService agentPortalService;
   @Resource
   private AgentUserService agentUserService;

   @Operation(
      summary = "代理查看直属会员分页"
   )
   @GetMapping({"/my/members"})
   public CommonResult<Page<AgentMemberPageResponse>> myMembers(AgentMemberPageParam pageParam) {
      return CommonResult.data(this.agentPortalService.myMembers(pageParam));
   }

   @Operation(
      summary = "代理给直属会员增加余额"
   )
   @CommonLog("代理给直属会员增加余额")
   @PostMapping({"/balance/add"})
   public CommonResult<String> addBalance(@RequestBody @Valid AgentMemberBalanceAddParam addParam) {
      AgentLoginUser loginUser = this.agentUserService.getLoginUser();
      this.agentPortalService.addMemberBalance(loginUser.getAgentId(), addParam);
      return CommonResult.ok();
   }
}
