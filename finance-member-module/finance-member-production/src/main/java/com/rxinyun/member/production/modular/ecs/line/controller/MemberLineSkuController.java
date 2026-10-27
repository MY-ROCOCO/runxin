package com.rxinyun.member.production.modular.ecs.line.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLineSku;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLineSkuIdParam;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLineSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "服务器线路核销价格管理控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberLineSkuController {
   @Resource
   private MemberLineSkuService memberLineSkuService;

   @Operation(
      summary = "根据核心数（CPU）查询云服务器核心价格"
   )
   @GetMapping({"/line/sku/cpu"})
   public CommonResult<List<MemberLineSku>> queryByCpu(@Valid MemberLineSkuIdParam memberLineSkuIdParam) {
      return CommonResult.data(this.memberLineSkuService.queryByCpu(memberLineSkuIdParam));
   }

   @Operation(
      summary = "根据内存（RAM）查询云服务器核心价格"
   )
   @GetMapping({"/line/sku/ram"})
   public CommonResult<List<MemberLineSku>> queryByRam(@Valid MemberLineSkuIdParam memberLineSkuIdParam) {
      return CommonResult.data(this.memberLineSkuService.queryByRam(memberLineSkuIdParam));
   }
}
