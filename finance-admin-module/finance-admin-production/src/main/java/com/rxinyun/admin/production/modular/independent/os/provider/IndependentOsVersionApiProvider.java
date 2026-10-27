package com.rxinyun.admin.production.modular.independent.os.provider;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsVersion;
import com.rxinyun.admin.production.modular.independent.os.service.OsVersionService;
import com.rxinyun.admin.production.modular.util.ResourcePoolDisplayHelper;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.production.admin.IndependentSystemVersionApi;
import com.rxinyun.production.admin.core.pojo.BaseIndependentSystemVersion;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IndependentOsVersionApiProvider implements IndependentSystemVersionApi {
   private static final Logger log = LoggerFactory.getLogger(IndependentOsVersionApiProvider.class);
   @Resource
   private OsVersionService osVersionService;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   public BaseIndependentSystemVersion selectByOsVersionId(String osVersionId) {
      IndependentOsVersion osVersion = this.osVersionService.queryEntity(osVersionId);
      log.info("selectByOsVersionId:{}", JSONObject.toJSONString(osVersion));
      if (osVersion != null) {
         BaseIndependentSystemVersion bean = (BaseIndependentSystemVersion)BeanUtil.toBean(osVersion, BaseIndependentSystemVersion.class);
         ResourcePoolDisplayHelper.ifPresent(this.resourcePoolApi, bean.getResourcePoolId(), pool -> {
            bean.setResourcePoolType(pool.getPoolType());
            bean.setResourcePoolName(pool.getName());
         });
         return bean;
      } else {
         return null;
      }
   }
}
