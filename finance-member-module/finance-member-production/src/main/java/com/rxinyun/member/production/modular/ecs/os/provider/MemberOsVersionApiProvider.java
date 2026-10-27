package com.rxinyun.member.production.modular.ecs.os.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsVersion;
import com.rxinyun.member.production.modular.ecs.os.service.MemberOsVersionService;
import com.rxinyun.production.api.MemberSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberOsVersionApiProvider implements MemberSystemVersionApi {
   private static final Logger log = LoggerFactory.getLogger(MemberOsVersionApiProvider.class);
   @Resource
   private MemberOsVersionService memberOsVersionService;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   public BaseMemberSystemVersion selectByOsVersionId(String osVersionId) {
      MemberOsVersion memberOsVersion = this.memberOsVersionService.queryEntity(osVersionId);
      log.info("selectByOsVersionId:{}", JSONObject.toJSONString(memberOsVersion));
      if (memberOsVersion != null) {
         BaseMemberSystemVersion vo = (BaseMemberSystemVersion)BeanUtil.toBean(memberOsVersion, BaseMemberSystemVersion.class);
         if (ObjectUtil.isNotEmpty(vo.getResourcePoolId())) {
            BaseResourcePool pool = this.resourcePoolApi.getById(vo.getResourcePoolId());
            if (ObjectUtil.isNotEmpty(pool)) {
               vo.setResourcePoolType(pool.getPoolType());
               vo.setResourcePoolName(pool.getName());
            }
         }

         return vo;
      } else {
         return null;
      }
   }
}
