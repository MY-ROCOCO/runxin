package com.rxinyun.member.order.modular.vhost.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.order.modular.vhost.entity.VhostOrderConfig;
import com.rxinyun.member.order.modular.vhost.mapper.VhostOrderConfigMapper;
import com.rxinyun.member.order.modular.vhost.service.VhostOrderConfigService;
import org.springframework.stereotype.Service;

@Service
public class VhostOrderConfigServiceImpl extends ServiceImpl<VhostOrderConfigMapper, VhostOrderConfig> implements VhostOrderConfigService {
   @Override
   public VhostOrderConfig queryByOrderNo(String orderNo) {
      return (VhostOrderConfig)this.getOne((Wrapper<VhostOrderConfig>)new LambdaQueryWrapper<VhostOrderConfig>().eq(VhostOrderConfig::getOrderNo, orderNo));
   }
}
