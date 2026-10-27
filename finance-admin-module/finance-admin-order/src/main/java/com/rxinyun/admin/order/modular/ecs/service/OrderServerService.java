package com.rxinyun.admin.order.modular.ecs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerAddParam;
import java.util.List;
import java.util.Map;

public interface OrderServerService extends IService<OrderServer> {
   void add(OrderServerAddParam orderServerAddParam);

   List<OrderServer> queryByList(String orderNo);

   Map<String, String> queryServerIdByOrderNo(String orderNo);

   OrderServer queryByOrderNo(String orderNo);

   void removeByServerId(String serverId);
}
