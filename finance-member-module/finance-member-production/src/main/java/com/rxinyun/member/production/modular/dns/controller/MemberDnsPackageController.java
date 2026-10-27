package com.rxinyun.member.production.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.dns.param.MemberDnsPackagePageParam;
import com.rxinyun.member.production.modular.dns.service.MemberDnsPackageService;
import com.rxinyun.production.api.dto.DnsPackageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-云解析DNS套餐"
)
@RestController
@RequestMapping({"/member/dns/package"})
@Validated
public class MemberDnsPackageController {
   @Resource
   private MemberDnsPackageService memberDnsPackageService;

   @Operation(
      summary = "云解析DNS套餐分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DnsPackageVO>> page(MemberDnsPackagePageParam pageParam) {
      return CommonResult.data(this.memberDnsPackageService.page(pageParam));
   }
}
