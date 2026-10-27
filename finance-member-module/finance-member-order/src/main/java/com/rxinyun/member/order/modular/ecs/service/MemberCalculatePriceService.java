package com.rxinyun.member.order.modular.ecs.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberRenewOrderParam;
import com.rxinyun.member.order.modular.ecs.response.MemberCalculatePriceResponse;

public interface MemberCalculatePriceService {
   CommonResult<MemberCalculatePriceResponse> calculatePrice(CalculatePriceParam calculatePriceParam);

   CommonResult<MemberCalculatePriceResponse> upgradeCore(MemberCoreUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberCalculatePriceResponse> upgradeDisk(MemberDiskUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberCalculatePriceResponse> upgradeBandwidth(MemberBandwidthUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberCalculatePriceResponse> upgradeDefense(MemberDefenseUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberCalculatePriceResponse> renew(MemberRenewOrderParam renewOrderParam);
}
