package com.rxinyun.member.tools.modular.log.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberLogVisLineChartDataResult {
   @Schema(
      description = "日期"
   )
   private String date;
   @Schema(
      description = "登录数量"
   )
   private Long loginCount;
   @Schema(
      description = "登出数量"
   )
   private Long logoutCount;

   public String getDate() {
      return this.date;
   }

   public Long getLoginCount() {
      return this.loginCount;
   }

   public Long getLogoutCount() {
      return this.logoutCount;
   }

   public void setDate(final String date) {
      this.date = date;
   }

   public void setLoginCount(final Long loginCount) {
      this.loginCount = loginCount;
   }

   public void setLogoutCount(final Long logoutCount) {
      this.logoutCount = logoutCount;
   }
}
