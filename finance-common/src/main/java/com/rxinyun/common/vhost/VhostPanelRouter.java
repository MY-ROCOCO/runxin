package com.rxinyun.common.vhost;

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
public class VhostPanelRouter {
   private static final Logger log = LoggerFactory.getLogger(VhostPanelRouter.class);
   @Resource
   private List<VhostPanelAdapter> adapters;
   private final Map<String, VhostPanelAdapter> adapterMap = new HashMap<>();

   @PostConstruct
   public void init() {
      if (this.adapters != null) {
         for (VhostPanelAdapter adapter : this.adapters) {
            this.adapterMap.put(adapter.controlledVersion(), adapter);
         }
      }
   }

   public VhostPanelAdapter getAdapter(String controlledVersion) {
      String key = StrUtil.trim(controlledVersion);
      VhostPanelAdapter adapter = StrUtil.isBlank(key) ? null : this.adapterMap.get(key);
      if (adapter == null) {
         log.warn("虚拟主机受控版本未接入: {}", key);
         throw new CommonException("受控版本不支持：{}", key);
      } else {
         return adapter;
      }
   }
}
