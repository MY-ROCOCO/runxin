package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrderServerConfig;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderServerConfigMapper;
import com.rxinyun.member.order.modular.ecs.param.MemberOrderServerConfigAddParam;
import com.rxinyun.member.order.modular.ecs.response.MemberServerConfigResponse;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerConfigService;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberOrderServerConfigServiceImpl
   extends ServiceImpl<MemberOrderServerConfigMapper, MemberOrderServerConfig>
   implements MemberOrderServerConfigService {
   private static final Logger log = LoggerFactory.getLogger(MemberOrderServerConfigServiceImpl.class);
   @Resource
   private MemberOrderServerConfigMapper memberOrderServerConfigMapper;
   @Resource
   private MemberOrderServerService memberOrderServerService;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberSystemVersionApi memberOsVersionApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberOrderServerConfigAddParam orderServerAddParam) {
      MemberOrderServerConfig orderServer = (MemberOrderServerConfig)BeanUtil.toBean(orderServerAddParam, MemberOrderServerConfig.class);
      this.save(orderServer);
   }

   @Override
   public List<MemberOrderServerConfig> queryByList(String orderNo) {
      LambdaQueryWrapper<MemberOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberOrderServerConfig::getOrderNo, orderNo);
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public MemberOrderServerConfig queryByOrderNo(String orderNo) {
      LambdaQueryWrapper<MemberOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberOrderServerConfig::getOrderNo, orderNo);
      return (MemberOrderServerConfig)this.getOne(lambdaQueryWrapper);
   }

   @Override
   public MemberServerConfigResponse queryByOrder(String orderNo) {
      LambdaQueryWrapper<MemberOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberOrderServerConfig::getOrderNo, orderNo);
      MemberServerConfigResponse memberServerResponse = new MemberServerConfigResponse();
      MemberOrderServerConfig memberOrderServerConfig = (MemberOrderServerConfig)this.getOne(lambdaQueryWrapper);
      BeanUtils.copyProperties(memberOrderServerConfig, memberServerResponse);
      if (null != memberOrderServerConfig.getLineId()) {
         BaseMemberLocalLine baseMemberLocalLine = this.memberLocalLineApi.selectByLineId(memberOrderServerConfig.getLineId());
         if (baseMemberLocalLine != null) {
            memberServerResponse.setLineName(baseMemberLocalLine.getLineName());
            memberServerResponse.setAvailableId(baseMemberLocalLine.getAvailableId());
            memberServerResponse.setAvailableName(baseMemberLocalLine.getAvailableName());
         }
      }

      if (null != memberOrderServerConfig.getSystemId()) {
         BaseMemberSystemVersion baseMemberOsVersion = this.memberOsVersionApi.selectByOsVersionId(memberOrderServerConfig.getSystemId());
         memberServerResponse.setSystemClassName(baseMemberOsVersion.getOsClassName());
         memberServerResponse.setSystemVersionName(baseMemberOsVersion.getOsName());
      }

      Map<String, String> serverMap = this.memberOrderServerService.queryServerIdByOrderNo(orderNo);
      if (null != serverMap) {
         memberServerResponse.setServerMap(serverMap);
      }

      return memberServerResponse;
   }
}
