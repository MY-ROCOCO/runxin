package com.rxinyun.admin.business.modular.independent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.independent.entity.IndependentIp;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpDeleteParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIpQueryParam;
import com.rxinyun.admin.business.modular.independent.service.IndependentIpService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器IP管理控制器"
)
@RestController
@RequestMapping({"/admin/independent/ip"})
@Validated
public class IndependentIpController {
   @Resource
   private IndependentIpService independentIpService;

   @Operation(
      summary = "获取独立服务器IP分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<IndependentIp>> page(IndependentIpPageParam pageParam) {
      return CommonResult.data(this.independentIpService.page(pageParam));
   }

   @Operation(
      summary = "根据独立服务器ID获取IP列表"
   )
   @GetMapping({"/queryByServerId"})
   public CommonResult<List<IndependentIp>> queryByServerId(@Valid IndependentIpQueryParam queryParam) {
      return CommonResult.data(this.independentIpService.queryByServerId(queryParam));
   }

   @Operation(
      summary = "设置主IP"
   )
   @PostMapping({"/setMainIp"})
   public CommonResult setMainIp(@RequestBody IndependentIpIdParam independentIpIdParam) {
      this.independentIpService.setMainIp(independentIpIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取独立服务器IP"
   )
   @GetMapping({"/detail"})
   public CommonResult<IndependentIp> detail(@Valid IndependentIpIdParam serverIpIdParam) {
      return CommonResult.data(this.independentIpService.detail(serverIpIdParam));
   }

   @Operation(
      summary = "新增独立服务器IP"
   )
   @PostMapping({"/add"})
   public CommonResult add(@Valid IndependentIpAddParam serverIpAddParam) {
      this.independentIpService.add(serverIpAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除操作独立服务器IP"
   )
   @CommonLog("删除独立服务器IP")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody List<IndependentIpDeleteParam> independentIpDeleteParamList) {
      this.independentIpService.delete(independentIpDeleteParamList);
      return CommonResult.ok();
   }
}
