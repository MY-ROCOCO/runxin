package com.rxinyun.member.business.modular.esc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerIpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "云服务器IP管理控制器"
)
@RestController
@RequestMapping({"/member/server/ip"})
@Validated
public class MemberServerIpController {
   @Resource
   private MemberServerIpService memberServerIpService;

   @Operation(
      summary = "获取云服务器IP分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberServerIp>> page(MemberServerIpPageParam pageParam) {
      return CommonResult.data(this.memberServerIpService.page(pageParam));
   }

   @Operation(
      summary = "获取云服务器IP"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberServerIp> detail(@Valid MemberServerIpIdParam serverIpIdParam) {
      return CommonResult.data(this.memberServerIpService.detail(serverIpIdParam));
   }
}
