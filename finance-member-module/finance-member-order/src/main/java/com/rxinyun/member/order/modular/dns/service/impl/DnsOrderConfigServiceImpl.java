package com.rxinyun.member.order.modular.dns.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.order.modular.dns.entity.DnsOrderConfig;
import com.rxinyun.member.order.modular.dns.mapper.DnsOrderConfigMapper;
import com.rxinyun.member.order.modular.dns.service.DnsOrderConfigService;
import org.springframework.stereotype.Service;

@Service
public class DnsOrderConfigServiceImpl extends ServiceImpl<DnsOrderConfigMapper, DnsOrderConfig> implements DnsOrderConfigService {
   @Override
   public DnsOrderConfig queryByOrderNo(String orderNo) {
      return (DnsOrderConfig)this.getOne((Wrapper<DnsOrderConfig>)new LambdaQueryWrapper<DnsOrderConfig>().eq(DnsOrderConfig::getOrderNo, orderNo));
   }
}
