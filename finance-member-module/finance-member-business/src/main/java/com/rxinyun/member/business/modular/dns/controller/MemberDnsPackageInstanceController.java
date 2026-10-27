package com.rxinyun.member.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.dns.param.MemberDnsPackageInstanceParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsPackageInstanceService;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-DNS套餐实例"
)
@RestController
@RequestMapping({"/member/dns/package-instance"})
@Validated
public class MemberDnsPackageInstanceController {
   @Resource
   private MemberDnsPackageInstanceService memberDnsPackageInstanceService;

   @Operation(
      summary = "DNS套餐实例分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DnsPackageInstanceVO>> page(MemberDnsPackageInstanceParam.PageParam pageParam) {
      return CommonResult.data(this.memberDnsPackageInstanceService.page(pageParam));
   }
}
