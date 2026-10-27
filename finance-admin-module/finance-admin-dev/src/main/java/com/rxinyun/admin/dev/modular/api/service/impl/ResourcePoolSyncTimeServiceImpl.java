package com.rxinyun.admin.dev.modular.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolSyncTimeService;
import com.rxinyun.common.util.TimeUtil;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResourcePoolSyncTimeServiceImpl implements ResourcePoolSyncTimeService {
   private static final Logger log = LoggerFactory.getLogger(ResourcePoolSyncTimeServiceImpl.class);
   private static final String REDIS_KEY_PREFIX = "resource_pool:sync_time:";
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   @Resource
   private ObjectMapper objectMapper;

   @Override
   public LocalDateTime getLastSyncTime(String resourcePoolId) {
      if (StrUtil.isBlank(resourcePoolId)) {
         log.warn("资源池 ID 为空，返回默认同步时间");
         return TimeUtil.now().minusHours(1L);
      } else {
         String redisKey = "resource_pool:sync_time:" + resourcePoolId;
         Object value = this.redisTemplate.opsForValue().get(redisKey);
         if (value == null) {
            log.debug("资源池{}未找到同步时间记录，返回默认时间", resourcePoolId);
            return TimeUtil.now().minusHours(1L);
         } else {
            try {
               if (value instanceof LocalDateTime syncTime) {
                  log.debug("资源池{}的最后同步时间：{}", resourcePoolId, syncTime);
                  return syncTime;
               } else {
                  if (value instanceof String timeStr) {
                     try {
                        LocalDateTime syncTime = (LocalDateTime)this.objectMapper.readValue(timeStr, LocalDateTime.class);
                        log.debug("资源池{}的最后同步时间（JSON 反序列化）：{}", resourcePoolId, syncTime);
                        return syncTime;
                     } catch (Exception var8) {
                        try {
                           LocalDateTime syncTimex = LocalDateTime.parse(timeStr, DATE_TIME_FORMATTER);
                           log.debug("资源池{}的最后同步时间（字符串解析）：{}", resourcePoolId, syncTimex);
                           return syncTimex;
                        } catch (Exception var7) {
                           log.warn("资源池{}的时间字符串格式无法解析：{}", resourcePoolId, timeStr);
                        }
                     }
                  } else {
                     log.warn("资源池{}的同步时间为未知类型：{}", resourcePoolId, value.getClass().getName());
                  }

                  return TimeUtil.now().minusHours(1L);
               }
            } catch (Exception var9) {
               log.error("从 Redis 读取同步时间处理失败，资源池 ID: {}", resourcePoolId, var9);
               return TimeUtil.now().minusHours(1L);
            }
         }
      }
   }

   @Override
   public void updateLastSyncTime(String resourcePoolId, LocalDateTime syncTime) {
      if (StrUtil.isBlank(resourcePoolId)) {
         log.warn("资源池 ID 为空，不更新同步时间");
      } else if (syncTime == null) {
         log.warn("同步时间为空，不更新");
      } else {
         String redisKey = "resource_pool:sync_time:" + resourcePoolId;
         this.redisTemplate.opsForValue().set(redisKey, syncTime);
         this.redisTemplate.expire(redisKey, 7L, TimeUnit.DAYS);
         log.debug("已更新资源池{}的最后同步时间为：{}", resourcePoolId, syncTime);
      }
   }
}
