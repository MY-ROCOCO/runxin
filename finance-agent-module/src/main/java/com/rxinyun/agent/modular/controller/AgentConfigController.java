package com.rxinyun.agent.modular.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.core.pojo.BaseDevConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理配置控制器"
)
@RestController
@RequestMapping({"/agent"})
public class AgentConfigController {
   @Resource
   private DevConfigApi devConfigApi;

   @Operation(
      summary = "获取系统基础配置"
   )
   @GetMapping({"/tools/config/sysBaseList"})
   public CommonResult<List<BaseDevConfig>> sysBaseList() {
      return CommonResult.data(this.devConfigApi.sysBaseList());
   }
}
