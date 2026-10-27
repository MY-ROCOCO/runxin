package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.rxinyun.agent.modular.entity.AgentProductRatio;
import com.rxinyun.agent.modular.service.AgentProductRatioService;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理分成比例控制器"
)
@RestController
@RequestMapping({"/agent/ratio"})
@SaCheckLogin(
   type = "A"
)
public class AgentRatioController {
   @Resource
   private AgentUserService agentUserService;
   @Resource
   private AgentProductRatioService agentProductRatioService;

   @Operation(
      summary = "代理查看我的产品分成比例"
   )
   @GetMapping({"/my/list"})
   public CommonResult<List<AgentProductRatio>> myList() {
      return CommonResult.data(this.agentProductRatioService.listByAgentId(this.agentUserService.getLoginUser().getAgentId()));
   }
}
