package com.rxinyun.dev.pojo;

public class DevTelegramBindingInfo {
   private Boolean bound;
   private String chatId;
   private String telegramUserId;
   private String telegramUsername;

   public Boolean getBound() {
      return this.bound;
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

   public void setBound(final Boolean bound) {
      this.bound = bound;
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
}
