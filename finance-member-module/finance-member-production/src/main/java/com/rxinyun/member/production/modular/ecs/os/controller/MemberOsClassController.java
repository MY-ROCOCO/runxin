package com.rxinyun.member.production.modular.ecs.os.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsClass;
import com.rxinyun.member.production.modular.ecs.os.param.OsClassIdParam;
import com.rxinyun.member.production.modular.ecs.os.param.OsClassPageParam;
import com.rxinyun.member.production.modular.ecs.os.service.MemberOsClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "操作系统类别管理控制器"
)
@RestController
@RequestMapping({"/member/os"})
@Validated
public class MemberOsClassController {
   @Resource
   private MemberOsClassService membersClassService;

   @Operation(
      summary = "获取操作系统类别分页"
   )
   @GetMapping({"class/page"})
   public CommonResult<Page<MemberOsClass>> page(OsClassPageParam pageParam) {
      return CommonResult.data(this.membersClassService.page(pageParam));
   }

   @Operation(
      summary = "获取操作系统类别详情"
   )
   @GetMapping({"/class/detail"})
   public CommonResult<MemberOsClass> detail(@Valid OsClassIdParam osClassIdParam) {
      return CommonResult.data(this.membersClassService.detail(osClassIdParam));
   }
}
