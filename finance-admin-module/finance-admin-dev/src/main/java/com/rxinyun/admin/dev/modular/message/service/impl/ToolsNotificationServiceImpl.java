package com.rxinyun.admin.dev.modular.message.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendParam;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.admin.dev.modular.message.entity.ToolsEmailTemplate;
import com.rxinyun.admin.dev.modular.message.entity.ToolsNotification;
import com.rxinyun.admin.dev.modular.message.entity.ToolsSmsTemplate;
import com.rxinyun.admin.dev.modular.message.entity.ToolsTelegramTemplate;
import com.rxinyun.admin.dev.modular.message.mapper.ToolsNotificationMapper;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationBindTemplateParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationPageParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationStatusParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsEmailTemplateService;
import com.rxinyun.admin.dev.modular.message.service.ToolsNotificationService;
import com.rxinyun.admin.dev.modular.message.service.ToolsSmsTemplateService;
import com.rxinyun.admin.dev.modular.message.service.ToolsTelegramTemplateService;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsSendParam;
import com.rxinyun.admin.dev.modular.sms.service.DevSmsService;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegramBinding;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramSendParam;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramBindingService;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.DevEmailApi;
import com.rxinyun.dev.api.DevSmsApi;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.sys.api.SysUserApi;
import com.rxinyun.sys.core.pojo.BaseSysUser;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ToolsNotificationServiceImpl extends ServiceImpl<ToolsNotificationMapper, ToolsNotification> implements ToolsNotificationService {
   private static final Logger log = LoggerFactory.getLogger(ToolsNotificationServiceImpl.class);
   private static final String TELEGRAM_PARSE_MODE_HTML = "HTML";
   @Resource
   private ToolsNotificationService notificationService;
   @Resource
   private ToolsNotificationMapper toolsNotificationMapper;
   @Resource
   private ToolsSmsTemplateService smsTemplateService;
   @Resource
   private ToolsEmailTemplateService emailTemplateService;
   @Resource
   private ToolsTelegramTemplateService telegramTemplateService;
   @Resource
   private DevSmsService devSmsService;
   @Resource
   private DevEmailService devEmailService;
   @Resource
   private DevTelegramService devTelegramService;
   @Resource
   private DevTelegramBindingService devTelegramBindingService;
   @Resource
   private MemberSaBaseLoginUserApi memberSaBaseLoginUserApi;
   @Resource
   private SysUserApi sysUserApi;
   @Resource
   private DevSmsApi devSmsApi;
   @Resource
   private DevEmailApi devEmailApi;
   @Resource
   private DevConfigApi devConfigApi;

   @Override
   public Page<ToolsNotification> page(ToolsNotificationPageParam toolsNotificationPageParam) {
      QueryWrapper<ToolsNotification> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(toolsNotificationPageParam.getTargetType())) {
         queryWrapper.lambda().like(ToolsNotification::getTargetType, toolsNotificationPageParam.getTargetType());
      }

      if (ObjectUtil.isNotEmpty(toolsNotificationPageParam.getType())) {
         queryWrapper.lambda().like(ToolsNotification::getType, toolsNotificationPageParam.getType());
      }

      if (ObjectUtil.isNotEmpty(toolsNotificationPageParam.getConfigType())) {
         queryWrapper.lambda().eq(ToolsNotification::getConfigType, toolsNotificationPageParam.getConfigType());
      }

      return (Page<ToolsNotification>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public ToolsNotification detail(ToolsNotificationIdParam toolsNotificationIdParam) {
      return this.queryEntity(toolsNotificationIdParam.getId());
   }

   @Override
   public ToolsNotification queryEntity(String id) {
      ToolsNotification toolsNotification = (ToolsNotification)this.getById(id);
      if (ObjectUtil.isEmpty(toolsNotification)) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{id});
      } else {
         return toolsNotification;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ToolsNotificationIdParam> toolsNotificationIdParamList) {
      List<String> toolsNotificationIdList = CollStreamUtil.toList(toolsNotificationIdParamList, ToolsNotificationIdParam::getId);
      if (ObjectUtil.isNotEmpty(toolsNotificationIdList)) {
         this.removeByIds(toolsNotificationIdList);
      }
   }

   @Override
   public void wxTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setWechatStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void wxSubscribeTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setWxSubscribeStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void smsTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setSmsStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void dingTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setDingStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void wxWorkTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setWxWorkStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void emailTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setEmailStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void telegramTemplateSwitch(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setTelegramStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void status(ToolsNotificationStatusParam toolsNotificationStatusParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationStatusParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationStatusParam.getId()});
      } else {
         toolsNotification.setStatus(toolsNotificationStatusParam.getStatus());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindSmsTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setSmsTemplateId(toolsNotificationBindTemplateParam.getSmsTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindWxTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setWechatTemplateId(toolsNotificationBindTemplateParam.getWechatTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindWxSubscribeTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setWxSubscribeTemplateId(toolsNotificationBindTemplateParam.getWxSubscribeTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindDingTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setDingTemplateId(toolsNotificationBindTemplateParam.getDingTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindWxWorkTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setWxWorkTemplateId(toolsNotificationBindTemplateParam.getWxWorkTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindEmailTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setEmailTemplateId(toolsNotificationBindTemplateParam.getEmailTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void bindTelegramTemplate(ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationBindTemplateParam.getId());
      if (null == toolsNotification) {
         throw new CommonException("消息通知场景不存在，id值为：{}", new Object[]{toolsNotificationBindTemplateParam.getId()});
      } else {
         toolsNotification.setTelegramTemplateId(toolsNotificationBindTemplateParam.getTelegramTemplateId());
         this.updateById(toolsNotification);
      }
   }

   @Override
   public void add(ToolsNotificationAddParam toolsNotificationAddParam) {
      ToolsNotification toolsNotification = new ToolsNotification();
      BeanUtil.copyProperties(toolsNotificationAddParam, toolsNotification, new String[0]);
      if (ObjectUtil.isEmpty(toolsNotification.getConfigType())) {
         toolsNotification.setConfigType("NOTICE");
      }

      this.save(toolsNotification);
   }

   @Override
   public void edit(ToolsNotificationEditParam toolsNotificationEditParam) {
      ToolsNotification toolsNotification = this.queryEntity(toolsNotificationEditParam.getId());
      BeanUtil.copyProperties(toolsNotificationEditParam, toolsNotification, new String[0]);
      this.updateById(toolsNotification);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendNotificationSms(String notificationType, String phoneNumbers, Map<String, Object> templateParams) {
      this.sendNotificationSmsInternal(notificationType, phoneNumbers, templateParams);
   }

   private void sendNotificationSmsInternal(String notificationType, String phoneNumbers, Map<String, Object> templateParams) {
      ToolsNotification notification = this.selectByType(notificationType);
      if (ObjectUtil.isEmpty(notification.getSmsStatus()) || notification.getSmsStatus() != 1) {
         throw new CommonException("场景【{}】的短信通知未开启", new Object[]{notificationType});
      } else if (ObjectUtil.isEmpty(notification.getSmsTemplateId())) {
         throw new CommonException("场景【{}】未配置短信模板", new Object[]{notificationType});
      } else {
         ToolsSmsTemplate smsTemplate = this.smsTemplateService.queryEntity(notification.getSmsTemplateId());
         if (ObjectUtil.isEmpty(smsTemplate)) {
            throw new CommonException("短信模板不存在，ID：{}", new Object[]{notification.getSmsTemplateId()});
         } else {
            DevSmsSendParam sendParam = this.buildSmsParam(smsTemplate, phoneNumbers, templateParams);
            this.devSmsService.sendSmsAsync(sendParam);
            log.info("场景短信发送成功，场景类型：{}，手机号：{}，模板：{}", new Object[]{notificationType, phoneNumbers, smsTemplate.getTemplateName()});
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendNotificationEmail(String notificationType, String emailAddresses, Map<String, Object> templateParams) {
      this.sendNotificationEmailInternal(notificationType, emailAddresses, templateParams);
   }

   private void sendNotificationEmailInternal(String notificationType, String emailAddresses, Map<String, Object> templateParams) {
      ToolsNotification notification = this.notificationService.selectByType(notificationType);
      if (ObjectUtil.isEmpty(notification.getEmailStatus()) || notification.getEmailStatus() != 1) {
         throw new CommonException("场景【{}】的邮件通知未开启", new Object[]{notificationType});
      } else if (ObjectUtil.isEmpty(notification.getEmailTemplateId())) {
         throw new CommonException("场景【{}】未配置邮件模板", new Object[]{notificationType});
      } else {
         ToolsEmailTemplate emailTemplate = this.emailTemplateService.queryEntity(notification.getEmailTemplateId());
         if (ObjectUtil.isEmpty(emailTemplate)) {
            throw new CommonException("邮件模板不存在，ID：{}", new Object[]{notification.getEmailTemplateId()});
         } else {
            DevEmailSendParam sendParam = this.buildUnifiedEmailParam(emailTemplate, emailAddresses, templateParams);
            this.devEmailService.sendEmail(sendParam);
            log.info("场景邮件发送成功，场景类型：{}，邮箱：{}，模板：{}", new Object[]{notificationType, emailAddresses, emailTemplate.getTemplateName()});
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendNotificationTelegram(String notificationType, String chatId, Map<String, Object> templateParams) {
      this.sendNotificationTelegramInternal(notificationType, chatId, templateParams);
   }

   private void sendNotificationTelegramInternal(String notificationType, String chatId, Map<String, Object> templateParams) {
      ToolsNotification notification = this.notificationService.selectByType(notificationType);
      if (ObjectUtil.isEmpty(notification.getTelegramStatus()) || notification.getTelegramStatus() != 1) {
         throw new CommonException("场景【{}】的Telegram通知未开启", new Object[]{notificationType});
      } else if (ObjectUtil.isEmpty(notification.getTelegramTemplateId())) {
         throw new CommonException("场景【{}】未配置Telegram模板", new Object[]{notificationType});
      } else {
         ToolsTelegramTemplate telegramTemplate = this.telegramTemplateService.queryEntity(notification.getTelegramTemplateId());
         if (ObjectUtil.isEmpty(telegramTemplate)) {
            throw new CommonException("Telegram模板不存在，ID：{}", new Object[]{notification.getTelegramTemplateId()});
         } else {
            DevTelegramSendParam sendParam = this.buildTelegramParam(telegramTemplate, chatId, templateParams);
            this.devTelegramService.sendTelegramAsync(sendParam);
            log.info("场景Telegram发送成功，场景类型：{}，chatId：{}，模板：{}", new Object[]{notificationType, chatId, telegramTemplate.getTemplateName()});
         }
      }
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void runSmsInNewTx(String notificationType, String phone, Map<String, Object> templateParams) {
      this.sendNotificationSmsInternal(notificationType, phone, templateParams);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void runEmailInNewTx(String notificationType, String email, Map<String, Object> templateParams) {
      this.sendNotificationEmailInternal(notificationType, email, templateParams);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void runTelegramInNewTx(String notificationType, String chatId, Map<String, Object> templateParams) {
      this.sendNotificationTelegramInternal(notificationType, chatId, templateParams);
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendMemberNotificationBoth(String notificationType, Long userId, Map<String, Object> templateParams) {
      log.info("开始发送场景【{}】会员通知，会员ID：{}，参数：{}", new Object[]{notificationType, userId, templateParams});

      try {
         ToolsNotification notification = this.selectByType(notificationType);
         if (notification.getTargetType().equals(1)) {
            SaBaseClientLoginUser member = this.memberSaBaseLoginUserApi.getClientUserById(userId);
            log.info("会员信息：{}", member);
            if (this.isMemberNoticeDisabled(notification, member)) {
               log.info("会员通知已关闭，跳过场景通知，场景类型：{}，会员ID：{}", notificationType, userId);
               return;
            }

            if (member.getPhone() != null && notification.getSmsStatus().equals(1)) {
               try {
                  this.notificationService.runSmsInNewTx(notificationType, member.getPhone(), templateParams);
               } catch (Throwable var10) {
                  log.warn("会员短信通知发送失败: {}", var10.getMessage());
               }
            }

            if (member.getEmail() != null && notification.getEmailStatus().equals(1)) {
               try {
                  this.notificationService.runEmailInNewTx(notificationType, member.getEmail(), templateParams);
               } catch (Throwable var9) {
                  log.warn("会员邮件通知发送失败: {}", var9.getMessage());
               }
            }

            if (Integer.valueOf(1).equals(notification.getTelegramStatus())) {
               DevTelegramBinding binding = this.devTelegramBindingService.getEnabledBinding("MEMBER", String.valueOf(userId));
               if (binding != null) {
                  try {
                     this.notificationService.runTelegramInNewTx(notificationType, binding.getChatId(), templateParams);
                  } catch (Throwable var8) {
                     log.warn("会员Telegram通知发送失败: {}", var8.getMessage());
                  }
               }
            }
         }
      } catch (Throwable var11) {
         log.error("场景发送失败，场景类型：{}，用户ID：{}，参数：{}", new Object[]{notificationType, userId, templateParams});
         log.error("MEMBER场景发送失败：{}", var11.getMessage());
      }
   }

   private boolean isMemberNoticeDisabled(ToolsNotification notification, SaBaseClientLoginUser member) {
      return notification != null
         && member != null
         && "NOTICE".equals(notification.getConfigType())
         && Integer.valueOf(0).equals(member.getNotificationEnabled());
   }

   @Transactional(
      propagation = Propagation.REQUIRES_NEW,
      rollbackFor = {Exception.class}
   )
   @Override
   public void sendAdminNotificationBoth(String notificationType, Map<String, Object> templateParams) {
      try {
         ToolsNotification notification = this.selectByType(notificationType);
         if (notification.getTargetType().equals(2)) {
            for (BaseSysUser baseSysUser : this.sysUserApi.selectAllUser()) {
               if (baseSysUser.getPhone() != null && notification.getSmsStatus().equals(1)) {
                  try {
                     this.notificationService.runSmsInNewTx(notificationType, baseSysUser.getPhone(), templateParams);
                  } catch (Throwable var11) {
                     log.warn("管理员短信通知发送失败: {}", var11.getMessage());
                  }
               }

               if (baseSysUser.getEmail() != null && notification.getEmailStatus().equals(1)) {
                  try {
                     this.notificationService.runEmailInNewTx(notificationType, baseSysUser.getEmail(), templateParams);
                  } catch (Throwable var10) {
                     log.warn("管理员邮件通知发送失败: {}", var10.getMessage());
                  }
               }

               if (Integer.valueOf(1).equals(notification.getTelegramStatus())) {
                  DevTelegramBinding binding = this.devTelegramBindingService.getEnabledBinding("ADMIN", baseSysUser.getId());
                  if (binding != null) {
                     try {
                        this.notificationService.runTelegramInNewTx(notificationType, binding.getChatId(), templateParams);
                     } catch (Throwable var9) {
                        log.warn("管理员Telegram通知发送失败: {}", var9.getMessage());
                     }
                  }
               }
            }
         }
      } catch (Throwable var12) {
         log.error("场景发送失败，场景类型：{}，参数：{}", notificationType, templateParams);
         log.error("ADMIN场景发送失败：{}", var12.getMessage());
      }
   }

   private DevSmsSendParam buildSmsParam(ToolsSmsTemplate smsTemplate, String phoneNumbers, Map<String, Object> templateParams) {
      DevSmsSendParam sendParam = new DevSmsSendParam();
      sendParam.setPhoneNumbers(phoneNumbers);
      sendParam.setTemplateCode(smsTemplate.getTemplateCode());
      String templateContent = smsTemplate.getTemplateContent();
      JSONObject jsonParams = new JSONObject();

      for (Entry<String, Object> entry : templateParams.entrySet()) {
         String key = entry.getKey();
         Object value = entry.getValue();
         if (templateContent.contains("${" + key + "}") || templateContent.contains(key)) {
            jsonParams.set(key, value);
         }
      }

      String processedContent = templateContent;

      for (Entry<String, Object> entryx : templateParams.entrySet()) {
         processedContent = processedContent.replace("${" + entryx.getKey() + "}", String.valueOf(entryx.getValue()));
         if (!processedContent.contains("${" + entryx.getKey() + "}")) {
            processedContent = processedContent.replace(entryx.getKey(), String.valueOf(entryx.getValue()));
         }
      }

      sendParam.setTemplateParam(jsonParams.toString());
      sendParam.setTemplateContent(processedContent);
      log.info("构建短信参数完成，模板：{}，JSON参数：{}，模板内容：{}", new Object[]{smsTemplate.getTemplateName(), jsonParams.toString(), processedContent});
      return sendParam;
   }

   private DevEmailSendParam buildUnifiedEmailParam(ToolsEmailTemplate emailTemplate, String emailAddresses, Map<String, Object> templateParams) {
      DevEmailSendParam sendParam = new DevEmailSendParam();
      sendParam.setReceiveAccounts(emailAddresses);
      sendParam.setSubject(emailTemplate.getTemplateSubject());
      sendParam.setTemplateName(emailTemplate.getTemplateCode());
      sendParam.setTagName(emailTemplate.getTagName());
      String templateParamJson = JSONUtil.toJsonStr(templateParams);
      sendParam.setTemplateParam(templateParamJson);
      String content = emailTemplate.getTemplateContent();

      for (Entry<String, Object> entry : templateParams.entrySet()) {
         String key = entry.getKey();
         String value = String.valueOf(entry.getValue());
         content = content.replace("${" + key + "}", value);
         if (content.contains(key)) {
            content = content.replaceAll("\\b" + key + "\\b", value);
         }
      }

      sendParam.setContent(content);
      return sendParam;
   }

   private DevTelegramSendParam buildTelegramParam(ToolsTelegramTemplate telegramTemplate, String chatId, Map<String, Object> templateParams) {
      DevTelegramSendParam sendParam = new DevTelegramSendParam();
      sendParam.setChatId(chatId);
      sendParam.setTemplateName(telegramTemplate.getTemplateName());
      sendParam.setTemplateParam(JSONUtil.toJsonStr(templateParams));
      String content = telegramTemplate.getTemplateContent();
      if (templateParams != null) {
         for (Entry<String, Object> entry : templateParams.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            content = content.replace("${" + key + "}", value);
            if (content.contains(key)) {
               content = content.replaceAll("\\b" + Pattern.quote(key) + "\\b", Matcher.quoteReplacement(value));
            }
         }
      }

      sendParam.setContent(this.buildTelegramContent(content));
      sendParam.setParseMode("HTML");
      return sendParam;
   }

   private String buildTelegramContent(String content) {
      String body = ObjectUtil.isNotEmpty(content) ? content : "";
      return this.escapeTelegramHtml(body).replace("\r\n", "\n");
   }

   private String escapeTelegramHtml(String value) {
      return value == null ? "" : value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
   }

   @Override
   public Map<String, String> getSceneParamDescription(String notificationId) {
      ToolsNotification notification = this.notificationService.queryEntity(notificationId);
      Map<String, String> paramDescriptions = new HashMap<>();
      if (ObjectUtil.isNotEmpty(notification.getSceneParams())) {
         try {
            JSONObject sceneParams = JSONUtil.parseObj(notification.getSceneParams());
            sceneParams.forEach((key, value) -> paramDescriptions.put(key, value.toString()));
         } catch (Exception var5) {
            log.warn("解析场景参数说明失败，notificationId：{}", notificationId, var5);
         }
      }

      log.info("获取场景参数说明成功，场景ID：{}，参数个数：{}", notificationId, paramDescriptions.size());
      return paramDescriptions;
   }

   @Override
   public ToolsNotification selectByType(String notificationType) {
      log.info("获取通知配置，通知类型：{}", notificationType);
      if (null == notificationType) {
         throw new CommonException("通知类型不存在", new Object[0]);
      } else {
         return (ToolsNotification)this.toolsNotificationMapper.selectOne((Wrapper<ToolsNotification>)new LambdaQueryWrapper<ToolsNotification>().eq(ToolsNotification::getType, notificationType));
      }
   }
}
