package com.rxinyun.admin.dev.modular.sse.util;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.admin.dev.modular.sse.enums.DevSseEmitterParameterEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

public class DevSseCacheUtil {
   private static final Logger log = LoggerFactory.getLogger(DevSseCacheUtil.class);
   public static Map<String, Map<String, Object>> sseCache = new ConcurrentHashMap<>();

   public static SseEmitter getSseEmitterByClientId(String clientId) {
      if (StrUtil.isBlank(clientId)) {
         return null;
      } else {
         Map<String, Object> map = sseCache.get(clientId);
         return map != null && !map.isEmpty() ? (SseEmitter)map.get(DevSseEmitterParameterEnum.EMITTER.getValue()) : null;
      }
   }

   public static ScheduledFuture<?> getSseFutureByClientId(String clientId) {
      if (StrUtil.isBlank(clientId)) {
         return null;
      } else {
         Map<String, Object> map = sseCache.get(clientId);
         return map != null && !map.isEmpty() ? (ScheduledFuture)map.get(DevSseEmitterParameterEnum.FUTURE.getValue()) : null;
      }
   }

   public static ScheduledFuture<?> getLoginIdByClientId(String clientId) {
      if (StrUtil.isBlank(clientId)) {
         return null;
      } else {
         Map<String, Object> map = sseCache.get(clientId);
         return map != null && !map.isEmpty() ? (ScheduledFuture)map.get(DevSseEmitterParameterEnum.LOGINID.getValue()) : null;
      }
   }

   public static String getClientIdByLoginId(String loginId) {
      if (existSseCache()) {
         for (Entry<String, Map<String, Object>> entry : sseCache.entrySet()) {
            Map<String, Object> map = sseCache.get(entry.getKey());
            String lId = (String)map.get(DevSseEmitterParameterEnum.LOGINID.getValue());
            if (loginId.equals(lId)) {
               return entry.getKey();
            }
         }
      }

      return null;
   }

   public static boolean existSseCache() {
      return sseCache.size() > 0;
   }

   public static boolean connectionValidity(String clientId, String loginId) {
      if (StrUtil.isBlank(clientId)) {
         return false;
      } else {
         return sseCache.get(clientId) == null ? false : Objects.equals(loginId, sseCache.get(clientId).get(DevSseEmitterParameterEnum.LOGINID.getValue()));
      }
   }

   public static void addConnection(String clientId, String loginId, SseEmitter emitter, ScheduledFuture<?> future) {
      if (StrUtil.isBlank(clientId)) {
         throw new CommonException("客户端ID不能为空", new Object[0]);
      } else {
         SseEmitter oldEmitter = getSseEmitterByClientId(clientId);
         if (oldEmitter != null) {
            throw new CommonException("连接已存在:{}", new Object[]{clientId});
         } else {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put(DevSseEmitterParameterEnum.EMITTER.getValue(), emitter);
            if (future != null) {
               map.put(DevSseEmitterParameterEnum.FUTURE.getValue(), future);
            }

            map.put(DevSseEmitterParameterEnum.LOGINID.getValue(), loginId);
            sseCache.put(clientId, map);
         }
      }
   }

   public static void removeConnection(String clientId) {
      if (!StrUtil.isBlank(clientId)) {
         SseEmitter emitter = getSseEmitterByClientId(clientId);
         if (emitter != null) {
            cancelScheduledFuture(clientId);
         }

         sseCache.remove(clientId);
         log.info("移除连接:{}", clientId);
      }
   }

   public static void cancelScheduledFuture(String clientId) {
      ScheduledFuture<?> future = getSseFutureByClientId(clientId);
      if (future != null) {
         future.cancel(true);
      }
   }

   public static Runnable completionCallBack(String clientId) {
      return () -> {
         log.info("结束连接:{}", clientId);
         removeConnection(clientId);
         cancelScheduledFuture(clientId);
      };
   }

   public static Runnable timeoutCallBack(String clientId) {
      return () -> {
         log.info("连接超时:{}", clientId);
         removeConnection(clientId);
         cancelScheduledFuture(clientId);
      };
   }

   public static Consumer<Throwable> errorCallBack(String clientId) {
      return throwable -> {
         log.info("推送消息异常:{}", clientId);
         removeConnection(clientId);
         cancelScheduledFuture(clientId);
      };
   }

   public static void sendMessageToAllClient(String msg) {
      if (existSseCache()) {
         if (StrUtil.isEmpty(msg)) {
            log.info("群发消息为空");
         } else {
            CommonResult<String> message = new CommonResult(200, "", msg);

            for (Entry<String, Map<String, Object>> entry : sseCache.entrySet()) {
               sendMessageToClientByClientId(entry.getKey(), message);
            }
         }
      }
   }

   public static void sendMessageToOneClient(String clientId, String msg) {
      if (StrUtil.isEmpty(clientId)) {
         log.info("客户端ID为空");
      } else if (StrUtil.isEmpty(msg)) {
         log.info("向客户端{}推送消息为空", clientId);
      } else {
         CommonResult<String> message = new CommonResult(200, "", msg);
         sendMessageToClientByClientId(clientId, message);
      }
   }

   public static void sendMessageToClientByClientId(String clientId, CommonResult<String> message) {
      Map<String, Object> map = sseCache.get(clientId);
      if (map != null && map.size() != 0) {
         SseEventBuilder sendData = SseEmitter.event().data(message, MediaType.APPLICATION_JSON);
         SseEmitter sseEmitter = getSseEmitterByClientId(clientId);

         try {
            Objects.requireNonNull(sseEmitter).send(sendData);
         } catch (Exception var6) {
            log.error("推送消息失败,报错异常:", var6);
            removeConnection(clientId);
         }
      } else {
         log.error("推送消息失败:客户端{}未创建长链接,失败消息:{}", clientId, message.toString());
      }
   }
}
