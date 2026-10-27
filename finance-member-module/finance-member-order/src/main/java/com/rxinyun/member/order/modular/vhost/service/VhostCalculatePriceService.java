package com.rxinyun.member.order.modular.vhost.service;

import com.rxinyun.member.order.modular.vhost.param.VhostCalculatePriceParam;
import com.rxinyun.member.order.modular.vhost.param.VhostRenewParam;
import com.rxinyun.member.order.modular.vhost.param.VhostUpgradeTrafficParam;
import com.rxinyun.member.order.modular.vhost.response.VhostCalculatePriceResponse;

public interface VhostCalculatePriceService {
   VhostCalculatePriceResponse calculateNewPurchase(VhostCalculatePriceParam param);

   VhostCalculatePriceResponse calculateRenew(VhostRenewParam param);

   VhostCalculatePriceResponse calculateUpgradeTraffic(VhostUpgradeTrafficParam param);
}
