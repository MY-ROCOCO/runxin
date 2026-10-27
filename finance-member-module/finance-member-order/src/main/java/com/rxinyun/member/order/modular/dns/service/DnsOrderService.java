package com.rxinyun.member.order.modular.dns.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.dns.param.DnsOrderCreateParam;
import com.rxinyun.member.order.modular.dns.param.DnsRenewOrderParam;
import com.rxinyun.member.order.modular.dns.param.DnsUpgradeQuotaOrderParam;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;

public interface DnsOrderService {
   CommonResult<MemberOrder> create(DnsOrderCreateParam param);

   CommonResult<MemberOrder> renew(DnsRenewOrderParam param);

   CommonResult<MemberOrder> upgradeQuota(DnsUpgradeQuotaOrderParam param);
}
