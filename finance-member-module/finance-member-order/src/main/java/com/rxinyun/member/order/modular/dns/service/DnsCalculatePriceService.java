package com.rxinyun.member.order.modular.dns.service;

import com.rxinyun.member.order.modular.dns.param.DnsCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.param.DnsRenewCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.param.DnsUpgradeQuotaCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.response.DnsCalculatePriceResponse;

public interface DnsCalculatePriceService {
   DnsCalculatePriceResponse calculateNewPurchase(DnsCalculatePriceParam param);

   DnsCalculatePriceResponse calculateRenew(DnsRenewCalculatePriceParam param);

   DnsCalculatePriceResponse calculateUpgradeQuota(DnsUpgradeQuotaCalculatePriceParam param);
}
