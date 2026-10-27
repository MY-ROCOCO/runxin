package com.rxinyun.member.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServerPort;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerPortPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerPortService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "云服务器端口管理控制器"
)
@RestController
@RequestMapping({"/member/cloud/port"})
@Validated
public class MemberServerPortController {
   @Resource
   private MemberServerPortService memberServerPortService;

   @Operation(
      summary = "获取云服务器端口分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberServerPort>> page(MemberServerPortPageParam pageParam) {
      return CommonResult.data(this.memberServerPortService.page(pageParam));
   }

   @Operation(
      summary = "获取云服务器端口详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberServerPort> detail(@Valid MemberServerPortIdParam memberServerPortIdParam) {
      return CommonResult.data(this.memberServerPortService.detail(memberServerPortIdParam));
   }

   @Operation(
      summary = "新增云服务器端口"
   )
   @PostMapping({"/add"})
   public CommonResult<MemberServerPort> add(@RequestBody MemberServerPortAddParam memberServerPortAddParam) {
      this.memberServerPortService.add(memberServerPortAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "云服务器端口删除"
   )
   @CommonLog("云服务器端口删除")
   @PostMapping({"/delete"})
   public CommonResult delete(@RequestBody List<MemberServerPortIdParam> memberServerPortIdParam) {
      this.memberServerPortService.delete(memberServerPortIdParam);
      return CommonResult.ok();
   }
}
