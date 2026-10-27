package com.rxinyun.admin.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServerConfig;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerConfigAddParam;
import com.rxinyun.admin.order.modular.ecs.response.OrderServerConfigResponse;

public interface OrderServerConfigService extends IService<OrderServerConfig> {
   void add(OrderServerConfigAddParam orderServerAddParam);

   OrderServerConfigResponse queryByOrderNo(String orderNo);
}
