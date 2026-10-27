package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.agent.modular.param.AgentCommissionPageParam;
import com.rxinyun.agent.modular.response.AgentCommissionPageResponse;
import com.rxinyun.agent.modular.response.AgentCommissionSummaryResponse;
import com.rxinyun.agent.modular.service.AgentCommissionService;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理分成控制器"
)
@RestController
@RequestMapping({"/agent/commission"})
@SaCheckLogin(
   type = "A"
)
public class AgentCommissionController {
   @Resource
   private AgentCommissionService agentCommissionService;
   @Resource
   private AgentUserService agentUserService;

   @Operation(
      summary = "代理查看我的分成流水"
   )
   @GetMapping({"/my/page"})
   public CommonResult<Page<AgentCommissionPageResponse>> myPage(AgentCommissionPageParam pageParam) {
      pageParam.setAgentId(this.agentUserService.getLoginUser().getAgentId());
      return CommonResult.data(this.agentCommissionService.page(pageParam));
   }

   @Operation(
      summary = "代理查看我的分成汇总"
   )
   @GetMapping({"/my/summary"})
   public CommonResult<AgentCommissionSummaryResponse> mySummary() {
      return CommonResult.data(this.agentCommissionService.summaryByAgentId(this.agentUserService.getLoginUser().getAgentId()));
   }
}
