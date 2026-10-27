package com.rxinyun.member.production.modular.ecs.region.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.region.entity.MemberRegion;
import com.rxinyun.member.production.modular.ecs.region.param.MemberRegionIdParam;
import com.rxinyun.member.production.modular.ecs.region.param.MemberRegionPageParam;
import com.rxinyun.member.production.modular.ecs.region.service.MemberRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "服务器地域管理控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberRegionController {
   @Resource
   private MemberRegionService serverRegionService;

   @Operation(
      summary = "获取地域分页"
   )
   @GetMapping({"/region/page"})
   public CommonResult<Page<MemberRegion>> page(MemberRegionPageParam pageParam) {
      return CommonResult.data(this.serverRegionService.page(pageParam));
   }

   @Operation(
      summary = "获取地域"
   )
   @GetMapping({"/region/detail"})
   public CommonResult<MemberRegion> detail(@Valid MemberRegionIdParam serverRegionIdParam) {
      return CommonResult.data(this.serverRegionService.detail(serverRegionIdParam));
   }
}
