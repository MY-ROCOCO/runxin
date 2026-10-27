package com.rxinyun.core.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

@Configuration
public class EventConfig {
   @Autowired
   ApplicationContext applicationContext;

   public EventConfig() {
   }

   @Bean(
      name = {"applicationEventMulticaster"}
   )
   public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
      SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
      eventMulticaster.setTaskExecutor((Executor)this.applicationContext.getBean("taskExecutor"));
      return eventMulticaster;
   }
}
