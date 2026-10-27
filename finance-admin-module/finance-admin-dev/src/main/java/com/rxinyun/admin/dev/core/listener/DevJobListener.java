package com.rxinyun.admin.dev.core.listener;

import cn.hutool.cron.CronUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.dev.modular.job.entity.DevJob;
import com.rxinyun.admin.dev.modular.job.enums.DevJobStatusEnum;
import com.rxinyun.admin.dev.modular.job.service.DevJobService;
import com.rxinyun.admin.dev.modular.job.support.DevJobTaskDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;

@Configuration
public class DevJobListener implements ApplicationListener<ApplicationStartedEvent>, Ordered {
   private static final Logger log = LoggerFactory.getLogger(DevJobListener.class);

   public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
      DevJobTaskDispatcher dispatcher = (DevJobTaskDispatcher)SpringUtil.getBean(DevJobTaskDispatcher.class);
      ((DevJobService)SpringUtil.getBean(DevJobService.class))
         .list(
            (Wrapper<DevJob>)((LambdaQueryWrapper<DevJob>)new LambdaQueryWrapper<DevJob>().eq(DevJob::getJobStatus, DevJobStatusEnum.RUNNING.getValue()))
               .orderByAsc(DevJob::getSortCode)
         )
         .forEach(devJob -> CronUtil.schedule(devJob.getId(), devJob.getCronExpression(), dispatcher.wrap(devJob)));
      CronUtil.setMatchSecond(true);
      CronUtil.restart();
   }

   @EventListener({ContextClosedEvent.class})
   public void onContextClosed(ContextClosedEvent event) {
      CronUtil.stop();
      log.info("定时任务调度器已停止");
   }

   public int getOrder() {
      return Integer.MAX_VALUE;
   }
}
