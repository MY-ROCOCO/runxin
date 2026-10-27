package com.rxinyun.core.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class ThreadPoolConfig {
   public ThreadPoolConfig() {
   }

   @Bean({"taskExecutor"})
   public Executor asyncServiceExecutor() {
      int processors = Runtime.getRuntime().availableProcessors();
      int corePoolSize = Math.max(8, processors * 2);
      int maxPoolSize = Math.max(corePoolSize * 2, 16);
      return this.buildExecutor("runxin-async-", corePoolSize, maxPoolSize, 2000);
   }

   @Bean({"orderTaskExecutor"})
   public Executor orderTaskExecutor() {
      int processors = Runtime.getRuntime().availableProcessors();
      int corePoolSize = Math.max(4, processors);
      int maxPoolSize = Math.max(corePoolSize * 2, 8);
      return this.buildExecutor("runxin-order-", corePoolSize, maxPoolSize, 1000);
   }

   @Bean({"refundTaskExecutor"})
   public Executor refundTaskExecutor() {
      int processors = Runtime.getRuntime().availableProcessors();
      int corePoolSize = Math.max(2, processors / 2);
      int maxPoolSize = Math.max(corePoolSize * 2, 4);
      return this.buildExecutor("runxin-refund-", corePoolSize, maxPoolSize, 500);
   }

   @Bean({"notificationTaskExecutor"})
   public Executor notificationTaskExecutor() {
      int processors = Runtime.getRuntime().availableProcessors();
      int corePoolSize = Math.max(2, processors / 2);
      int maxPoolSize = Math.max(corePoolSize * 2, 4);
      return this.buildExecutor("runxin-notify-", corePoolSize, maxPoolSize, 1500);
   }

   @Bean({"resourcePoolTaskExecutor"})
   public Executor resourcePoolTaskExecutor() {
      int processors = Runtime.getRuntime().availableProcessors();
      int corePoolSize = Math.max(4, processors);
      int maxPoolSize = Math.max(corePoolSize * 2, 8);
      return this.buildExecutor("runxin-resource-", corePoolSize, maxPoolSize, 1000);
   }

   private Executor buildExecutor(String threadNamePrefix, int corePoolSize, int maxPoolSize, int queueCapacity) {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setCorePoolSize(corePoolSize);
      executor.setMaxPoolSize(maxPoolSize);
      executor.setQueueCapacity(queueCapacity);
      executor.setKeepAliveSeconds(120);
      executor.setThreadNamePrefix(threadNamePrefix);
      executor.setWaitForTasksToCompleteOnShutdown(true);
      executor.setAwaitTerminationSeconds(60);
      executor.setRejectedExecutionHandler(new CallerRunsPolicy());
      executor.initialize();
      return executor;
   }
}
