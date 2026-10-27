package com.rxinyun.member.order.modular.dns.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.dns.param.DnsCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.param.DnsRenewCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.param.DnsUpgradeQuotaCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.response.DnsCalculatePriceResponse;
import com.rxinyun.member.order.modular.dns.service.DnsCalculatePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-DNS云解析计价"
)
@RestController
@RequestMapping({"/member/dns"})
@Validated
public class MemberDnsCalculatePriceController {
   @Resource
   private DnsCalculatePriceService dnsCalculatePriceService;

   @Operation(
      summary = "DNS云解析新购计价"
   )
   @PostMapping({"/calculate"})
   public CommonResult<DnsCalculatePriceResponse> calculate(@RequestBody @Validated DnsCalculatePriceParam param) {
      return CommonResult.data(this.dnsCalculatePriceService.calculateNewPurchase(param));
   }

   @Operation(
      summary = "DNS云解析续费计价"
   )
   @PostMapping({"/renew/calculate"})
   public CommonResult<DnsCalculatePriceResponse> calculateRenew(@RequestBody @Validated DnsRenewCalculatePriceParam param) {
      return CommonResult.data(this.dnsCalculatePriceService.calculateRenew(param));
   }

   @Operation(
      summary = "DNS云解析升级域名数量计价"
   )
   @PostMapping({"/upgrade/quota/calculate"})
   public CommonResult<DnsCalculatePriceResponse> calculateUpgradeQuota(@RequestBody @Validated DnsUpgradeQuotaCalculatePriceParam param) {
      return CommonResult.data(this.dnsCalculatePriceService.calculateUpgradeQuota(param));
   }
}
