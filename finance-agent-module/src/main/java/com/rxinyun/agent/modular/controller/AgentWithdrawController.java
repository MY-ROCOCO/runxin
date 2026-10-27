package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.agent.modular.auth.result.AgentLoginUser;
import com.rxinyun.agent.modular.param.AgentWithdrawApplyParam;
import com.rxinyun.agent.modular.param.AgentWithdrawPageParam;
import com.rxinyun.agent.modular.response.AgentWithdrawPageResponse;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.agent.modular.service.AgentWithdrawService;
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
   name = "代理提现控制器"
)
@RestController
@RequestMapping({"/agent/withdraw"})
@SaCheckLogin(
   type = "A"
)
public class AgentWithdrawController {
   @Resource
   private AgentUserService agentUserService;
   @Resource
   private AgentWithdrawService agentWithdrawService;

   @Operation(
      summary = "代理申请提现"
   )
   @CommonLog("代理申请提现")
   @PostMapping({"/apply"})
   public CommonResult<String> apply(@RequestBody @Valid AgentWithdrawApplyParam applyParam) {
      AgentLoginUser loginUser = this.agentUserService.getLoginUser();
      this.agentWithdrawService.apply(loginUser.getAgentId(), loginUser.getId(), applyParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "代理查看我的提现记录"
   )
   @GetMapping({"/my/page"})
   public CommonResult<Page<AgentWithdrawPageResponse>> myPage(AgentWithdrawPageParam pageParam) {
      pageParam.setAgentId(this.agentUserService.getLoginUser().getAgentId());
      return CommonResult.data(this.agentWithdrawService.page(pageParam));
   }
}
