package com.rxinyun.dev.api;

import java.util.Map;

public interface ToolsNotificationApi {
   void sendMemberNotificationBoth(String notificationType, Long userId, Map<String, Object> templateParams);

   void sendAdminNotificationBoth(String notificationType, Map<String, Object> templateParams);

   void sendNotificationSms(String notificationType, String phone, Map<String, Object> map);

   void sendNotificationTelegram(String notificationType, String chatId, Map<String, Object> templateParams);

   void sendNotificationEmail(String notificationType, String email, Map<String, Object> templateParams);
}
