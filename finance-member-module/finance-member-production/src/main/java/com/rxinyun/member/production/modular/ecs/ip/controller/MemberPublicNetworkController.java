package com.rxinyun.member.production.modular.ecs.ip.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPublicNetwork;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPublicNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPublicNetworkPageParam;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberPublicNetworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "公网IP管理控制器"
)
@RestController
@RequestMapping({"/member/public"})
@Validated
public class MemberPublicNetworkController {
   @Resource
   private MemberPublicNetworkService memberPublicNetworkService;

   @Operation(
      summary = "获取公网IP分页"
   )
   @GetMapping({"/ip/page"})
   public CommonResult<Page<MemberPublicNetwork>> page(MemberPublicNetworkPageParam pageParam) {
      StpUtil.checkLogin();
      return CommonResult.data(this.memberPublicNetworkService.page(pageParam));
   }

   @Operation(
      summary = "获取公网IP"
   )
   @GetMapping({"/ip/detail"})
   public CommonResult<MemberPublicNetwork> detail(@Valid MemberPublicNetworkIdParam publicNetworkIdParam) {
      StpUtil.checkLogin();
      return CommonResult.data(this.memberPublicNetworkService.detail(publicNetworkIdParam));
   }
}
