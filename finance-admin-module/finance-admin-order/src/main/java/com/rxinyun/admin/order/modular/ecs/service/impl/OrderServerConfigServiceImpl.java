package com.rxinyun.admin.order.modular.ecs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.order.modular.ecs.entity.OrderServerConfig;
import com.rxinyun.admin.order.modular.ecs.mapper.OrderServerConfigMapper;
import com.rxinyun.admin.order.modular.ecs.param.OrderServerConfigAddParam;
import com.rxinyun.admin.order.modular.ecs.response.OrderServerConfigResponse;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServerConfigServiceImpl extends ServiceImpl<OrderServerConfigMapper, OrderServerConfig> implements OrderServerConfigService {
   private static final Logger log = LoggerFactory.getLogger(OrderServerConfigServiceImpl.class);
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberSystemVersionApi memberOsVersionApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(OrderServerConfigAddParam orderServerAddParam) {
      OrderServerConfig orderServer = (OrderServerConfig)BeanUtil.toBean(orderServerAddParam, OrderServerConfig.class);
      this.save(orderServer);
   }

   @Override
   public OrderServerConfigResponse queryByOrderNo(String orderNo) {
      LambdaQueryWrapper<OrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(OrderServerConfig::getOrderNo, orderNo);
      OrderServerConfigResponse orderServerConfigResponse = new OrderServerConfigResponse();
      OrderServerConfig memberOrderServerConfig = (OrderServerConfig)this.getOne(lambdaQueryWrapper);
      if (memberOrderServerConfig == null) {
         return null;
      } else {
         BeanUtils.copyProperties(memberOrderServerConfig, orderServerConfigResponse);
         if (null != memberOrderServerConfig.getLineId()) {
            BaseMemberLocalLine baseMemberLocalLine = this.memberLocalLineApi.selectByLineId(memberOrderServerConfig.getLineId());
            if (baseMemberLocalLine != null) {
               orderServerConfigResponse.setLineName(baseMemberLocalLine.getLineName());
               orderServerConfigResponse.setAvailableId(baseMemberLocalLine.getAvailableId());
               orderServerConfigResponse.setAvailableName(baseMemberLocalLine.getAvailableName());
            }
         }

         if (null != memberOrderServerConfig.getSystemId()) {
            BaseMemberSystemVersion baseMemberOsVersion = this.memberOsVersionApi.selectByOsVersionId(memberOrderServerConfig.getSystemId());
            orderServerConfigResponse.setSystemClassName(baseMemberOsVersion.getOsClassName());
            orderServerConfigResponse.setSystemVersionName(baseMemberOsVersion.getOsName());
         }

         Map<String, String> serverMap = this.orderServerService.queryServerIdByOrderNo(orderNo);
         if (null != serverMap) {
            orderServerConfigResponse.setServerMap(serverMap);
         }

         return orderServerConfigResponse;
      }
   }
}
