package com.rxinyun.member.order.modular.ecs.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberRenewOrderParam;
import com.rxinyun.member.order.modular.ecs.response.MemberCalculatePriceResponse;
import com.rxinyun.member.order.modular.ecs.service.MemberCalculatePriceService;
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
   name = "价格计算控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberCalculatePriceController {
   @Resource
   private MemberCalculatePriceService memberCalculatePriceService;

   @Operation(
      summary = "云服务器订单计价"
   )
   @PostMapping({"/server/calculate"})
   public CommonResult<MemberCalculatePriceResponse> calculatePrice(@Valid @RequestBody CalculatePriceParam calculatePriceParam) {
      return this.memberCalculatePriceService.calculatePrice(calculatePriceParam);
   }

   @Operation(
      summary = "云服务器核心计价"
   )
   @PostMapping({"/calculate/upgrade/core"})
   public CommonResult<MemberCalculatePriceResponse> upgradeCore(@Valid @RequestBody MemberCoreUpgradeOrderParam upgradeOrderParam) {
      return this.memberCalculatePriceService.upgradeCore(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器系统硬盘和硬盘升级计价"
   )
   @PostMapping({"/calculate/upgrade/disk"})
   public CommonResult<MemberCalculatePriceResponse> upgradeDataDisk(@Valid @RequestBody MemberDiskUpgradeOrderParam upgradeOrderParam) {
      return this.memberCalculatePriceService.upgradeDisk(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器带宽升级计价"
   )
   @PostMapping({"/calculate/upgrade/bandwidth"})
   public CommonResult<MemberCalculatePriceResponse> upgradeBandwidth(@Valid @RequestBody MemberBandwidthUpgradeOrderParam upgradeOrderParam) {
      return this.memberCalculatePriceService.upgradeBandwidth(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器防御升级"
   )
   @PostMapping({"/calculate/upgrade/defense"})
   public CommonResult<MemberCalculatePriceResponse> upgradeDefense(@Valid @RequestBody MemberDefenseUpgradeOrderParam upgradeOrderParam) {
      return this.memberCalculatePriceService.upgradeDefense(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器续费计价"
   )
   @PostMapping({"/calculate/renew"})
   public CommonResult<MemberCalculatePriceResponse> renew(@Valid @RequestBody MemberRenewOrderParam renewOrderParam) {
      return this.memberCalculatePriceService.renew(renewOrderParam);
   }
}
