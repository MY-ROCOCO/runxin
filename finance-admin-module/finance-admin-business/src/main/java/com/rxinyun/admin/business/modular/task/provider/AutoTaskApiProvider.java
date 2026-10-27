package com.rxinyun.admin.business.modular.task.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.admin.business.modular.task.param.AutoTaskAddParam;
import com.rxinyun.admin.business.modular.task.service.AutoTaskTaskService;
import com.rxinyun.business.api.AutoTaskApi;
import com.rxinyun.business.core.pojo.BaseAutoTask;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AutoTaskApiProvider implements AutoTaskApi {
   private static final Logger log = LoggerFactory.getLogger(AutoTaskApiProvider.class);
   @Resource
   private AutoTaskTaskService autoTaskTaskService;

   public void add(BaseAutoTask baseAutoTask) {
      AutoTaskAddParam autoTask = (AutoTaskAddParam)BeanUtil.toBean(baseAutoTask, AutoTaskAddParam.class);
      this.autoTaskTaskService.add(autoTask);
   }
}
