package com.rxinyun.member.production.modular.vhost.region.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.vhost.region.entity.MemberVhostRegion;
import com.rxinyun.member.production.modular.vhost.region.param.MemberVhostRegionPageParam;
import com.rxinyun.member.production.modular.vhost.region.service.MemberVhostRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-虚拟主机地域"
)
@RestController
@RequestMapping({"/member/vhost/region"})
@Validated
public class MemberVhostRegionController {
   @Resource
   private MemberVhostRegionService memberVhostRegionService;

   @Operation(
      summary = "下单查询虚拟主机地域分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberVhostRegion>> page(MemberVhostRegionPageParam pageParam) {
      return CommonResult.data(this.memberVhostRegionService.page(pageParam));
   }
}
