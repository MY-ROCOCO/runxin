package com.rxinyun.admin.order.modular.ecs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServer;
import com.rxinyun.admin.order.modular.ecs.mapper.OrderServerMapper;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerAddParam;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServerServiceImpl extends ServiceImpl<OrderServerMapper, OrderServer> implements OrderServerService {
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(OrderServerAddParam orderServerAddParam) {
      OrderServer orderServer = (OrderServer)BeanUtil.toBean(orderServerAddParam, OrderServer.class);
      this.save(orderServer);
   }

   @Override
   public List<OrderServer> queryByList(String orderNo) {
      LambdaQueryWrapper<OrderServer> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(OrderServer::getOrderNo, orderNo);
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public OrderServer queryByOrderNo(String orderNo) {
      LambdaQueryWrapper<OrderServer> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(OrderServer::getOrderNo, orderNo);
      return (OrderServer)this.getOne(lambdaQueryWrapper);
   }

   @Override
   public Map<String, String> queryServerIdByOrderNo(String orderNo) {
      LambdaQueryWrapper<OrderServer> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(OrderServer::getOrderNo, orderNo);
      return this.list(lambdaQueryWrapper).stream().collect(Collectors.toMap(OrderServer::getServerId, OrderServer::getServerName));
   }

   @Override
   public void removeByServerId(String serverId) {
      LambdaQueryWrapper<OrderServer> w = new LambdaQueryWrapper();
      w.eq(OrderServer::getServerId, serverId);
      this.remove(w);
   }
}
