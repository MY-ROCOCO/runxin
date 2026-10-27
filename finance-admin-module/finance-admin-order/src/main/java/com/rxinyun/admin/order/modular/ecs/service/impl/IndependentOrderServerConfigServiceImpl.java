package com.rxinyun.admin.order.modular.ecs.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.admin.order.modular.ecs.mapper.IndependentOrderServerConfigMapper;
import com.rxinyun.admin.order.modular.ecs.response.IndependentOrderServerConfigResponse;
import com.rxinyun.admin.order.modular.ecs.service.IndependentOrderServerConfigService;
import com.rxinyun.admin.order.modular.ecs.service.OrderServerService;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.MemberIndependentSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;
import jakarta.annotation.Resource;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class IndependentOrderServerConfigServiceImpl
   extends ServiceImpl<IndependentOrderServerConfigMapper, IndependentOrderServerConfig>
   implements IndependentOrderServerConfigService {
   private static final Logger log = LoggerFactory.getLogger(IndependentOrderServerConfigServiceImpl.class);
   @Resource
   private OrderServerService orderServerService;
   @Resource
   private IndependentLineApi memberLocalLineApi;
   @Resource
   private MemberIndependentSystemVersionApi memberIndependentSystemVersionApi;

   @Override
   public IndependentOrderServerConfigResponse queryByOrderNo(String orderNo) {
      LambdaQueryWrapper<IndependentOrderServerConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.eq(IndependentOrderServerConfig::getOrderNo, orderNo);
      IndependentOrderServerConfigResponse orderServerConfigResponse = new IndependentOrderServerConfigResponse();
      IndependentOrderServerConfig independentOrderServerConfig = (IndependentOrderServerConfig)this.getOne(lambdaQueryWrapper);
      BeanUtils.copyProperties(independentOrderServerConfig, orderServerConfigResponse);
      if (null != independentOrderServerConfig.getLineId()) {
         BaseIndependentLine baseIndependentLine = this.memberLocalLineApi.selectByLineId(independentOrderServerConfig.getLineId());
         orderServerConfigResponse.setLineName(baseIndependentLine.getName());
      }

      if (null != independentOrderServerConfig.getSystemId()) {
         BaseMemberIndependentSystemVersion memberOsVersion = this.memberIndependentSystemVersionApi
            .selectByOsVersionId(independentOrderServerConfig.getSystemId());
         log.info("查询系统版本信息，系统id:{},系统版本信息:{}", independentOrderServerConfig.getSystemId(), JSON.toJSONString(memberOsVersion));
         orderServerConfigResponse.setSystemClass(memberOsVersion.getOsClassName());
         orderServerConfigResponse.setSystemName(memberOsVersion.getOsName());
      }

      Map<String, String> serverMap = this.orderServerService.queryServerIdByOrderNo(orderNo);
      if (null != serverMap) {
         orderServerConfigResponse.setServerMap(serverMap);
      }

      return orderServerConfigResponse;
   }

   @Override
   public void saveNew(IndependentOrderServerConfig config) {
      if (config != null) {
         if (StrUtil.isBlank(config.getId())) {
            config.setId(SnowflakeIdGenerator.generateId());
         }

         this.save(config);
      }
   }
}
