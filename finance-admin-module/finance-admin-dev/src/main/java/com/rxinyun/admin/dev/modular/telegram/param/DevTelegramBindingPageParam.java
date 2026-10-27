package com.rxinyun.admin.dev.modular.telegram.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class DevTelegramBindingPageParam {
   @Schema(
      description = "用户类型：MEMBER/ADMIN"
   )
   private String userType;
   @Schema(
      description = "用户ID"
   )
   private String userId;
   @Schema(
      description = "chatId或Telegram用户名关键词"
   )
   private String searchKey;

   public String getUserType() {
      return this.userType;
   }

   public String getUserId() {
      return this.userId;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setUserType(final String userType) {
      this.userType = userType;
   }

   public void setUserId(final String userId) {
      this.userId = userId;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
