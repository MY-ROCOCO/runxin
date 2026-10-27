package com.rxinyun.admin.order.modular.ecs.provider;

import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.order.api.OrderServerApi;
import com.rxinyun.order.core.pojo.BaseOrderServer;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderServerApiProvider implements OrderServerApi {
   private static final Logger log = LoggerFactory.getLogger(OrderServerApiProvider.class);
   @Resource
   private OrderServerService orderServerService;

   public List<BaseOrderServer> queryByOrderNo(String orderNo) {
      List<OrderServer> list = this.orderServerService.queryByList(orderNo);
      return list == null ? null : list.stream().map(orderServer -> {
         BaseOrderServer baseOrderServer = new BaseOrderServer();
         baseOrderServer.setId(orderServer.getId());
         baseOrderServer.setOrderNo(orderServer.getOrderNo());
         baseOrderServer.setServerId(orderServer.getServerId());
         baseOrderServer.setServerName(orderServer.getServerName());
         return baseOrderServer;
      }).toList();
   }
}
