package com.rxinyun.member.order.modular.independent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;

public interface MemberIndependentOrderService extends IService<MemberOrder> {
   CommonResult<MemberOrder> create(MemberIndependentOrderParam orderParam);

   CommonResult<MemberOrder> upgradeIp(MemberIndependentIpUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberOrder> upgradeBandwidth(MemberIndependentBandwidthUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberOrder> upgradeDefense(MemberIndependentDefenseUpgradeOrderParam upgradeOrderParam);

   CommonResult<MemberOrder> renew(MemberIndependentRenewOrderParam renewOrderParam);
}
