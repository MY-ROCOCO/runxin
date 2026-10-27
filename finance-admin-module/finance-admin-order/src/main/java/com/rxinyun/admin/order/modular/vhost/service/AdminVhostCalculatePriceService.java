package com.rxinyun.admin.order.modular.vhost.service;

import com.rxinyun.admin.order.modular.vhost.param.AdminVhostCalculateParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostRenewParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostUpgradeParam;
import com.rxinyun.admin.order.modular.vhost.response.AdminVhostPriceResponse;

public interface AdminVhostCalculatePriceService {
   AdminVhostPriceResponse calculateNewPurchase(AdminVhostCalculateParam param);

   AdminVhostPriceResponse calculateRenew(AdminVhostRenewParam param);

   AdminVhostPriceResponse calculateUpgradeTraffic(AdminVhostUpgradeParam param);
}
