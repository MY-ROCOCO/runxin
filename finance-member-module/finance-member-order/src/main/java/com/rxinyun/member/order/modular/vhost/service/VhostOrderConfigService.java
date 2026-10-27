package com.rxinyun.member.order.modular.vhost.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.order.modular.vhost.entity.VhostOrderConfig;

public interface VhostOrderConfigService extends IService<VhostOrderConfig> {
   VhostOrderConfig queryByOrderNo(String orderNo);
}
