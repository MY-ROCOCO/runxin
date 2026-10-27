package com.rxinyun.admin.biz.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizUserExportParam {
   @Schema(
      description = "人员状态"
   )
   private String userStatus;
   @Schema(
      description = "账号、姓名、手机号关键词"
   )
   private String searchKey;
   @Schema(
      description = "人员id集合"
   )
   private String userIds;

   public String getUserStatus() {
      return this.userStatus;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getUserIds() {
      return this.userIds;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setUserIds(final String userIds) {
      this.userIds = userIds;
   }
}
