package com.rxinyun.member.order.modular.independent.controller;

import cn.hutool.json.JSONUtil;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.service.MemberIndependentOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立订单控制器"
)
@RestController
@RequestMapping({"/member/independent"})
@Validated
public class MemberIndependentOrderController {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentOrderController.class);
   @Resource
   private MemberIndependentOrderService memberIndependentOrderService;

   @Operation(
      summary = "独立服务器新购创建订单"
   )
   @PostMapping({"/order/create"})
   public CommonResult<MemberOrder> create(@Valid @RequestBody MemberIndependentOrderParam orderParam) {
      log.info("创建独立服务器订单开始，参数:{}", JSONUtil.toJsonStr(orderParam));
      return this.memberIndependentOrderService.create(orderParam);
   }

   @Operation(
      summary = "独立服务器增加IP创建订单"
   )
   @PostMapping({"/order/upgrade/ip"})
   public CommonResult<MemberOrder> upgradeIp(@Valid @RequestBody MemberIndependentIpUpgradeOrderParam upgradeOrderParam) {
      log.info("独立服务器订单增加IP开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberIndependentOrderService.upgradeIp(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器带宽升级创建订单"
   )
   @PostMapping({"/order/upgrade/bandwidth"})
   public CommonResult<MemberOrder> upgradeBandwidth(@Valid @RequestBody MemberIndependentBandwidthUpgradeOrderParam upgradeOrderParam) {
      log.info("独立服务器订单带宽升级开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberIndependentOrderService.upgradeBandwidth(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器防御升级创建订单"
   )
   @PostMapping({"/order/upgrade/defense"})
   public CommonResult<MemberOrder> upgradeDefense(@Valid @RequestBody MemberIndependentDefenseUpgradeOrderParam upgradeOrderParam) {
      log.info("独立服务器订单防御升级开始，参数:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      return this.memberIndependentOrderService.upgradeDefense(upgradeOrderParam);
   }

   @Operation(
      summary = "独立服务器续费订单创建订单"
   )
   @PostMapping({"/order/renew"})
   public CommonResult<MemberOrder> renew(@Valid @RequestBody MemberIndependentRenewOrderParam renewOrderParam) {
      log.info("独立服务器订单续费开始，参数:{}", JSONUtil.toJsonStr(renewOrderParam));
      return this.memberIndependentOrderService.renew(renewOrderParam);
   }
}
