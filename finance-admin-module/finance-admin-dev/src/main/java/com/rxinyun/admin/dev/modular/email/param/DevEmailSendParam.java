package com.rxinyun.admin.dev.modular.email.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class DevEmailSendParam {
   @Schema(
      description = "接收人邮箱，多个用逗号分隔"
   )
   @NotBlank(
      message = "接收人邮箱不能为空"
   )
   @Length(
      max = 500,
      message = "接收人邮箱长度不能超过500"
   )
   private String receiveAccounts;
   @Schema(
      description = "邮件主题"
   )
   @Length(
      max = 200,
      message = "邮件主题长度不能超过200"
   )
   private String subject;
   @Schema(
      description = "邮件正文"
   )
   @Length(
      max = 5000,
      message = "邮件正文长度不能超过5000"
   )
   private String content;
   @Schema(
      description = "模板参数，JSON格式"
   )
   private String templateParam;
   @Schema(
      description = "模板名称/模板编码"
   )
   private String templateName;
   @Schema(
      description = "标签名"
   )
   private String tagName;
   @Schema(
      description = "发件人邮箱"
   )
   @Length(
      max = 100,
      message = "发件人邮箱长度不能超过100"
   )
   private String sendAccount;
   @Schema(
      description = "发件人昵称"
   )
   @Length(
      max = 50,
      message = "发件人昵称长度不能超过50"
   )
   private String sendUser;
   @Schema(
      description = "IP地址"
   )
   private String ip;

   public String getReceiveAccounts() {
      return this.receiveAccounts;
   }

   public String getSubject() {
      return this.subject;
   }

   public String getContent() {
      return this.content;
   }

   public String getTemplateParam() {
      return this.templateParam;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getTagName() {
      return this.tagName;
   }

   public String getSendAccount() {
      return this.sendAccount;
   }

   public String getSendUser() {
      return this.sendUser;
   }

   public String getIp() {
      return this.ip;
   }

   public void setReceiveAccounts(final String receiveAccounts) {
      this.receiveAccounts = receiveAccounts;
   }

   public void setSubject(final String subject) {
      this.subject = subject;
   }

   public void setContent(final String content) {
      this.content = content;
   }

   public void setTemplateParam(final String templateParam) {
      this.templateParam = templateParam;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setTagName(final String tagName) {
      this.tagName = tagName;
   }

   public void setSendAccount(final String sendAccount) {
      this.sendAccount = sendAccount;
   }

   public void setSendUser(final String sendUser) {
      this.sendUser = sendUser;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DevEmailSendParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$receiveAccounts = this.getReceiveAccounts();
         Object other$receiveAccounts = other.getReceiveAccounts();
         if (this$receiveAccounts == null ? other$receiveAccounts == null : this$receiveAccounts.equals(other$receiveAccounts)) {
            Object this$subject = this.getSubject();
            Object other$subject = other.getSubject();
            if (this$subject == null ? other$subject == null : this$subject.equals(other$subject)) {
               Object this$content = this.getContent();
               Object other$content = other.getContent();
               if (this$content == null ? other$content == null : this$content.equals(other$content)) {
                  Object this$templateParam = this.getTemplateParam();
                  Object other$templateParam = other.getTemplateParam();
                  if (this$templateParam == null ? other$templateParam == null : this$templateParam.equals(other$templateParam)) {
                     Object this$templateName = this.getTemplateName();
                     Object other$templateName = other.getTemplateName();
                     if (this$templateName == null ? other$templateName == null : this$templateName.equals(other$templateName)) {
                        Object this$tagName = this.getTagName();
                        Object other$tagName = other.getTagName();
                        if (this$tagName == null ? other$tagName == null : this$tagName.equals(other$tagName)) {
                           Object this$sendAccount = this.getSendAccount();
                           Object other$sendAccount = other.getSendAccount();
                           if (this$sendAccount == null ? other$sendAccount == null : this$sendAccount.equals(other$sendAccount)) {
                              Object this$sendUser = this.getSendUser();
                              Object other$sendUser = other.getSendUser();
                              if (this$sendUser == null ? other$sendUser == null : this$sendUser.equals(other$sendUser)) {
                                 Object this$ip = this.getIp();
                                 Object other$ip = other.getIp();
                                 return this$ip == null ? other$ip == null : this$ip.equals(other$ip);
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
      return other instanceof DevEmailSendParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $receiveAccounts = this.getReceiveAccounts();
      result = result * 59 + ($receiveAccounts == null ? 43 : $receiveAccounts.hashCode());
      Object $subject = this.getSubject();
      result = result * 59 + ($subject == null ? 43 : $subject.hashCode());
      Object $content = this.getContent();
      result = result * 59 + ($content == null ? 43 : $content.hashCode());
      Object $templateParam = this.getTemplateParam();
      result = result * 59 + ($templateParam == null ? 43 : $templateParam.hashCode());
      Object $templateName = this.getTemplateName();
      result = result * 59 + ($templateName == null ? 43 : $templateName.hashCode());
      Object $tagName = this.getTagName();
      result = result * 59 + ($tagName == null ? 43 : $tagName.hashCode());
      Object $sendAccount = this.getSendAccount();
      result = result * 59 + ($sendAccount == null ? 43 : $sendAccount.hashCode());
      Object $sendUser = this.getSendUser();
      result = result * 59 + ($sendUser == null ? 43 : $sendUser.hashCode());
      Object $ip = this.getIp();
      return result * 59 + ($ip == null ? 43 : $ip.hashCode());
   }

   @Override
   public String toString() {
      return "DevEmailSendParam(receiveAccounts="
         + this.getReceiveAccounts()
         + ", subject="
         + this.getSubject()
         + ", content="
         + this.getContent()
         + ", templateParam="
         + this.getTemplateParam()
         + ", templateName="
         + this.getTemplateName()
         + ", tagName="
         + this.getTagName()
         + ", sendAccount="
         + this.getSendAccount()
         + ", sendUser="
         + this.getSendUser()
         + ", ip="
         + this.getIp()
         + ")";
   }
}
