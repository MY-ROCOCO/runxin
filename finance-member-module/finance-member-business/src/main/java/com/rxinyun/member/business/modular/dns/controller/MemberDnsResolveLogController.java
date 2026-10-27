package com.rxinyun.member.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsResolveLog;
import com.rxinyun.member.business.modular.dns.param.MemberDnsResolveLogParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsResolveLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-DNS托管域名解析日志"
)
@RestController
@RequestMapping({"/member/dns/resolve-log"})
@Validated
public class MemberDnsResolveLogController {
   @Resource
   private MemberDnsResolveLogService memberDnsResolveLogService;

   @Operation(
      summary = "DNS托管域名解析日志分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberDnsResolveLog>> page(MemberDnsResolveLogParam.PageParam pageParam) {
      return CommonResult.data(this.memberDnsResolveLogService.page(pageParam));
   }
}
