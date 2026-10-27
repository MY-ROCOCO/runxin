package com.rxinyun.admin.dev.modular.job.task;

import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.production.api.DomainTemplateTaskApi;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DomainTemplateStatusTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DomainTemplateStatusTaskRunner.class);
   @Resource
   private DomainTemplateTaskApi domainTemplateTaskApi;

   public void action() {
      log.info("域名信息模板状态检测定时任务");

      try {
         List<String> ids = this.domainTemplateTaskApi.listAuditingTemplateIds();
         log.info("域名信息模板状态检测: 审核中模板数量={}", ids.size());

         for (String templateId : ids) {
            this.domainTemplateTaskApi.syncTemplateStatusByTemplateId(templateId);
         }
      } catch (Exception var4) {
         log.error("域名信息模板状态检测异常", var4);
      }
   }
}
