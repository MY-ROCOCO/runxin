package com.rxinyun.admin.notice.modular.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

public class TemplateVO {
   @Schema(
      description = "模板标识"
   )
   private String id;
   @Schema(
      description = "模板名称"
   )
   private String name;
   @Schema(
      description = "身份象征，1为会员，2为管理员"
   )
   private Integer symbol;
   @Schema(
      description = "是否含有邮件模板，1是0否"
   )
   private Integer havingEmail;
   @Schema(
      description = "是否开启发送邮件，0关闭，1开启"
   )
   private Integer emailStatus;
   @Schema(
      description = "是否含有短信模板，1是0否"
   )
   private Integer havingSms;
   @Schema(
      description = "是否开启发送邮件，0关闭，1开启"
   )
   private Integer smsStatus;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Integer getSymbol() {
      return this.symbol;
   }

   public Integer getHavingEmail() {
      return this.havingEmail;
   }

   public Integer getEmailStatus() {
      return this.emailStatus;
   }

   public Integer getHavingSms() {
      return this.havingSms;
   }

   public Integer getSmsStatus() {
      return this.smsStatus;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSymbol(final Integer symbol) {
      this.symbol = symbol;
   }

   public void setHavingEmail(final Integer havingEmail) {
      this.havingEmail = havingEmail;
   }

   public void setEmailStatus(final Integer emailStatus) {
      this.emailStatus = emailStatus;
   }

   public void setHavingSms(final Integer havingSms) {
      this.havingSms = havingSms;
   }

   public void setSmsStatus(final Integer smsStatus) {
      this.smsStatus = smsStatus;
   }
}
