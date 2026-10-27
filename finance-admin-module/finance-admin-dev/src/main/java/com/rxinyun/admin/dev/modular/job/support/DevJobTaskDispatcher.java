package com.rxinyun.admin.dev.modular.job.support;

import cn.hutool.cron.task.Task;
import cn.hutool.extra.spring.SpringUtil;
import com.rxinyun.admin.dev.modular.job.entity.DevJob;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class DevJobTaskDispatcher {
   private static final Logger log = LoggerFactory.getLogger(DevJobTaskDispatcher.class);
   private final ConcurrentHashMap<String, AtomicBoolean> runningFlags = new ConcurrentHashMap<>();
   private ThreadPoolTaskExecutor cronJobExecutor;

   @Bean({"cronJobExecutor"})
   public ThreadPoolTaskExecutor cronJobExecutor() {
      int processors = Runtime.getRuntime().availableProcessors();
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(Math.max(16, processors * 2));
      executor.setMaxPoolSize(Math.max(32, processors * 4));
      executor.setQueueCapacity(128);
      executor.setKeepAliveSeconds(120);
      executor.setThreadNamePrefix("runxin-cron-");
      executor.setWaitForTasksToCompleteOnShutdown(true);
      executor.setAwaitTerminationSeconds(30);
      executor.setRejectedExecutionHandler(new AbortPolicy());
      executor.initialize();
      this.cronJobExecutor = executor;
      return executor;
   }

   public Task wrap(DevJob devJob) {
      return () -> this.dispatch(devJob);
   }

   private void dispatch(DevJob devJob) {
      AtomicBoolean running = this.runningFlags.computeIfAbsent(devJob.getId(), id -> new AtomicBoolean(false));
      if (!running.compareAndSet(false, true)) {
         log.debug("定时任务上一轮未结束，跳过本轮: {}", devJob.getName());
      } else {
         try {
            this.cronJobExecutor.execute(() -> {
               try {
                  this.runAction(devJob);
               } finally {
                  running.set(false);
               }
            });
         } catch (RejectedExecutionException var4) {
            running.set(false);
            log.warn("定时任务线程池已满，丢弃本轮: {}", devJob.getName());
         }
      }
   }

   private void runAction(DevJob devJob) {
      try {
         ((CommonTimerTaskRunner)SpringUtil.getBean(Class.forName(devJob.getActionClass()))).action();
      } catch (ClassNotFoundException var3) {
         throw new CommonException("定时任务找不到对应的类，名称为：{}", new Object[]{devJob.getActionClass()});
      } catch (BeanCreationNotAllowedException var4) {
         log.debug("定时任务跳过(应用已关闭): {}", devJob.getName());
      } catch (Exception var5) {
         log.error("定时任务执行异常: {}", devJob.getName(), var5);
      }
   }
}
