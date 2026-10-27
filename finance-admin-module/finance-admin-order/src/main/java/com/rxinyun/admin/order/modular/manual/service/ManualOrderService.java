package com.rxinyun.admin.order.modular.manual.service;

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
import com.rxinyun.common.pojo.CommonResult;

public interface ManualOrderService {
   CommonResult<ManualOrderResult> createCloudOrder(ManualCloudCreateOrderParam param);

   CommonResult<ManualOrderResult> upgradeCloudCore(ManualCloudCoreUpgradeOrderParam param);

   CommonResult<ManualOrderResult> upgradeCloudDisk(ManualCloudDiskUpgradeOrderParam param);

   CommonResult<ManualOrderResult> upgradeCloudBandwidth(ManualCloudBandwidthUpgradeOrderParam param);

   CommonResult<ManualOrderResult> upgradeCloudDefense(ManualCloudDefenseUpgradeOrderParam param);

   CommonResult<ManualOrderResult> renewCloudOrder(ManualCloudRenewOrderParam param);

   CommonResult<ManualOrderResult> renewCloudOrder(ManualCloudRenewOrderParam param, String settleRemark);

   CommonResult<ManualOrderResult> createIndependentOrder(ManualIndependentCreateOrderParam param);

   CommonResult<ManualOrderResult> upgradeIndependentIp(ManualIndependentIpUpgradeOrderParam param);

   CommonResult<ManualOrderResult> upgradeIndependentBandwidth(ManualIndependentBandwidthUpgradeOrderParam param);

   CommonResult<ManualOrderResult> upgradeIndependentDefense(ManualIndependentDefenseUpgradeOrderParam param);

   CommonResult<ManualOrderResult> renewIndependentOrder(ManualIndependentRenewOrderParam param);

   CommonResult<ManualOrderResult> renewIndependentOrder(ManualIndependentRenewOrderParam param, String settleRemark);
}
