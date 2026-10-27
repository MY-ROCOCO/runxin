package com.rxinyun.admin.dev.modular.sse.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.admin.dev.modular.sse.service.DevSseEmitterService;
import com.rxinyun.admin.dev.modular.sse.util.DevSseCacheUtil;
import com.rxinyun.common.sse.CommonSseParam;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class DevSseEmitterServiceImpl implements DevSseEmitterService {
   private static final Logger log = LoggerFactory.getLogger(DevSseEmitterServiceImpl.class);
   private static final ScheduledExecutorService heartbeatExecutors = Executors.newScheduledThreadPool(10);

   @Override
   public SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer) {
      SseEmitter sseEmitter = new SseEmitter(0L);
      String loginId = StpUtil.getLoginIdAsString();
      if (StrUtil.isNotBlank(clientId)) {
         if (DevSseCacheUtil.connectionValidity(clientId, loginId)) {
            return DevSseCacheUtil.getSseEmitterByClientId(clientId);
         }

         DevSseCacheUtil.removeConnection(clientId);
      }

      clientId = IdUtil.simpleUUID();
      String finalClientId = clientId;
      String finalLoginId = loginId;
      if (setHeartBeat != null && setHeartBeat) {
         ScheduledFuture<?> future;
         if ((defaultHeartbeat == null || !defaultHeartbeat) && consumer != null) {
            CommonSseParam commonSseParam = new CommonSseParam();
            commonSseParam.setClientId(clientId);
            commonSseParam.setLoginId(loginId);
            future = heartbeatExecutors.scheduleAtFixedRate(() -> consumer.accept(commonSseParam), 2L, 10L, TimeUnit.SECONDS);
         } else {
            future = heartbeatExecutors.scheduleAtFixedRate(
               () -> DevSseCacheUtil.sendMessageToOneClient(finalClientId, finalClientId + "-" + finalLoginId), 2L, 10L, TimeUnit.SECONDS
            );
         }

         DevSseCacheUtil.addConnection(clientId, loginId, sseEmitter, future);
      } else {
         DevSseCacheUtil.addConnection(clientId, loginId, sseEmitter, null);
      }

      return sseEmitter;
   }

   @Override
   public void closeSseConnect(String clientId) {
      DevSseCacheUtil.removeConnection(clientId);
   }

   @Override
   public void sendMessageToAllClient(String msg) {
      DevSseCacheUtil.sendMessageToAllClient(msg);
   }

   @Override
   public void sendMessageToOneClient(String clientId, String msg) {
      DevSseCacheUtil.sendMessageToOneClient(clientId, msg);
   }
}
