package com.rxinyun.member.order.modular.independent.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentCalculatePriceParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.member.order.modular.independent.service.IndependentCalculatePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器价格计算控制器"
)
@RestController
@RequestMapping({"/member/independent"})
@Validated
public class IndependentCalculatePriceController {
   @Resource
   private IndependentCalculatePriceService independentCalculatePriceService;

   @Operation(
      summary = "独立服务器订单计价"
   )
   @PostMapping({"/server/calculate"})
   public CommonResult<IndependentCalculatePriceResponse> calculatePrice(@RequestBody @Valid MemberIndependentCalculatePriceParam calculatePriceParam) {
      return this.independentCalculatePriceService.calculatePrice(calculatePriceParam);
   }

   @Operation(
      summary = "独立服务器增加IP计价"
   )
   @PostMapping({"/calculate/upgrade/ip"})
   public CommonResult<IndependentCalculatePriceResponse> upgradeIp(@Valid @RequestBody MemberIndependentIpUpgradeOrderParam upgradeOrderParam) {
      return this.independentCalculatePriceService.upgradeIp(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器带宽升级计价"
   )
   @PostMapping({"/calculate/upgrade/bandwidth"})
   public CommonResult<IndependentCalculatePriceResponse> upgradeBandwidth(@Valid @RequestBody MemberIndependentBandwidthUpgradeOrderParam upgradeOrderParam) {
      return this.independentCalculatePriceService.upgradeBandwidth(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器防御升级"
   )
   @PostMapping({"/calculate/upgrade/defense"})
   public CommonResult<IndependentCalculatePriceResponse> upgradeDefense(@Valid @RequestBody MemberIndependentDefenseUpgradeOrderParam upgradeOrderParam) {
      return this.independentCalculatePriceService.upgradeDefense(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器续费计价"
   )
   @PostMapping({"/calculate/renew"})
   public CommonResult<IndependentCalculatePriceResponse> renew(@Valid @RequestBody MemberIndependentRenewOrderParam renewOrderParam) {
      return this.independentCalculatePriceService.renew(renewOrderParam);
   }
}
