package com.rxinyun.member.tools.modular.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.tools.modular.log.entity.MemberLog;
import com.rxinyun.member.tools.modular.log.param.MemberLogIdParam;
import com.rxinyun.member.tools.modular.log.param.MemberLogPageParam;
import com.rxinyun.member.tools.modular.log.service.MemberLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "C端用户日志控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberLogController {
   @Resource
   private MemberLogService memberLogService;

   @Operation(
      summary = "获取当前会员日志分页"
   )
   @GetMapping({"/tools/log/page"})
   public CommonResult<Page<MemberLog>> page(MemberLogPageParam memberLogPageParam) {
      return CommonResult.data(this.memberLogService.currentMemberPage(memberLogPageParam));
   }

   @Operation(
      summary = "获取当前会员登录日志分页"
   )
   @GetMapping({"/tools/log/login/page"})
   public CommonResult<Page<MemberLog>> loginPage(MemberLogPageParam memberLogPageParam) {
      return CommonResult.data(this.memberLogService.currentMemberLoginPage(memberLogPageParam));
   }

   @Operation(
      summary = "获取当前会员日志详情"
   )
   @GetMapping({"/tools/log/detail"})
   public CommonResult<MemberLog> detail(@Valid MemberLogIdParam memberLogIdParam) {
      return CommonResult.data(this.memberLogService.currentMemberDetail(memberLogIdParam));
   }
}
