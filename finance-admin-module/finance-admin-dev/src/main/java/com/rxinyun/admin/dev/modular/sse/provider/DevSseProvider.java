package com.rxinyun.admin.dev.modular.sse.provider;

import com.rxinyun.admin.dev.modular.sse.service.DevSseEmitterService;
import com.rxinyun.common.sse.CommonSseParam;
import com.rxinyun.dev.api.DevSseApi;
import jakarta.annotation.Resource;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class DevSseProvider implements DevSseApi {
   @Resource
   private DevSseEmitterService devSseEmitterService;

   public SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer) {
      return this.devSseEmitterService.createSseConnect(clientId, setHeartBeat, defaultHeartbeat, consumer);
   }

   public void closeSseConnect(String clientId) {
      this.devSseEmitterService.closeSseConnect(clientId);
   }

   public void sendMessageToAllClient(String msg) {
      this.devSseEmitterService.sendMessageToAllClient(msg);
   }

   public void sendMessageToOneClient(String clientId, String msg) {
      this.devSseEmitterService.sendMessageToOneClient(clientId, msg);
   }
}
