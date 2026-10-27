package com.rxinyun.admin.dev.modular.message.provider;

import com.rxinyun.admin.dev.modular.message.service.ToolsNotificationService;
import com.rxinyun.dev.api.ToolsNotificationApi;
import jakarta.annotation.Resource;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ToolsNotificationProvider implements ToolsNotificationApi {
   @Resource
   private ToolsNotificationService toolsNotificationService;

   public void sendMemberNotificationBoth(String notificationType, Long userId, Map<String, Object> templateParams) {
      this.toolsNotificationService.sendMemberNotificationBoth(notificationType, userId, templateParams);
   }

   public void sendAdminNotificationBoth(String notificationId, Map<String, Object> templateParams) {
      this.toolsNotificationService.sendAdminNotificationBoth(notificationId, templateParams);
   }

   public void sendNotificationSms(String notificationType, String phone, Map<String, Object> map) {
      this.toolsNotificationService.sendNotificationSms(notificationType, phone, map);
   }

   public void sendNotificationTelegram(String notificationType, String chatId, Map<String, Object> templateParams) {
      this.toolsNotificationService.sendNotificationTelegram(notificationType, chatId, templateParams);
   }

   public void sendNotificationEmail(String notificationType, String email, Map<String, Object> templateParams) {
      this.toolsNotificationService.sendNotificationEmail(notificationType, email, templateParams);
   }
}
