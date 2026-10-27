package com.rxinyun.admin.dev.modular.config.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.admin.dev.modular.config.service.DevConfigService;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.core.pojo.BaseDevConfig;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DevConfigApiProvider implements DevConfigApi {
   @Resource
   private DevConfigService devConfigService;

   public String getValueByKey(String key) {
      return this.devConfigService.getValueByKey(key);
   }

   public List<BaseDevConfig> sysBaseList() {
      return this.devConfigService
         .sysBaseList()
         .stream()
         .map(devConfig -> (BaseDevConfig)BeanUtil.copyProperties(devConfig, BaseDevConfig.class, new String[0]))
         .toList();
   }
}
