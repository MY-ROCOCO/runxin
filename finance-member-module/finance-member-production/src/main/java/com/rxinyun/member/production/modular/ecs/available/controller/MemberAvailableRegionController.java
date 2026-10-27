package com.rxinyun.member.production.modular.ecs.available.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.available.entity.MemberAvailableRegion;
import com.rxinyun.member.production.modular.ecs.available.param.MemberAvailableRegionIdParam;
import com.rxinyun.member.production.modular.ecs.available.param.MemberAvailableRegionPageParam;
import com.rxinyun.member.production.modular.ecs.available.response.MemberAvailableRegionResponse;
import com.rxinyun.member.production.modular.ecs.available.service.MemberAvailableRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "服务器可用区域管理控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberAvailableRegionController {
   @Resource
   private MemberAvailableRegionService serverAvailableRegionService;

   @Operation(
      summary = "获取可用区域分页"
   )
   @GetMapping({"/available/page"})
   public CommonResult<Page<MemberAvailableRegion>> page(MemberAvailableRegionPageParam pageParam) {
      return CommonResult.data(this.serverAvailableRegionService.page(pageParam));
   }

   @Operation(
      summary = "获取可用区域详情"
   )
   @GetMapping({"/available/detail"})
   public CommonResult<MemberAvailableRegion> detail(@Valid MemberAvailableRegionIdParam serverAvailableRegionIdParam) {
      return CommonResult.data(this.serverAvailableRegionService.detail(serverAvailableRegionIdParam));
   }

   @Operation(
      summary = "根据地域ID查询可用区域"
   )
   @GetMapping({"/available/region"})
   public CommonResult<List<MemberAvailableRegionResponse>> queryByRegionId(@RequestParam("regionId") String regionId) {
      return CommonResult.data(this.serverAvailableRegionService.queryByRegionId(regionId));
   }
}
