package com.rxinyun.admin.order.modular.independent.service;

import com.rxinyun.admin.order.modular.independent.param.IndependentBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentCalculatePriceParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentIpUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.common.pojo.CommonResult;

public interface IndependentCalculatePriceService {
   CommonResult<IndependentCalculatePriceResponse> calculatePrice(IndependentCalculatePriceParam calculatePriceParam);

   CommonResult<IndependentCalculatePriceResponse> upgradeIp(IndependentIpUpgradeOrderParam upgradeOrderParam);

   CommonResult<IndependentCalculatePriceResponse> upgradeBandwidth(IndependentBandwidthUpgradeOrderParam upgradeOrderParam);

   CommonResult<IndependentCalculatePriceResponse> upgradeDefense(IndependentDefenseUpgradeOrderParam upgradeOrderParam);

   CommonResult<IndependentCalculatePriceResponse> renew(IndependentRenewOrderParam renewOrderParam);
}
