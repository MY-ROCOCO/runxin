package com.rxinyun.member.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServerStrategy;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "云服务器策略管理控制器"
)
@RestController
@RequestMapping({"/member/server/strategy"})
@Validated
public class MemberServerStrategyController {
   @Resource
   private MemberServerStrategyService memberServerStrategyService;

   @Operation(
      summary = "获取云服务器策略分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberServerStrategy>> page(MemberServerStrategyPageParam pageParam) {
      return CommonResult.data(this.memberServerStrategyService.page(pageParam));
   }

   @Operation(
      summary = "新增云服务器策略"
   )
   @PostMapping({"/add"})
   public CommonResult<MemberServerStrategy> add(@RequestBody MemberServerStrategyAddParam memberServerStrategyAddParam) {
      return this.memberServerStrategyService.add(memberServerStrategyAddParam);
   }

   @Operation(
      summary = "获取云服务器策略"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberServerStrategy> detail(@Valid MemberServerStrategyIdParam memberServerStrategyIdParam) {
      return CommonResult.data(this.memberServerStrategyService.detail(memberServerStrategyIdParam));
   }

   @Operation(
      summary = "编辑云服务器策略"
   )
   @CommonLog("编辑云服务器策略")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody MemberServerStrategyEditParam memberServerStrategyEditParam) {
      this.memberServerStrategyService.edit(memberServerStrategyEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除操作云服务器策略"
   )
   @CommonLog("删除云服务器策略")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody MemberServerStrategyIdParam memberServerStrategyIdParam) {
      this.memberServerStrategyService.delete(memberServerStrategyIdParam);
      return CommonResult.ok();
   }
}
