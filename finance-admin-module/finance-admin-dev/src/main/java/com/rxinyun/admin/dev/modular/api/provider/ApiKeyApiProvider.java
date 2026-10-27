package com.rxinyun.admin.dev.modular.api.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.admin.dev.modular.api.entity.ApiKey;
import com.rxinyun.admin.dev.modular.api.service.ApiKeyService;
import com.rxinyun.dev.api.ApiKeyApi;
import com.rxinyun.dev.core.pojo.BaseApiKey;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyApiProvider implements ApiKeyApi {
   @Resource
   private ApiKeyService apiKeyService;

   public BaseApiKey getByApiKey(String apiKey) {
      ApiKey entity = this.apiKeyService.getByApiKey(apiKey);
      return entity == null ? null : (BaseApiKey)BeanUtil.toBean(entity, BaseApiKey.class);
   }
}
