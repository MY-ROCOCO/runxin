package com.rxinyun.admin.dev.modular.job.task;

import cn.hutool.extra.spring.SpringUtil;
import com.rxinyun.admin.dev.modular.log.util.LogUtil;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.ToolsNotificationApi;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerEndTimeTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(ServerEndTimeTaskRunner.class);
   @Resource
   private ServerApi serverApi;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;
   @Autowired
   private CommonCacheOperator commonCacheOperator;
   private static final String REDIS_KEY_PREFIX = "server:expiry:notified:";
   private static final ZoneId BEIJING_ZONE = ZoneId.of("Asia/Shanghai");
   private static final DateTimeFormatter BEIJING_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   private long getExpireSeconds() {
      LocalDateTime now = TimeUtil.now();
      LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
      long seconds = Duration.between(now, endOfDay).getSeconds();
      return Math.max(seconds, 60L);
   }

   public void action() {
      log.info("服务器到期定时任务");
      LocalDateTime now = TimeUtil.now();
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String expireNotifyDaysStr = devConfigApi.getValueByKey("EXPIRE_NOTIFY_DAYS");
      if (expireNotifyDaysStr == null || expireNotifyDaysStr.isBlank()) {
         expireNotifyDaysStr = "7";
      }

      long expireNotifyDays = Long.parseLong(expireNotifyDaysStr.trim());
      LocalDateTime sevenDaysLater = now.plusDays(expireNotifyDays);
      List<BaseServer> serversToCheck = this.serverApi.getServersToCheck(sevenDaysLater);
      log.info("服务器到期定时任务: 待检查数量={}, 通知天数={}", serversToCheck.size(), expireNotifyDays);

      for (BaseServer server : serversToCheck) {
         Date expiryDate = server.getEndTime();
         LocalDateTime expiryLocalDateTime = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
         long daysUntilExpiry = ChronoUnit.DAYS.between(now.toLocalDate(), expiryLocalDateTime.toLocalDate());
         if (now.isAfter(expiryLocalDateTime)) {
            log.info("服务器 {} 已经到期，更改状态", server.getId());

            try {
               this.serverApi.updateServerStatus(server.getId(), "到期自动关机");
               LogUtil.executeSystemOperationLog("云服务器到期关机", this.getClass(), "action", server, "{\"msg\":\"定时任务已执行云服务器到期关机\"}");
            } catch (Exception var20) {
               LogUtil.executeSystemExceptionLog("云服务器到期关机", this.getClass(), "action", server, var20);
               throw var20;
            }
         } else if (daysUntilExpiry == expireNotifyDays) {
            String redisKey = "server:expiry:notified:" + server.getId();
            Object hasNotified = this.commonCacheOperator.get(redisKey);
            if (hasNotified != null) {
               log.debug("服务器 {} 今日已发送过到期提醒，跳过本次通知", server.getId());
            } else {
               String expiryDateBeijing = expiryDate.toInstant().atZone(BEIJING_ZONE).format(BEIJING_TIME_FORMATTER);
               Map<String, Object> templateParams = new HashMap<>();
               templateParams.put("serverName", server.getHostName());
               templateParams.put("expiryDate", expiryDateBeijing);
               templateParams.put("daysUntilExpiry", daysUntilExpiry);
               templateParams.put("serverId", server.getId());
               templateParams.put("serverStatus", server.getServerStatus());
               this.toolsNotificationApi
                  .sendMemberNotificationBoth(NotificationTypeEnum.MEMBER_CLOUD_SERVER_EXPIRING.getCode(), Long.valueOf(server.getMemberNo()), templateParams);
               log.info("服务器 {} 将在 {} 天后到期，发送提醒通知", server.getId(), daysUntilExpiry);
               long expireSeconds = this.getExpireSeconds();
               this.commonCacheOperator.put(redisKey, TimeUtil.format(TimeUtil.now()), expireSeconds);
               log.info("服务器 {} 到期提醒通知已发送，Redis缓存key={}, 过期时间={}秒", new Object[]{server.getId(), redisKey, expireSeconds});
            }
         }
      }
   }
}
