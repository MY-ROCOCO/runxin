package com.rxinyun.admin.dev.modular.telegram.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("DEV_TELEGRAM_BINDING")
public class DevTelegramBinding extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "用户类型：MEMBER/ADMIN"
   )
   private String userType;
   @Schema(
      description = "用户ID"
   )
   private String userId;
   @Schema(
      description = "Telegram chat id"
   )
   private String chatId;
   @Schema(
      description = "Telegram用户ID"
   )
   private String telegramUserId;
   @Schema(
      description = "Telegram用户名"
   )
   private String telegramUsername;
   @Schema(
      description = "状态：1启用，0禁用"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getUserType() {
      return this.userType;
   }

   public String getUserId() {
      return this.userId;
   }

   public String getChatId() {
      return this.chatId;
   }

   public String getTelegramUserId() {
      return this.telegramUserId;
   }

   public String getTelegramUsername() {
      return this.telegramUsername;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setUserType(final String userType) {
      this.userType = userType;
   }

   public void setUserId(final String userId) {
      this.userId = userId;
   }

   public void setChatId(final String chatId) {
      this.chatId = chatId;
   }

   public void setTelegramUserId(final String telegramUserId) {
      this.telegramUserId = telegramUserId;
   }

   public void setTelegramUsername(final String telegramUsername) {
      this.telegramUsername = telegramUsername;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
