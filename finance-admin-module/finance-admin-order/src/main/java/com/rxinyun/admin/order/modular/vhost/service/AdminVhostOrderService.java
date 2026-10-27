package com.rxinyun.admin.order.modular.vhost.service;

import com.rxinyun.admin.order.modular.manual.response.ManualOrderResult;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostCreateOrderParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostRenewOrderParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostUpgradeOrderParam;
import com.rxinyun.common.pojo.CommonResult;

public interface AdminVhostOrderService {
   CommonResult<ManualOrderResult> create(AdminVhostCreateOrderParam param);

   CommonResult<ManualOrderResult> renew(AdminVhostRenewOrderParam param);

   CommonResult<ManualOrderResult> upgradeTraffic(AdminVhostUpgradeOrderParam param);
}
