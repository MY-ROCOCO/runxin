package com.rxinyun.admin.order.modular.ecs.service;

import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.admin.order.modular.ecs.response.IndependentOrderServerConfigResponse;

public interface IndependentOrderServerConfigService {
   IndependentOrderServerConfigResponse queryByOrderNo(String orderNo);

   void saveNew(IndependentOrderServerConfig config);
}
