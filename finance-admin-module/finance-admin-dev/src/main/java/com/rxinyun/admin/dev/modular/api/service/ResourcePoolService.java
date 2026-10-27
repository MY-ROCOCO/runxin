package com.rxinyun.admin.dev.modular.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolAddParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolEditParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolIdParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolPageParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolStatusParam;
import java.util.List;

public interface ResourcePoolService {
   Page<ResourcePool> page(ResourcePoolPageParam resourcePoolPageParam);

   void add(ResourcePoolAddParam resourcePoolAddParam);

   void edit(ResourcePoolEditParam resourcePoolEditParam);

   void delete(List<ResourcePoolIdParam> resourcePoolIdParamList);

   ResourcePool detail(ResourcePoolIdParam resourcePoolIdParam);

   void changeStatus(ResourcePoolStatusParam resourcePoolStatusParam);

   ResourcePool getByPoolType(String poolType);

   ResourcePool getByIdForApi(String resourcePoolId);

   List<ResourcePool> listEnabled();

   boolean validateResourcePoolApiCredentials(String apiKey, String apiSecretPlain);
}
