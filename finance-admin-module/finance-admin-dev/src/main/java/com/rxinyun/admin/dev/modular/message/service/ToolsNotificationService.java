package com.rxinyun.admin.dev.modular.message.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.dev.modular.message.entity.ToolsNotification;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationBindTemplateParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationPageParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationStatusParam;
import java.util.List;
import java.util.Map;

public interface ToolsNotificationService extends IService<ToolsNotification> {
   Page<ToolsNotification> page(ToolsNotificationPageParam toolsNotificationPageParam);

   ToolsNotification detail(ToolsNotificationIdParam toolsNotificationIdParam);

   ToolsNotification queryEntity(String id);

   void delete(List<ToolsNotificationIdParam> toolsNotificationIdParamList);

   void wxTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void wxSubscribeTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void smsTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void dingTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void wxWorkTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void emailTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void telegramTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void status(ToolsNotificationStatusParam toolsNotificationStatusParam);

   void bindSmsTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void bindWxTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void bindWxSubscribeTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void bindDingTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void bindWxWorkTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void bindEmailTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void bindTelegramTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam);

   void add(ToolsNotificationAddParam toolsNotificationAddParam);

   void edit(ToolsNotificationEditParam toolsNotificationEditParam);

   void sendNotificationSms(String notificationId, String phoneNumbers, Map<String, Object> templateParams);

   void sendNotificationEmail(String notificationId, String emailAddresses, Map<String, Object> templateParams);

   void sendNotificationTelegram(String notificationId, String chatId, Map<String, Object> templateParams);

   void sendMemberNotificationBoth(String notificationType, Long userId, Map<String, Object> templateParams);

   void sendAdminNotificationBoth(String notificationType, Map<String, Object> templateParams);

   void runSmsInNewTx(String notificationType, String phone, Map<String, Object> templateParams);

   void runEmailInNewTx(String notificationType, String email, Map<String, Object> templateParams);

   void runTelegramInNewTx(String notificationType, String chatId, Map<String, Object> templateParams);

   Map<String, String> getSceneParamDescription(String notificationId);

   ToolsNotification selectByType(String notificationType);
}
