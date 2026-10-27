package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServer;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderServerMapper;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerAddParam;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberOrderServerServiceImpl extends ServiceImpl<MemberOrderServerMapper, MemberOrderServer> implements MemberOrderServerService {
   @Resource
   private MemberOrderServerMapper memberOrderServerMapper;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberOrderServerAddParam orderServerAddParam) {
      MemberOrderServer orderServer = (MemberOrderServer)BeanUtil.toBean(orderServerAddParam, MemberOrderServer.class);
      this.save(orderServer);
   }

   @Override
   public List<MemberOrderServer> queryByList(String orderNo) {
      LambdaQueryWrapper<MemberOrderServer> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberOrderServer::getOrderNo, orderNo);
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public MemberOrderServer queryByOrderNo(String orderNo) {
      LambdaQueryWrapper<MemberOrderServer> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberOrderServer::getOrderNo, orderNo);
      return (MemberOrderServer)this.getOne(lambdaQueryWrapper);
   }

   @Override
   public Map<String, String> queryServerIdByOrderNo(String orderNo) {
      LambdaQueryWrapper<MemberOrderServer> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberOrderServer::getOrderNo, orderNo);
      return this.list(lambdaQueryWrapper).stream().collect(Collectors.toMap(MemberOrderServer::getServerId, MemberOrderServer::getServerName));
   }
}
