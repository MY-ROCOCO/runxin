package com.rxinyun.common.controller;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudControllerRouter {
   private static final Logger log = LoggerFactory.getLogger(CloudControllerRouter.class);
   @Resource
   private List<CloudControllerAdapter> adapters;
   private final Map<String, CloudControllerAdapter> adapterMap = new HashMap<>();

   @PostConstruct
   public void init() {
      if (this.adapters != null) {
         for (CloudControllerAdapter adapter : this.adapters) {
            this.adapterMap.put(adapter.getGuardVersion(), adapter);
         }
      }
   }

   public CloudControllerAdapter getAdapterByGuardVersion(String guardVersion) {
      if (StrUtil.isBlank(guardVersion)) {
         throw new CommonException("受控版本不支持：{}", guardVersion);
      } else {
         String key = StrUtil.trim(guardVersion);
         CloudControllerAdapter adapter = this.adapterMap.get(key);
         if (adapter == null) {
            log.warn("版本路由失败: guardVersion={}, 未找到适配器", key);
            throw new CommonException("受控版本不支持：{}", key);
         } else {
            log.info("版本路由成功: guardVersion={}, adapter={}", key, adapter.getClass().getName());
            return adapter;
         }
      }
   }
}
