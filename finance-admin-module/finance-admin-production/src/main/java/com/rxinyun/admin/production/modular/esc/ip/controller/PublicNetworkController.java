package com.rxinyun.admin.production.modular.esc.ip.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.ip.entity.PublicNetwork;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkAddParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkEditParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkIdParam;
import com.rxinyun.admin.production.modular.esc.ip.param.PublicNetworkPageParam;
import com.rxinyun.admin.production.modular.esc.ip.service.PublicNetworkService;
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
   name = "公网IP管理控制器"
)
@RestController
@RequestMapping({"/admin/public"})
@Validated
public class PublicNetworkController {
   @Resource
   private PublicNetworkService publicNetworkService;

   @Operation(
      summary = "获取公网IP分页"
   )
   @GetMapping({"/ip/page"})
   public CommonResult<Page<PublicNetwork>> page(PublicNetworkPageParam pageParam) {
      return CommonResult.data(this.publicNetworkService.page(pageParam));
   }

   @Operation(
      summary = "新增公网IP"
   )
   @PostMapping({"/ip/add"})
   public CommonResult<String> add(@RequestBody PublicNetworkAddParam publicNetworkAddParam) {
      return this.publicNetworkService.add(publicNetworkAddParam);
   }

   @Operation(
      summary = "获取公网IP"
   )
   @GetMapping({"/ip/detail"})
   public CommonResult<PublicNetwork> detail(@Valid PublicNetworkIdParam publicNetworkIdParam) {
      return CommonResult.data(this.publicNetworkService.detail(publicNetworkIdParam));
   }

   @Operation(
      summary = "编辑公网IP"
   )
   @CommonLog("编辑公网IP")
   @PostMapping({"/ip/edit"})
   public CommonResult<String> edit(@RequestBody PublicNetworkEditParam publicNetworkEditParam) {
      this.publicNetworkService.edit(publicNetworkEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除公网IP"
   )
   @CommonLog("删除公网IP")
   @PostMapping({"/ip/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<PublicNetworkIdParam> publicNetworkIdParamList) {
      this.publicNetworkService.delete(publicNetworkIdParamList);
      return CommonResult.ok();
   }
}
