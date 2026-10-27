package com.rxinyun.admin.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.dns.entity.DnsResolveLog;
import com.rxinyun.admin.business.modular.dns.param.DnsResolveLogParam;
import com.rxinyun.admin.business.modular.dns.service.DnsResolveLogService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "DNS托管域名解析日志管理"
)
@RestController
@RequestMapping({"/admin/dns/resolve-log"})
@Validated
public class DnsResolveLogController {
   @Resource
   private DnsResolveLogService dnsResolveLogService;

   @Operation(
      summary = "DNS托管域名解析日志分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DnsResolveLog>> page(DnsResolveLogParam.PageParam pageParam) {
      return CommonResult.data(this.dnsResolveLogService.page(pageParam));
   }
}
