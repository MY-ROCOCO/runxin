package com.rxinyun.admin.dev.modular.sse.service;

import com.rxinyun.common.sse.CommonSseParam;
import java.util.function.Consumer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface DevSseEmitterService {
   SseEmitter createSseConnect(String clientId, Boolean setHeartBeat, Boolean defaultHeartbeat, Consumer<CommonSseParam> consumer);

   void closeSseConnect(String clientId);

   void sendMessageToAllClient(String msg);

   void sendMessageToOneClient(String clientId, String msg);
}
