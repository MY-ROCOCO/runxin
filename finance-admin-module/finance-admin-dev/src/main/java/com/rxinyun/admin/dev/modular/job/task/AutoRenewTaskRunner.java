package com.rxinyun.admin.dev.modular.job.task;

import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.order.api.AutoRenewApi;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AutoRenewTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(AutoRenewTaskRunner.class);
   @Resource
   private AutoRenewApi autoRenewApi;

   public void action() {
      log.info("云服务器/独立服务器自动续费定时任务");
      this.autoRenewApi.execute();
   }
}
