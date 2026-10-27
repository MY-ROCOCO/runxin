package com.rxinyun.admin.order.modular.ecs.service;

import com.rxinyun.admin.order.modular.ecs.param.BandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.admin.order.modular.ecs.param.CoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.common.pojo.CommonResult;

public interface CalculatePriceService {
   CommonResult<CalculatePriceResponse> calculatePrice(CalculatePriceParam calculatePriceParam);

   CommonResult<CalculatePriceResponse> upgradeCore(CoreUpgradeOrderParam upgradeOrderParam);

   CommonResult<CalculatePriceResponse> upgradeDisk(DiskUpgradeOrderParam upgradeOrderParam);

   CommonResult<CalculatePriceResponse> upgradeBandwidth(BandwidthUpgradeOrderParam upgradeOrderParam);

   CommonResult<CalculatePriceResponse> upgradeDefense(DefenseUpgradeOrderParam upgradeOrderParam);

   CommonResult<CalculatePriceResponse> renew(RenewOrderParam renewOrderParam);
}
