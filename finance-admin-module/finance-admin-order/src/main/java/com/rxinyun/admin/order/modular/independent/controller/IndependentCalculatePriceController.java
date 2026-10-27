package com.rxinyun.admin.order.modular.independent.controller;

import com.rxinyun.admin.order.modular.independent.param.IndependentBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentCalculatePriceParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentIpUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.admin.order.modular.independent.service.IndependentCalculatePriceService;
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
   name = "后台独立服务器价格计算控制器"
)
@RestController("adminIndependentCalculatePriceController")
@RequestMapping({"/admin/independent"})
@Validated
public class IndependentCalculatePriceController {
   @Resource
   private IndependentCalculatePriceService independentCalculatePriceService;

   @Operation(
      summary = "独立服务器订单计价"
   )
   @PostMapping({"/server/calculate"})
   public CommonResult<IndependentCalculatePriceResponse> calculatePrice(@Valid @RequestBody IndependentCalculatePriceParam calculatePriceParam) {
      return this.independentCalculatePriceService.calculatePrice(calculatePriceParam);
   }

   @Operation(
      summary = "独立服务器增加IP计价"
   )
   @PostMapping({"/calculate/upgrade/ip"})
   public CommonResult<IndependentCalculatePriceResponse> upgradeIp(@Valid @RequestBody IndependentIpUpgradeOrderParam upgradeOrderParam) {
      return this.independentCalculatePriceService.upgradeIp(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器带宽升级计价"
   )
   @PostMapping({"/calculate/upgrade/bandwidth"})
   public CommonResult<IndependentCalculatePriceResponse> upgradeBandwidth(@Valid @RequestBody IndependentBandwidthUpgradeOrderParam upgradeOrderParam) {
      return this.independentCalculatePriceService.upgradeBandwidth(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器防御升级计价"
   )
   @PostMapping({"/calculate/upgrade/defense"})
   public CommonResult<IndependentCalculatePriceResponse> upgradeDefense(@Valid @RequestBody IndependentDefenseUpgradeOrderParam upgradeOrderParam) {
      return this.independentCalculatePriceService.upgradeDefense(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器续费计价"
   )
   @PostMapping({"/calculate/renew"})
   public CommonResult<IndependentCalculatePriceResponse> renew(@Valid @RequestBody IndependentRenewOrderParam renewOrderParam) {
      return this.independentCalculatePriceService.renew(renewOrderParam);
   }
}
