package com.rxinyun.member.order.modular.independent.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentCalculatePriceParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;

public interface IndependentCalculatePriceService {
   CommonResult<IndependentCalculatePriceResponse> calculatePrice(MemberIndependentCalculatePriceParam calculatePriceParam);

   CommonResult<IndependentCalculatePriceResponse> upgradeIp(MemberIndependentIpUpgradeOrderParam upgradeOrderParam);

   CommonResult<IndependentCalculatePriceResponse> upgradeBandwidth(MemberIndependentBandwidthUpgradeOrderParam upgradeOrderParam);

   CommonResult<IndependentCalculatePriceResponse> upgradeDefense(MemberIndependentDefenseUpgradeOrderParam upgradeOrderParam);

   CommonResult<IndependentCalculatePriceResponse> renew(MemberIndependentRenewOrderParam renewOrderParam);
}
