package com.rxinyun.agent.core.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpLogic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentSaTokenConfigure {
   @Bean({"agentStpLogic"})
   public StpLogic agentStpLogic(SaTokenConfig saTokenConfig) {
      return new StpLogic("A").setConfig(saTokenConfig);
   }
}
