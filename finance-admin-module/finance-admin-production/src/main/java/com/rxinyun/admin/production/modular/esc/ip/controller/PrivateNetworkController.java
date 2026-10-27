package com.rxinyun.admin.production.modular.esc.ip.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.PrivateNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PrivateNetworkPageParam;
import com.rxinyun.admin.production.modular.esc.ip.service.PrivateNetworkService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "私网IP管理控制器"
)
@RestController
@RequestMapping({"/admin/private/ip"})
@Validated
public class PrivateNetworkController {
   @Resource
   private PrivateNetworkService privateNetworkService;

   @Operation(
      summary = "获取私网IP分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<PrivateNetwork>> page(PrivateNetworkPageParam pageParam) {
      return CommonResult.data(this.privateNetworkService.page(pageParam));
   }

   @Operation(
      summary = "新增私网IP"
   )
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody PrivateNetworkAddParam privateNetworkAddParam) {
      return this.privateNetworkService.add(privateNetworkAddParam);
   }

   @Operation(
      summary = "获取私网IP"
   )
   @GetMapping({"/detail"})
   public CommonResult<PrivateNetwork> detail(@Valid PrivateNetworkIdParam privateNetworkIdParam) {
      return CommonResult.data(this.privateNetworkService.detail(privateNetworkIdParam));
   }

   @Operation(
      summary = "编辑私网IP"
   )
   @CommonLog("编辑私网IP")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody PrivateNetworkEditParam privateNetworkEditParam) {
      this.privateNetworkService.edit(privateNetworkEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除私网IP"
   )
   @CommonLog("删除私网IP")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<PrivateNetworkIdParam> privateNetworkIdParamList) {
      this.privateNetworkService.delete(privateNetworkIdParamList);
      return CommonResult.ok();
   }
}
