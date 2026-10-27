package com.rxinyun.member.tools.modular.config.provider;

import com.rxinyun.dev.api.MemberConfigApi;
import com.rxinyun.member.tools.modular.config.service.MemberConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberConfigApiProvider implements MemberConfigApi {
   @Resource
   private MemberConfigService memberConfigService;

   public String getValueByKey(String key) {
      return this.memberConfigService.getValueByKey(key);
   }
}
