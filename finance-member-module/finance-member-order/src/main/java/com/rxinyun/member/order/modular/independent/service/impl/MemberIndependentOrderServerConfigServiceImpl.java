package com.rxinyun.member.order.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderServerService;
import com.rxinyun.member.order.modular.independent.entity.MemberIndependentOrderServerConfig;
import com.rxinyun.member.order.modular.independent.mapper.MemberIndependentOrderServerConfigMapper;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentOrderServerConfigAddParam;
import com.rxinyun.member.order.modular.independent.response.IndependentOrderServerConfigResponse;
import com.rxinyun.member.order.modular.independent.service.MemberIndependentOrderServerConfigService;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.MemberIndependentSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberIndependentOrderServerConfigServiceImpl
   extends ServiceImpl<MemberIndependentOrderServerConfigMapper, MemberIndependentOrderServerConfig>
   implements MemberIndependentOrderServerConfigService {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentOrderServerConfigServiceImpl.class);
   @Resource
   private MemberIndependentOrderServerConfigMapper memberIndependentOrderServerConfigMapper;
   @Resource
   private MemberOrderServerService memberOrderServerService;
   @Resource
   private IndependentLineApi memberLocalLineApi;
   @Resource
   private MemberIndependentSystemVersionApi memberIndependentSystemVersionApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberIndependentOrderServerConfigAddParam orderServerAddParam) {
      MemberIndependentOrderServerConfig orderServer = (MemberIndependentOrderServerConfig)BeanUtil.toBean(
         orderServerAddParam, MemberIndependentOrderServerConfig.class
      );
      this.save(orderServer);
   }

   @Override
   public List<MemberIndependentOrderServerConfig> queryByList(String orderNo) {
      LambdaQueryWrapper<MemberIndependentOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberIndependentOrderServerConfig::getOrderNo, orderNo);
      return this.list(lambdaQueryWrapper);
   }

   @Override
   public MemberIndependentOrderServerConfig queryByOrderNo(String orderNo) {
      LambdaQueryWrapper<MemberIndependentOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberIndependentOrderServerConfig::getOrderNo, orderNo);
      return (MemberIndependentOrderServerConfig)this.getOne(lambdaQueryWrapper);
   }

   @Override
   public IndependentOrderServerConfigResponse queryByOrder(String orderNo) {
      LambdaQueryWrapper<MemberIndependentOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(MemberIndependentOrderServerConfig::getOrderNo, orderNo);
      IndependentOrderServerConfigResponse memberServerResponse = new IndependentOrderServerConfigResponse();
      MemberIndependentOrderServerConfig memberOrderServerConfig = (MemberIndependentOrderServerConfig)this.getOne(lambdaQueryWrapper);
      BeanUtils.copyProperties(memberOrderServerConfig, memberServerResponse);
      if (null != memberOrderServerConfig.getLineId()) {
         BaseIndependentLine baseIndependentLine = this.memberLocalLineApi.selectByLineId(memberOrderServerConfig.getLineId());
         memberServerResponse.setLineName(baseIndependentLine.getName());
      }

      if (null != memberOrderServerConfig.getSystemId()) {
         BaseMemberIndependentSystemVersion memberOsVersion = this.memberIndependentSystemVersionApi.selectByOsVersionId(memberOrderServerConfig.getSystemId());
         log.info("查询系统版本信息，系统id:{},系统版本信息:{}", memberOrderServerConfig.getSystemId(), JSON.toJSONString(memberOsVersion));
         memberServerResponse.setSystemClass(memberOsVersion.getOsClassName());
         memberServerResponse.setSystemName(memberOsVersion.getOsName());
      }

      Map<String, String> serverMap = this.memberOrderServerService.queryServerIdByOrderNo(orderNo);
      if (null != serverMap) {
         memberServerResponse.setServerMap(serverMap);
      }

      return memberServerResponse;
   }
}
