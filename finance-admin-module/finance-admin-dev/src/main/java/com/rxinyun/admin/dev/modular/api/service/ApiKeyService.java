package com.rxinyun.admin.dev.modular.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.api.entity.ApiKey;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyAddParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyEditParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyIdParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyPageParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyStatusParam;
import com.rxinyun.admin.dev.modular.api.result.ApiKeyCreateResult;
import java.util.List;

public interface ApiKeyService extends IService<ApiKey> {
   Page<ApiKey> page(ApiKeyPageParam apiKeyPageParam);

   ApiKeyCreateResult add(ApiKeyAddParam apiKeyAddParam);

   void edit(ApiKeyEditParam apiKeyEditParam);

   void delete(List<ApiKeyIdParam> apiKeyIdParamList);

   ApiKey detail(ApiKeyIdParam apiKeyIdParam);

   void changeStatus(ApiKeyStatusParam apiKeyStatusParam);

   ApiKey getByApiKey(String apiKey);
}
