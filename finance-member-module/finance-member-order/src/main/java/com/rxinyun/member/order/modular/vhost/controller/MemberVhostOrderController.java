package com.rxinyun.member.order.modular.vhost.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.vhost.param.VhostOrderCreateParam;
import com.rxinyun.member.order.modular.vhost.param.VhostRenewOrderParam;
import com.rxinyun.member.order.modular.vhost.param.VhostUpgradeTrafficOrderParam;
import com.rxinyun.member.order.modular.vhost.service.VhostOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-虚拟主机订单"
)
@RestController
@RequestMapping({"/member/vhost/order"})
@Validated
public class MemberVhostOrderController {
   @Resource
   private VhostOrderService vhostOrderService;

   @Operation(
      summary = "创建虚拟主机新购订单"
   )
   @PostMapping({"/create"})
   public CommonResult<MemberOrder> create(@RequestBody @Validated VhostOrderCreateParam param) {
      return this.vhostOrderService.create(param);
   }

   @Operation(
      summary = "创建虚拟主机续费订单"
   )
   @PostMapping({"/renew"})
   public CommonResult<MemberOrder> renew(@RequestBody @Validated VhostRenewOrderParam param) {
      return this.vhostOrderService.renew(param);
   }

   @Operation(
      summary = "创建虚拟主机升级流量订单"
   )
   @PostMapping({"/upgrade-traffic"})
   public CommonResult<MemberOrder> upgradeTraffic(@RequestBody @Validated VhostUpgradeTrafficOrderParam param) {
      return this.vhostOrderService.upgradeTraffic(param);
   }
}
