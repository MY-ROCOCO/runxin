package com.rxinyun.admin.dev.modular.job.task;

import com.rxinyun.admin.dev.modular.log.util.LogUtil;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.OperateTypeEnum;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.ToolsNotificationApi;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IndependentDeleteTimeRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(IndependentDeleteTimeRunner.class);
   @Resource
   private IndependentApi independentApi;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;
   @Autowired
   private CommonCacheOperator commonCacheOperator;
   private static final String REDIS_KEY_PREFIX = "independent:delete:notified:";

   private long getExpireSeconds() {
      LocalDateTime now = TimeUtil.now();
      LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
      long seconds = Duration.between(now, endOfDay).getSeconds();
      return Math.max(seconds, 60L);
   }

   public void action() {
      log.info("独立服务器删除定时任务");
      List<BaseIndependent> serversToCheck = this.independentApi.getServersToDeleteCheck();
      log.info("独立服务器删除定时任务: 待检查数量={}", serversToCheck.size());

      for (BaseIndependent server : serversToCheck) {
         String redisKey = "independent:delete:notified:" + server.getId();
         Object hasNotified = this.commonCacheOperator.get(redisKey);
         if (hasNotified != null) {
            log.debug("独立服务器 {} 今日已执行过删除操作，跳过本次处理", server.getId());
         } else {
            try {
               this.independentApi.deleteServer(server.getId(), OperateTypeEnum.EXPIRE_DELETE.getCode());
               LogUtil.executeSystemOperationLog("独立服务器到期删除", this.getClass(), "action", server, "{\"msg\":\"定时任务已执行独立服务器到期删除\"}");
            } catch (Exception var9) {
               LogUtil.executeSystemExceptionLog("独立服务器到期删除", this.getClass(), "action", server, var9);
               throw var9;
            }

            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("serverName", server.getServerName());
            templateParams.put("serverId", server.getId());
            this.toolsNotificationApi
               .sendMemberNotificationBoth(NotificationTypeEnum.MEMBER_DEDICATED_SERVER_DELETED.getCode(), server.getMemberNo(), templateParams);
            log.info("独立服务器 {} 已删除并发送通知", server.getId());
            long expireSeconds = this.getExpireSeconds();
            this.commonCacheOperator.put(redisKey, TimeUtil.format(TimeUtil.now()), expireSeconds);
            log.info("独立服务器 {} 删除操作已记录，Redis缓存key={}, 过期时间={}秒", new Object[]{server.getId(), redisKey, expireSeconds});
         }
      }
   }
}
