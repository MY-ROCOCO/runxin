package com.rxinyun.admin.order.modular.manual.controller;

import com.rxinyun.admin.order.modular.manual.param.ManualCloudBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudCoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudCreateOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudDiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualCloudRenewOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentCreateOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentIpUpgradeOrderParam;
import com.rxinyun.admin.order.modular.manual.param.ManualIndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.manual.response.ManualOrderResult;
import com.rxinyun.admin.order.modular.manual.service.ManualOrderService;
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
   name = "后台手动开通与升级订单控制器"
)
@RestController
@RequestMapping({"/admin/manual/order"})
@Validated
public class ManualOrderController {
   @Resource
   private ManualOrderService manualOrderService;

   @Operation(
      summary = "后台手动开通云服务器"
   )
   @PostMapping({"/cloud/create"})
   public CommonResult<ManualOrderResult> createCloudOrder(@Valid @RequestBody ManualCloudCreateOrderParam param) {
      return this.manualOrderService.createCloudOrder(param);
   }

   @Operation(
      summary = "后台手动升级云服务器CPU内存"
   )
   @PostMapping({"/cloud/upgrade/core"})
   public CommonResult<ManualOrderResult> upgradeCloudCore(@Valid @RequestBody ManualCloudCoreUpgradeOrderParam param) {
      return this.manualOrderService.upgradeCloudCore(param);
   }

   @Operation(
      summary = "后台手动升级云服务器磁盘"
   )
   @PostMapping({"/cloud/upgrade/disk"})
   public CommonResult<ManualOrderResult> upgradeCloudDisk(@Valid @RequestBody ManualCloudDiskUpgradeOrderParam param) {
      return this.manualOrderService.upgradeCloudDisk(param);
   }

   @Operation(
      summary = "后台手动升级云服务器带宽"
   )
   @PostMapping({"/cloud/upgrade/bandwidth"})
   public CommonResult<ManualOrderResult> upgradeCloudBandwidth(@Valid @RequestBody ManualCloudBandwidthUpgradeOrderParam param) {
      return this.manualOrderService.upgradeCloudBandwidth(param);
   }

   @Operation(
      summary = "后台手动升级云服务器防御"
   )
   @PostMapping({"/cloud/upgrade/defense"})
   public CommonResult<ManualOrderResult> upgradeCloudDefense(@Valid @RequestBody ManualCloudDefenseUpgradeOrderParam param) {
      return this.manualOrderService.upgradeCloudDefense(param);
   }

   @Operation(
      summary = "后台手动续费云服务器"
   )
   @PostMapping({"/cloud/renew"})
   public CommonResult<ManualOrderResult> renewCloudOrder(@Valid @RequestBody ManualCloudRenewOrderParam param) {
      return this.manualOrderService.renewCloudOrder(param);
   }

   @Operation(
      summary = "后台手动开通独立服务器"
   )
   @PostMapping({"/independent/create"})
   public CommonResult<ManualOrderResult> createIndependentOrder(@Valid @RequestBody ManualIndependentCreateOrderParam param) {
      return this.manualOrderService.createIndependentOrder(param);
   }

   @Operation(
      summary = "后台手动升级独立服务器IP"
   )
   @PostMapping({"/independent/upgrade/ip"})
   public CommonResult<ManualOrderResult> upgradeIndependentIp(@Valid @RequestBody ManualIndependentIpUpgradeOrderParam param) {
      return this.manualOrderService.upgradeIndependentIp(param);
   }

   @Operation(
      summary = "后台手动升级独立服务器带宽"
   )
   @PostMapping({"/independent/upgrade/bandwidth"})
   public CommonResult<ManualOrderResult> upgradeIndependentBandwidth(@Valid @RequestBody ManualIndependentBandwidthUpgradeOrderParam param) {
      return this.manualOrderService.upgradeIndependentBandwidth(param);
   }

   @Operation(
      summary = "后台手动升级独立服务器防御"
   )
   @PostMapping({"/independent/upgrade/defense"})
   public CommonResult<ManualOrderResult> upgradeIndependentDefense(@Valid @RequestBody ManualIndependentDefenseUpgradeOrderParam param) {
      return this.manualOrderService.upgradeIndependentDefense(param);
   }

   @Operation(
      summary = "后台手动续费独立服务器"
   )
   @PostMapping({"/independent/renew"})
   public CommonResult<ManualOrderResult> renewIndependentOrder(@Valid @RequestBody ManualIndependentRenewOrderParam param) {
      return this.manualOrderService.renewIndependentOrder(param);
   }
}
