package com.rxinyun.member.order.modular.vhost.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.vhost.param.VhostCalculatePriceParam;
import com.rxinyun.member.order.modular.vhost.param.VhostRenewParam;
import com.rxinyun.member.order.modular.vhost.param.VhostUpgradeTrafficParam;
import com.rxinyun.member.order.modular.vhost.response.VhostCalculatePriceResponse;
import com.rxinyun.member.order.modular.vhost.service.VhostCalculatePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-虚拟主机计价"
)
@RestController
@RequestMapping({"/member/vhost"})
@Validated
public class MemberVhostCalculatePriceController {
   @Resource
   private VhostCalculatePriceService vhostCalculatePriceService;

   @Operation(
      summary = "虚拟主机新购计价"
   )
   @PostMapping({"/calculate"})
   public CommonResult<VhostCalculatePriceResponse> calculate(@RequestBody @Validated VhostCalculatePriceParam param) {
      return CommonResult.data(this.vhostCalculatePriceService.calculateNewPurchase(param));
   }

   @Operation(
      summary = "虚拟主机续费计价"
   )
   @PostMapping({"/calculate/renew"})
   public CommonResult<VhostCalculatePriceResponse> renew(@RequestBody @Validated VhostRenewParam param) {
      return CommonResult.data(this.vhostCalculatePriceService.calculateRenew(param));
   }

   @Operation(
      summary = "虚拟主机升级流量计价"
   )
   @PostMapping({"/calculate/upgrade-traffic"})
   public CommonResult<VhostCalculatePriceResponse> upgradeTraffic(@RequestBody @Validated VhostUpgradeTrafficParam param) {
      return CommonResult.data(this.vhostCalculatePriceService.calculateUpgradeTraffic(param));
   }
}
