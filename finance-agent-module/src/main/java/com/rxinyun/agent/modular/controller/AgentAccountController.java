package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.response.AgentBalanceResponse;
import com.rxinyun.agent.modular.response.AgentCommissionSummaryResponse;
import com.rxinyun.agent.modular.service.AgentCommissionService;
import com.rxinyun.agent.modular.service.AgentInfoService;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理账户控制器"
)
@RestController
@RequestMapping({"/agent/account"})
@SaCheckLogin(
   type = "A"
)
public class AgentAccountController {
   @Resource
   private AgentUserService agentUserService;
   @Resource
   private AgentInfoService agentInfoService;
   @Resource
   private AgentCommissionService agentCommissionService;

   @Operation(
      summary = "代理查看我的余额概览"
   )
   @GetMapping({"/my/info"})
   public CommonResult<AgentBalanceResponse> myInfo() {
      String agentId = this.agentUserService.getLoginUser().getAgentId();
      AgentInfo agentInfo = this.agentInfoService.queryEntity(agentId);
      AgentCommissionSummaryResponse summary = this.agentCommissionService.summaryByAgentId(agentId);
      AgentBalanceResponse response = new AgentBalanceResponse();
      response.setAgentId(agentInfo.getId());
      response.setAgentCode(agentInfo.getAgentCode());
      response.setAgentName(agentInfo.getAgentName());
      response.setBalance(this.agentInfoService.queryBalance(agentId));
      response.setTotalCommission(summary.getTotalCommission());
      response.setTotalRefundDeduct(summary.getTotalRefundDeduct());
      response.setNetCommission(summary.getNetCommission());
      return CommonResult.data(response);
   }
}
