package com.rxinyun.member.order.modular.dns.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.order.modular.dns.entity.DnsOrderConfig;

public interface DnsOrderConfigService extends IService<DnsOrderConfig> {
   DnsOrderConfig queryByOrderNo(String orderNo);
}
