package com.rxinyun.admin.dev.modular.job.task;

import com.rxinyun.business.api.DomainTaskApi;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DomainExpiryTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DomainExpiryTaskRunner.class);
   @Resource
   private DomainTaskApi domainTaskApi;

   public void action() {
      log.info("域名到期检测定时任务");

      try {
         this.domainTaskApi.markExpiredDomains();
      } catch (Exception var2) {
         log.error("域名到期检测异常", var2);
      }
   }
}
