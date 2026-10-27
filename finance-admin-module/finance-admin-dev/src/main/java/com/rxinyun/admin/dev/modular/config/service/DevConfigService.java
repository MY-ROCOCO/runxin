package com.rxinyun.admin.dev.modular.config.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.config.entity.DevConfig;
import com.rxinyun.admin.dev.modular.config.param.DevConfigAddParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigBatchParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigEditParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigIdParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigKeyEditParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigListParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigPageParam;
import java.util.List;

public interface DevConfigService extends IService<DevConfig> {
   String getValueByKey(String key);

   String getValueDetailByKey(String key);

   Page<DevConfig> page(DevConfigPageParam devConfigPageParam);

   List<DevConfig> sysBaseList();

   List<DevConfig> list(DevConfigListParam devConfigListParam);

   void add(DevConfigAddParam devConfigAddParam);

   void edit(DevConfigEditParam devConfigEditParam);

   void delete(List<DevConfigIdParam> devConfigIdParamList);

   DevConfig detail(DevConfigIdParam devConfigIdParam);

   DevConfig queryEntity(String id);

   DevConfig queryByConfigKey(String configKey);

   void editBatch(List<DevConfigBatchParam> devConfigBatchParamList);

   void updateByConfigKey(DevConfigKeyEditParam devConfigKeyEditParam);
}
