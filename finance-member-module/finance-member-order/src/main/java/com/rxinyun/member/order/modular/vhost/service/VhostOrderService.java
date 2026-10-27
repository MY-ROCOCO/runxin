package com.rxinyun.member.order.modular.vhost.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.vhost.param.VhostOrderCreateParam;
import com.rxinyun.member.order.modular.vhost.param.VhostRenewOrderParam;
import com.rxinyun.member.order.modular.vhost.param.VhostUpgradeTrafficOrderParam;

public interface VhostOrderService {
   CommonResult<MemberOrder> create(VhostOrderCreateParam param);

   CommonResult<MemberOrder> renew(VhostRenewOrderParam param);

   CommonResult<MemberOrder> upgradeTraffic(VhostUpgradeTrafficOrderParam param);
}
