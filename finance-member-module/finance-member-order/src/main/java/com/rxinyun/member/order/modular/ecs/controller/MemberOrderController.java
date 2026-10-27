package com.rxinyun.member.order.modular.ecs.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.entity.MemberServerRefundRecord;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderIdParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderPageParam;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberPayParam;
import com.rxinyun.member.order.modular.ecs.param.MemberRenewOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberServerRefundParam;
import com.rxinyun.member.order.modular.ecs.param.MemberServerRefundRecordPageParam;
import com.rxinyun.member.order.modular.ecs.response.MemberOrderResponse;
import com.rxinyun.member.order.modular.ecs.response.MemberServerRefundPreviewResponse;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderService;
import com.rxinyun.member.order.modular.ecs.service.MemberServerRefundRecordService;
import com.rxinyun.member.order.modular.ecs.service.MemberServerRefundService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/member"})
@Validated
public class MemberOrderController {
   private static final Logger log = LoggerFactory.getLogger(MemberOrderController.class);
   @Resource
   private MemberOrderService memberOrderService;
   @Resource
   private MemberServerRefundService memberServerRefundService;
   @Resource
   private MemberServerRefundRecordService memberServerRefundRecordService;

   @Operation(
      summary = "获取订单分页",
      tags = {"订单控制器"}
   )
   @GetMapping({"order/page"})
   public CommonResult<Page<MemberOrder>> page(MemberOrderPageParam pageParam) {
      return CommonResult.data(this.memberOrderService.page(pageParam));
   }

   @Operation(
      summary = "获取服务器订单详情",
      tags = {"订单控制器"}
   )
   @GetMapping({"order/detail"})
   public CommonResult<MemberOrderResponse> detail(@Valid MemberOrderIdParam orderIdParam) {
      return this.memberOrderService.detail(orderIdParam);
   }

   @Operation(
      summary = "云服务器新购创建订单",
      tags = {"云服务器订单控制器"}
   )
   @PostMapping({"/order/create"})
   public CommonResult<MemberOrder> create(@Valid @RequestBody MemberOrderParam orderParam) {
      log.info("创建订单开始，参数:{}", JSONUtil.toJsonStr(orderParam));
      return this.memberOrderService.create(orderParam);
   }

   @Operation(
      summary = "云服务器CPU/内存升级创建订单",
      tags = {"云服务器订单控制器"}
   )
   @PostMapping({"/order/upgrade/core"})
   public CommonResult<MemberOrder> upgradeCpuRam(@Valid @RequestBody MemberCoreUpgradeOrderParam upgradeOrderParam) {
      log.info("CPU/内存升级创建订单开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberOrderService.upgradeCore(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器硬盘升级创建订单",
      tags = {"云服务器订单控制器"}
   )
   @PostMapping({"/order/upgrade/disk"})
   public CommonResult<MemberOrder> upgradeDisk(@Valid @RequestBody MemberDiskUpgradeOrderParam upgradeOrderParam) {
      log.info("硬盘升级创建订单开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberOrderService.upgradeDisk(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器带宽升级创建订单",
      tags = {"云服务器订单控制器"}
   )
   @PostMapping({"/order/upgrade/bandwidth"})
   public CommonResult<MemberOrder> upgradeBandwidth(@Valid @RequestBody MemberBandwidthUpgradeOrderParam upgradeOrderParam) {
      log.info("带宽升级创建订单开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberOrderService.upgradeBandwidth(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器防御升级创建订单",
      tags = {"云服务器订单控制器"}
   )
   @PostMapping({"/order/upgrade/defense"})
   public CommonResult<MemberOrder> upgradeDefense(@Valid @RequestBody MemberDefenseUpgradeOrderParam upgradeOrderParam) {
      log.info("防御升级创建订单开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberOrderService.upgradeDefense(upgradeOrderParam);
   }

   @Operation(
      summary = "云服务器续费订单创建订单",
      tags = {"云服务器订单控制器"}
   )
   @PostMapping({"/order/renew"})
   public CommonResult<MemberOrder> renew(@Valid @RequestBody MemberRenewOrderParam renewOrderParam) {
      log.info("续费订单创建订单开始，参数:{}", JSONUtil.toJsonStr(renewOrderParam));
      return this.memberOrderService.renew(renewOrderParam);
   }

   @Operation(
      summary = "订单支付",
      tags = {"订单控制器"}
   )
   @PostMapping({"/order/pay"})
   public CommonResult pay(@Valid @RequestBody MemberPayParam orderParam) {
      log.info("订单支付开始，参数:{}", JSONUtil.toJsonStr(orderParam));
      return this.memberOrderService.pay(orderParam);
   }

   @Operation(
      summary = "服务器退款计价（仅按天退款）",
      tags = {"订单控制器"}
   )
   @PostMapping({"/order/server/refund/preview"})
   public CommonResult<MemberServerRefundPreviewResponse> serverRefundPreview(@Valid @RequestBody MemberServerRefundParam refundParam) {
      log.info("服务器退款试算开始，参数:{}", JSONUtil.toJsonStr(refundParam));
      return this.memberServerRefundService.preview(refundParam);
   }

   @Operation(
      summary = "服务器退款申请提交",
      tags = {"订单控制器"}
   )
   @PostMapping({"/order/server/refund/execute"})
   public CommonResult<String> serverRefundExecute(@Valid @RequestBody MemberServerRefundParam refundParam) {
      log.info("服务器退款申请开始，参数:{}", JSONUtil.toJsonStr(refundParam));
      return this.memberServerRefundService.execute(refundParam);
   }

   @Operation(
      summary = "服务器退款申请记录分页",
      tags = {"订单控制器"}
   )
   @GetMapping({"/order/server/refund/record/page"})
   public CommonResult<Page<MemberServerRefundRecord>> serverRefundRecordPage(MemberServerRefundRecordPageParam pageParam) {
      return CommonResult.data(this.memberServerRefundRecordService.page(pageParam));
   }
}
