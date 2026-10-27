package com.rxinyun.admin.dev.modular.api.task;

import com.rxinyun.admin.dev.modular.api.handler.ResourcePoolRequestHandler;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ResourcePoolRequestTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolRequestTaskRunner.class);
   @Resource
   private ResourcePoolRequestHandler resourcePoolRequestHandler;

   public void action() {
      try {
         log.info("开始执行资源池请求定时任务");
         this.resourcePoolRequestHandler.scanAndSendPendingRequests();
         log.info("资源池请求定时任务执行完成");
      } catch (Exception var2) {
         log.error("资源池请求定时任务执行失败", var2);
      }
   }
}
