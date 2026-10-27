package com.rxinyun.admin.order.modular.ecs.controller;

import com.rxinyun.admin.order.modular.ecs.param.BandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.admin.order.modular.ecs.param.CoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.admin.order.modular.ecs.service.CalculatePriceService;
import com.rxinyun.common.pojo.CommonResult;
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
   name = "云服务器价格计算控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class CalculatePriceController {
   @Resource
   private CalculatePriceService calculatePriceService;

   @Operation(
      summary = "云服务器订单计价"
   )
   @PostMapping({"/server/calculate"})
   public CommonResult<CalculatePriceResponse> calculatePrice(@Valid @RequestBody CalculatePriceParam calculatePriceParam) {
      return this.calculatePriceService.calculatePrice(calculatePriceParam);
   }

   @Operation(
      summary = "云服务器核心计价"
   )
   @PostMapping({"/calculate/upgrade/core"})
   public CommonResult<CalculatePriceResponse> upgradeCore(@Valid @RequestBody CoreUpgradeOrderParam upgradeOrderParam) {
      return this.calculatePriceService.upgradeCore(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器系统硬盘和硬盘升级计价"
   )
   @PostMapping({"/calculate/upgrade/disk"})
   public CommonResult<CalculatePriceResponse> upgradeDataDisk(@Valid @RequestBody DiskUpgradeOrderParam upgradeOrderParam) {
      return this.calculatePriceService.upgradeDisk(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器带宽升级计价"
   )
   @PostMapping({"/calculate/upgrade/bandwidth"})
   public CommonResult<CalculatePriceResponse> upgradeBandwidth(@Valid @RequestBody BandwidthUpgradeOrderParam upgradeOrderParam) {
      return this.calculatePriceService.upgradeBandwidth(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器防御升级"
   )
   @PostMapping({"/calculate/upgrade/defense"})
   public CommonResult<CalculatePriceResponse> upgradeDefense(@Valid @RequestBody DefenseUpgradeOrderParam upgradeOrderParam) {
      return this.calculatePriceService.upgradeDefense(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器续费计价"
   )
   @PostMapping({"/calculate/renew"})
   public CommonResult<CalculatePriceResponse> renew(@Valid @RequestBody RenewOrderParam renewOrderParam) {
      return this.calculatePriceService.renew(renewOrderParam);
   }
}
