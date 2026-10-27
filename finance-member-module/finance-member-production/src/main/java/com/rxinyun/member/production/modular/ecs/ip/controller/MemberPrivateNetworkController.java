package com.rxinyun.member.production.modular.ecs.ip.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPrivateNetwork;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPrivateNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPrivateNetworkPageParam;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberPrivateNetworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "私网IP管理控制器"
)
@RestController
@RequestMapping({"/member/private/ip"})
@Validated
public class MemberPrivateNetworkController {
   @Resource
   private MemberPrivateNetworkService memberPrivateNetworkService;

   @Operation(
      summary = "获取私网IP分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberPrivateNetwork>> page(MemberPrivateNetworkPageParam pageParam) {
      StpUtil.checkLogin();
      return CommonResult.data(this.memberPrivateNetworkService.page(pageParam));
   }

   @Operation(
      summary = "获取私网IP"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberPrivateNetwork> detail(@Valid MemberPrivateNetworkIdParam privateNetworkIdParam) {
      StpUtil.checkLogin();
      return CommonResult.data(this.memberPrivateNetworkService.detail(privateNetworkIdParam));
   }
}
