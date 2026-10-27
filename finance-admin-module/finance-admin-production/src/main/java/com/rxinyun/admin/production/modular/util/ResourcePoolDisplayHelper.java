package com.rxinyun.admin.production.modular.util;

import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import java.util.function.Consumer;

public final class ResourcePoolDisplayHelper {
   private ResourcePoolDisplayHelper() {
   }

   public static void ifPresent(ResourcePoolApi resourcePoolApi, String resourcePoolId, Consumer<BaseResourcePool> poolConsumer) {
      if (!ObjectUtil.isEmpty(resourcePoolId)) {
         BaseResourcePool pool = resourcePoolApi.getById(resourcePoolId);
         if (ObjectUtil.isNotEmpty(pool)) {
            poolConsumer.accept(pool);
         }
      }
   }
}
