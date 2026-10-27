package com.rxinyun.member.production.modular.independent.os.provider;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsVersion;
import com.rxinyun.member.production.modular.independent.os.service.MemberIndependentOsVersionService;
import com.rxinyun.production.api.MemberIndependentSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberIndependentOsVersionApiProvider implements MemberIndependentSystemVersionApi {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentOsVersionApiProvider.class);
   @Resource
   private MemberIndependentOsVersionService memberIndependentOsVersionService;

   public BaseMemberIndependentSystemVersion selectByOsVersionId(String osVersionId) {
      MemberIndependentOsVersion memberOsVersion = this.memberIndependentOsVersionService.queryEntity(osVersionId);
      log.info("selectByOsVersionId:{}", JSONObject.toJSONString(memberOsVersion));
      return memberOsVersion != null ? (BaseMemberIndependentSystemVersion)BeanUtil.toBean(memberOsVersion, BaseMemberIndependentSystemVersion.class) : null;
   }
}
