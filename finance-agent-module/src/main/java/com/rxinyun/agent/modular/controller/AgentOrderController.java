package com.rxinyun.agent.modular.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.agent.modular.param.AgentOrderPageParam;
import com.rxinyun.agent.modular.response.AgentOrderPageResponse;
import com.rxinyun.agent.modular.service.AgentPortalService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理直属会员订单控制器"
)
@RestController
@RequestMapping({"/agent/order"})
@SaCheckLogin(
   type = "A"
)
public class AgentOrderController {
   @Resource
   private AgentPortalService agentPortalService;

   @Operation(
      summary = "代理查看直属会员订单分页"
   )
   @GetMapping({"/my/orders"})
   public CommonResult<Page<AgentOrderPageResponse>> myOrders(AgentOrderPageParam pageParam) {
      return CommonResult.data(this.agentPortalService.myOrders(pageParam));
   }
}
