package com.rxinyun.admin.order.modular.vhost.controller;

import com.rxinyun.admin.order.modular.vhost.param.AdminVhostCalculateParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostRenewParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostUpgradeParam;
import com.rxinyun.admin.order.modular.vhost.response.AdminVhostPriceResponse;
import com.rxinyun.admin.order.modular.vhost.service.AdminVhostCalculatePriceService;
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
   name = "后台虚拟主机计价"
)
@RestController
@RequestMapping({"/admin/vhost"})
@Validated
public class AdminVhostCalculatePriceController {
   @Resource
   private AdminVhostCalculatePriceService adminVhostCalculatePriceService;

   @Operation(
      summary = "虚拟主机新购计价"
   )
   @PostMapping({"/calculate"})
   public CommonResult<AdminVhostPriceResponse> calculate(@Valid @RequestBody AdminVhostCalculateParam param) {
      return CommonResult.data(this.adminVhostCalculatePriceService.calculateNewPurchase(param));
   }

   @Operation(
      summary = "虚拟主机续费计价"
   )
   @PostMapping({"/calculate/renew"})
   public CommonResult<AdminVhostPriceResponse> renew(@Valid @RequestBody AdminVhostRenewParam param) {
      return CommonResult.data(this.adminVhostCalculatePriceService.calculateRenew(param));
   }

   @Operation(
      summary = "虚拟主机升级流量计价"
   )
   @PostMapping({"/calculate/upgrade-traffic"})
   public CommonResult<AdminVhostPriceResponse> upgradeTraffic(@Valid @RequestBody AdminVhostUpgradeParam param) {
      return CommonResult.data(this.adminVhostCalculatePriceService.calculateUpgradeTraffic(param));
   }
}
