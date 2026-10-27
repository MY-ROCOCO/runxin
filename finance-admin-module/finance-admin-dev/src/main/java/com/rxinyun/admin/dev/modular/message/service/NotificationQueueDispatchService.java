package com.rxinyun.admin.dev.modular.message.service;

import com.rxinyun.admin.dev.modular.email.entity.DevEmail;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.admin.dev.modular.sms.entity.DevSms;
import com.rxinyun.admin.dev.modular.sms.service.DevSmsService;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegram;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramService;
import jakarta.annotation.Resource;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationQueueDispatchService {
   private static final Logger log = LoggerFactory.getLogger(NotificationQueueDispatchService.class);
   private static final int BATCH_SIZE = 20;
   @Resource
   private DevEmailService devEmailService;
   @Resource
   private DevSmsService devSmsService;
   @Resource
   private DevTelegramService devTelegramService;
   @Resource(
      name = "notificationTaskExecutor"
   )
   private Executor notificationTaskExecutor;

   @Scheduled(
      fixedDelayString = "${runxin.notification.queue.fixed-delay-ms:5000}"
   )
   public void dispatchPendingNotifications() {
      this.dispatchPendingEmails();
      this.dispatchPendingSms();
      this.dispatchPendingTelegram();
   }

   private void dispatchPendingEmails() {
      for (DevEmail email : this.devEmailService.listPendingEmails(20)) {
         if (this.devEmailService.markSending(email.getId())) {
            this.notificationTaskExecutor.execute(() -> this.devEmailService.dispatchQueuedEmail(email));
         }
      }
   }

   private void dispatchPendingSms() {
      for (DevSms sms : this.devSmsService.listPendingSms(20)) {
         if (this.devSmsService.markSending(sms.getId())) {
            this.notificationTaskExecutor.execute(() -> this.devSmsService.dispatchQueuedSms(sms));
         }
      }
   }

   private void dispatchPendingTelegram() {
      for (DevTelegram telegram : this.devTelegramService.listPendingTelegram(20)) {
         if (this.devTelegramService.markSending(telegram.getId())) {
            this.notificationTaskExecutor.execute(() -> this.devTelegramService.dispatchQueuedTelegram(telegram));
         }
      }
   }
}
