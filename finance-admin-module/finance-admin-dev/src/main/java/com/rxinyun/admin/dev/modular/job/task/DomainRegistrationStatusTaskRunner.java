package com.rxinyun.admin.dev.modular.job.task;

import com.rxinyun.business.api.DomainTaskApi;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.production.api.DomainCreateStatusApi;
import com.rxinyun.production.api.dto.DomainCreateStatusVO;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DomainRegistrationStatusTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DomainRegistrationStatusTaskRunner.class);
   @Resource
   private DomainTaskApi domainTaskApi;
   @Resource
   private DomainCreateStatusApi domainCreateStatusApi;

   public void action() {
      log.info("域名注册状态检测定时任务");

      try {
         List<String> domainNames = this.domainTaskApi.listCreateWaitingDomainNames();
         log.info("域名注册状态检测: 注册中域名数量={}", domainNames.size());

         for (String domainName : domainNames) {
            try {
               DomainCreateStatusVO vo = this.domainCreateStatusApi.getCreateStatus(domainName);
               if (vo.getStatus() != null) {
                  this.domainTaskApi.updateRegistrationStatus(domainName, vo.getStatus());
               }
            } catch (Exception var5) {
               log.warn("域名注册状态同步失败 domainName={} err={}", domainName, var5.getMessage());
            }
         }
      } catch (Exception var6) {
         log.error("域名注册状态检测异常", var6);
      }
   }
}
