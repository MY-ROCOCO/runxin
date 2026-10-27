package com.rxinyun.member.production.modular.ecs.os.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsVersion;
import com.rxinyun.member.production.modular.ecs.os.param.OsVersionIdParam;
import com.rxinyun.member.production.modular.ecs.os.param.OsVersionPageParam;
import com.rxinyun.member.production.modular.ecs.os.service.MemberOsVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "操作系统版本管理控制器"
)
@RestController
@RequestMapping({"/member/os"})
@Validated
public class MemberOsVersionController {
   @Resource
   private MemberOsVersionService memberOsVersionService;

   @Operation(
      summary = "获取操作系统版本分页"
   )
   @GetMapping({"version/page"})
   public CommonResult<Page<MemberOsVersion>> page(OsVersionPageParam pageParam) {
      return CommonResult.data(this.memberOsVersionService.page(pageParam));
   }

   @Operation(
      summary = "获取操作系统版本"
   )
   @GetMapping({"/version/detail"})
   public CommonResult<MemberOsVersion> detail(@Valid OsVersionIdParam osVersionIdParam) {
      return CommonResult.data(this.memberOsVersionService.detail(osVersionIdParam));
   }
}
