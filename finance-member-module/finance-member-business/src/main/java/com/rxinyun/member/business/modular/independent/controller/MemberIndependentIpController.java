package com.rxinyun.member.business.modular.independent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentIp;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpIdParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpPageParam;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentIpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器IP管理控制器"
)
@RestController
@RequestMapping({"/member/independent/ip"})
@Validated
public class MemberIndependentIpController {
   @Resource
   private MemberIndependentIpService memberIndependentIpService;

   @Operation(
      summary = "获取独立服务器IP分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberIndependentIp>> page(MemberIndependentIpPageParam pageParam) {
      return CommonResult.data(this.memberIndependentIpService.page(pageParam));
   }

   @Operation(
      summary = "获取独立服务器IP"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberIndependentIp> detail(@Valid MemberIndependentIpIdParam serverIpIdParam) {
      return CommonResult.data(this.memberIndependentIpService.detail(serverIpIdParam));
   }
}
