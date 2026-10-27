package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.hibernate.validator.constraints.Length;

public class NotificationSendParam {
   @Schema(
      description = "场景ID"
   )
   @NotBlank(
      message = "场景ID不能为空"
   )
   private String notificationId;
   @Schema(
      description = "手机号(多个用逗号分隔)"
   )
   @Length(
      max = 500,
      message = "手机号长度不能超过500"
   )
   private String phoneNumbers;
   @Schema(
      description = "邮箱地址(多个用逗号分隔)"
   )
   @Length(
      max = 500,
      message = "邮箱地址长度不能超过500"
   )
   private String emailAddresses;
   @Schema(
      description = "Telegram chat id"
   )
   @Length(
      max = 500,
      message = "Telegram chatId长度不能超过500"
   )
   private String chatId;
   @Schema(
      description = "模板参数"
   )
   private Map<String, Object> templateParams;

   public String getNotificationId() {
      return this.notificationId;
   }

   public String getPhoneNumbers() {
      return this.phoneNumbers;
   }

   public String getEmailAddresses() {
      return this.emailAddresses;
   }

   public String getChatId() {
      return this.chatId;
   }

   public Map<String, Object> getTemplateParams() {
      return this.templateParams;
   }

   public void setNotificationId(final String notificationId) {
      this.notificationId = notificationId;
   }

   public void setPhoneNumbers(final String phoneNumbers) {
      this.phoneNumbers = phoneNumbers;
   }

   public void setEmailAddresses(final String emailAddresses) {
      this.emailAddresses = emailAddresses;
   }

   public void setChatId(final String chatId) {
      this.chatId = chatId;
   }

   public void setTemplateParams(final Map<String, Object> templateParams) {
      this.templateParams = templateParams;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof NotificationSendParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$notificationId = this.getNotificationId();
         Object other$notificationId = other.getNotificationId();
         if (this$notificationId == null ? other$notificationId == null : this$notificationId.equals(other$notificationId)) {
            Object this$phoneNumbers = this.getPhoneNumbers();
            Object other$phoneNumbers = other.getPhoneNumbers();
            if (this$phoneNumbers == null ? other$phoneNumbers == null : this$phoneNumbers.equals(other$phoneNumbers)) {
               Object this$emailAddresses = this.getEmailAddresses();
               Object other$emailAddresses = other.getEmailAddresses();
               if (this$emailAddresses == null ? other$emailAddresses == null : this$emailAddresses.equals(other$emailAddresses)) {
                  Object this$chatId = this.getChatId();
                  Object other$chatId = other.getChatId();
                  if (this$chatId == null ? other$chatId == null : this$chatId.equals(other$chatId)) {
                     Object this$templateParams = this.getTemplateParams();
                     Object other$templateParams = other.getTemplateParams();
                     return this$templateParams == null ? other$templateParams == null : this$templateParams.equals(other$templateParams);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof NotificationSendParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $notificationId = this.getNotificationId();
      result = result * 59 + ($notificationId == null ? 43 : $notificationId.hashCode());
      Object $phoneNumbers = this.getPhoneNumbers();
      result = result * 59 + ($phoneNumbers == null ? 43 : $phoneNumbers.hashCode());
      Object $emailAddresses = this.getEmailAddresses();
      result = result * 59 + ($emailAddresses == null ? 43 : $emailAddresses.hashCode());
      Object $chatId = this.getChatId();
      result = result * 59 + ($chatId == null ? 43 : $chatId.hashCode());
      Object $templateParams = this.getTemplateParams();
      return result * 59 + ($templateParams == null ? 43 : $templateParams.hashCode());
   }

   @Override
   public String toString() {
      return "NotificationSendParam(notificationId="
         + this.getNotificationId()
         + ", phoneNumbers="
         + this.getPhoneNumbers()
         + ", emailAddresses="
         + this.getEmailAddresses()
         + ", chatId="
         + this.getChatId()
         + ", templateParams="
         + this.getTemplateParams()
         + ")";
   }
}
