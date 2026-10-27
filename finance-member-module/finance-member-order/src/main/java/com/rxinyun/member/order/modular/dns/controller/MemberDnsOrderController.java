package com.rxinyun.member.order.modular.dns.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.dns.param.DnsOrderCreateParam;
import com.rxinyun.member.order.modular.dns.param.DnsRenewOrderParam;
import com.rxinyun.member.order.modular.dns.param.DnsUpgradeQuotaOrderParam;
import com.rxinyun.member.order.modular.dns.service.DnsOrderService;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-DNS云解析订单"
)
@RestController
@RequestMapping({"/member/dns/order"})
@Validated
public class MemberDnsOrderController {
   @Resource
   private DnsOrderService dnsOrderService;

   @Operation(
      summary = "创建DNS云解析新购订单"
   )
   @PostMapping({"/create"})
   public CommonResult<MemberOrder> create(@RequestBody @Validated DnsOrderCreateParam param) {
      return this.dnsOrderService.create(param);
   }

   @Operation(
      summary = "创建DNS云解析续费订单"
   )
   @PostMapping({"/renew"})
   public CommonResult<MemberOrder> renew(@RequestBody @Validated DnsRenewOrderParam param) {
      return this.dnsOrderService.renew(param);
   }

   @Operation(
      summary = "创建DNS云解析升级域名数量订单"
   )
   @PostMapping({"/upgrade/quota"})
   public CommonResult<MemberOrder> upgradeQuota(@RequestBody @Validated DnsUpgradeQuotaOrderParam param) {
      return this.dnsOrderService.upgradeQuota(param);
   }
}
