package com.rxinyun.member.production.modular.vhost.version.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.vhost.version.entity.MemberVhostVersion;
import com.rxinyun.member.production.modular.vhost.version.param.MemberVhostVersionPageParam;
import com.rxinyun.member.production.modular.vhost.version.service.MemberVhostVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-虚拟主机版本"
)
@RestController
@RequestMapping({"/member/vhost/version"})
@Validated
public class MemberVhostVersionController {
   @Resource
   private MemberVhostVersionService memberVhostVersionService;

   @Operation(
      summary = "下单按线路查询虚拟主机版本分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberVhostVersion>> page(@Valid MemberVhostVersionPageParam pageParam) {
      return CommonResult.data(this.memberVhostVersionService.page(pageParam));
   }
}
