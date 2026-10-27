package com.rxinyun.member.tools.modular.config.controller;

import cn.hutool.json.JSONObject;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevSlideshowApi;
import com.rxinyun.member.tools.modular.config.entity.MemberConfig;
import com.rxinyun.member.tools.modular.config.param.MemberConfigIdParam;
import com.rxinyun.member.tools.modular.config.service.MemberConfigService;
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
   name = "配置控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberConfigController {
   @Resource
   private MemberConfigService memberConfigService;
   @Resource
   private DevSlideshowApi devSlideshowApi;

   @Operation(
      summary = "获取系统基础配置"
   )
   @GetMapping({"/tools/config/sysBaseList"})
   public CommonResult<List<MemberConfig>> sysBaseList() {
      return CommonResult.data(this.memberConfigService.sysBaseList());
   }

   @Operation(
      summary = "获取前台轮播图列表"
   )
   @GetMapping({"/tools/config/slideshow/list"})
   public CommonResult<List<JSONObject>> slideshowList() {
      return CommonResult.data(this.devSlideshowApi.getListByPlace(null));
   }

   @Operation(
      summary = "获取配置详情"
   )
   @GetMapping({"/tools/config/detail"})
   public CommonResult<MemberConfig> detail(@Valid MemberConfigIdParam memberConfigIdParam) {
      return CommonResult.data(this.memberConfigService.detail(memberConfigIdParam));
   }
}
