package com.rxinyun.admin.dev.modular.sse.controller;

import com.rxinyun.admin.dev.modular.sse.service.DevSseEmitterService;
import com.rxinyun.common.sse.CommonSseParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.function.Consumer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(
   name = "SSE通信控制器"
)
@RestController
@Validated
public class DevSseEmitterController {
   @Resource
   private DevSseEmitterService devSseEmitterService;

   @Operation(
      summary = "创建sse连接"
   )
   @GetMapping({"/admin/dev/sse/createConnect"})
   public SseEmitter createConnect(
      String clientId,
      @RequestParam(required = false) Boolean setHeartBeat,
      @RequestParam(required = false) Boolean defaultHeartbeat,
      @RequestParam(required = false) Consumer<CommonSseParam> consumer
   ) {
      return this.devSseEmitterService.createSseConnect(clientId, setHeartBeat, defaultHeartbeat, consumer);
   }

   @Operation(
      summary = "关闭sse连接"
   )
   @GetMapping({"/admin/dev/sse/closeSseConnect"})
   public void closeSseConnect(String clientId) {
      this.devSseEmitterService.closeSseConnect(clientId);
   }

   @Operation(
      summary = "推送消息到所有客户端"
   )
   @PostMapping({"/admin/dev/sse/broadcast"})
   public void sendMessageToAllClient(@RequestBody(required = false) String msg) {
      this.devSseEmitterService.sendMessageToAllClient(msg);
   }

   @Operation(
      summary = "根据clientId发送消息给某一客户端"
   )
   @PostMapping({"/admin/dev/sse/sendMessage"})
   public void sendMessageToOneClient(String clientId, String msg) {
      this.devSseEmitterService.sendMessageToOneClient(clientId, msg);
   }
}
