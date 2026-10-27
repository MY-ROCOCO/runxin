package com.rxinyun.member.business.modular.independent.async;

import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.OperateTypeEnum;
import com.rxinyun.dev.api.ToolsNotificationApi;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("memberDedicatedServerManualWorkNotificationPublisher")
public class DedicatedServerManualWorkNotificationPublisher {
   private static final Logger log = LoggerFactory.getLogger(DedicatedServerManualWorkNotificationPublisher.class);
   private final ToolsNotificationApi toolsNotificationApi;

   @Async("notificationTaskExecutor")
   public void publishAfterWorkCreated(String independentId, String independentIp, Integer operateType) {
      try {
         Map<String, Object> templateParams = new HashMap<>(4);
         templateParams.put("independentId", independentId);
         templateParams.put("independentIp", independentIp);
         templateParams.put("operateType", OperateTypeEnum.getDescription(operateType));
         this.toolsNotificationApi.sendAdminNotificationBoth(NotificationTypeEnum.ADMIN_DEDICATED_SERVER_MANUAL.getCode(), templateParams);
      } catch (Throwable var5) {
         log.error("异步发送独立服务器人工工单管理员通知失败 independentId={}", independentId, var5);
      }
   }

   public DedicatedServerManualWorkNotificationPublisher(final ToolsNotificationApi toolsNotificationApi) {
      this.toolsNotificationApi = toolsNotificationApi;
   }
}
