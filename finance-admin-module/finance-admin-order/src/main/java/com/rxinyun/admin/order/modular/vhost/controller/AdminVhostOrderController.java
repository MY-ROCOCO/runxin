package com.rxinyun.admin.order.modular.vhost.controller;

import com.rxinyun.admin.order.modular.manual.response.ManualOrderResult;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostCreateOrderParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostRenewOrderParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostUpgradeOrderParam;
import com.rxinyun.admin.order.modular.vhost.service.AdminVhostOrderService;
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
   name = "后台虚拟主机订单"
)
@RestController
@RequestMapping({"/admin/vhost/order"})
@Validated
public class AdminVhostOrderController {
   @Resource
   private AdminVhostOrderService adminVhostOrderService;

   @Operation(
      summary = "后台创建虚拟主机新购订单"
   )
   @PostMapping({"/create"})
   public CommonResult<ManualOrderResult> create(@Valid @RequestBody AdminVhostCreateOrderParam param) {
      return this.adminVhostOrderService.create(param);
   }

   @Operation(
      summary = "后台创建虚拟主机续费订单"
   )
   @PostMapping({"/renew"})
   public CommonResult<ManualOrderResult> renew(@Valid @RequestBody AdminVhostRenewOrderParam param) {
      return this.adminVhostOrderService.renew(param);
   }

   @Operation(
      summary = "后台创建虚拟主机升级流量订单"
   )
   @PostMapping({"/upgrade-traffic"})
   public CommonResult<ManualOrderResult> upgradeTraffic(@Valid @RequestBody AdminVhostUpgradeOrderParam param) {
      return this.adminVhostOrderService.upgradeTraffic(param);
   }
}
