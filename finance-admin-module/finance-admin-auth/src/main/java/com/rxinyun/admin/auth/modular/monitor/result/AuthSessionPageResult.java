package com.rxinyun.admin.auth.modular.monitor.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;

public class AuthSessionPageResult {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "头像"
   )
   private String avatar;
   @Schema(
      description = "账号"
   )
   private String account;
   @Schema(
      description = "姓名"
   )
   private String name;
   @Schema(
      description = "上次登录ip"
   )
   private String lastLoginIp;
   @Schema(
      description = "上次登录地点"
   )
   private String lastLoginAddress;
   @Schema(
      description = "上次登录时间"
   )
   private Date lastLoginTime;
   @Schema(
      description = "上次登录设备"
   )
   private String lastLoginDevice;
   @Schema(
      description = "最新登录ip"
   )
   private String latestLoginIp;
   @Schema(
      description = "最新登录地点"
   )
   private String latestLoginAddress;
   @Schema(
      description = "最新登录时间"
   )
   private Date latestLoginTime;
   @Schema(
      description = "最新登录设备"
   )
   private String latestLoginDevice;
   @Schema(
      description = "会话id"
   )
   private String sessionId;
   @Schema(
      description = "会话创建时间"
   )
   private Date sessionCreateTime;
   @Schema(
      description = "会话剩余有效期"
   )
   private String sessionTimeout;
   @Schema(
      description = "令牌数量"
   )
   private Integer tokenCount;
   @Schema(
      description = "令牌信息集合"
   )
   private List<AuthSessionPageResult.TokenSignInfo> tokenSignList;

   public String getId() {
      return this.id;
   }

   public String getAvatar() {
      return this.avatar;
   }

   public String getAccount() {
      return this.account;
   }

   public String getName() {
      return this.name;
   }

   public String getLastLoginIp() {
      return this.lastLoginIp;
   }

   public String getLastLoginAddress() {
      return this.lastLoginAddress;
   }

   public Date getLastLoginTime() {
      return this.lastLoginTime;
   }

   public String getLastLoginDevice() {
      return this.lastLoginDevice;
   }

   public String getLatestLoginIp() {
      return this.latestLoginIp;
   }

   public String getLatestLoginAddress() {
      return this.latestLoginAddress;
   }

   public Date getLatestLoginTime() {
      return this.latestLoginTime;
   }

   public String getLatestLoginDevice() {
      return this.latestLoginDevice;
   }

   public String getSessionId() {
      return this.sessionId;
   }

   public Date getSessionCreateTime() {
      return this.sessionCreateTime;
   }

   public String getSessionTimeout() {
      return this.sessionTimeout;
   }

   public Integer getTokenCount() {
      return this.tokenCount;
   }

   public List<AuthSessionPageResult.TokenSignInfo> getTokenSignList() {
      return this.tokenSignList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAvatar(final String avatar) {
      this.avatar = avatar;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setLastLoginIp(final String lastLoginIp) {
      this.lastLoginIp = lastLoginIp;
   }

   public void setLastLoginAddress(final String lastLoginAddress) {
      this.lastLoginAddress = lastLoginAddress;
   }

   public void setLastLoginTime(final Date lastLoginTime) {
      this.lastLoginTime = lastLoginTime;
   }

   public void setLastLoginDevice(final String lastLoginDevice) {
      this.lastLoginDevice = lastLoginDevice;
   }

   public void setLatestLoginIp(final String latestLoginIp) {
      this.latestLoginIp = latestLoginIp;
   }

   public void setLatestLoginAddress(final String latestLoginAddress) {
      this.latestLoginAddress = latestLoginAddress;
   }

   public void setLatestLoginTime(final Date latestLoginTime) {
      this.latestLoginTime = latestLoginTime;
   }

   public void setLatestLoginDevice(final String latestLoginDevice) {
      this.latestLoginDevice = latestLoginDevice;
   }

   public void setSessionId(final String sessionId) {
      this.sessionId = sessionId;
   }

   public void setSessionCreateTime(final Date sessionCreateTime) {
      this.sessionCreateTime = sessionCreateTime;
   }

   public void setSessionTimeout(final String sessionTimeout) {
      this.sessionTimeout = sessionTimeout;
   }

   public void setTokenCount(final Integer tokenCount) {
      this.tokenCount = tokenCount;
   }

   public void setTokenSignList(final List<AuthSessionPageResult.TokenSignInfo> tokenSignList) {
      this.tokenSignList = tokenSignList;
   }

   public static class TokenSignInfo {
      @Schema(
         description = "token值"
      )
      private String tokenValue;
      @Schema(
         description = "登录设备"
      )
      private String tokenDevice;
      @Schema(
         description = "token剩余有效期"
      )
      private String tokenTimeout;
      @Schema(
         description = "token剩余有效期百分比"
      )
      private Double tokenTimeoutPercent;

      public String getTokenValue() {
         return this.tokenValue;
      }

      public String getTokenDevice() {
         return this.tokenDevice;
      }

      public String getTokenTimeout() {
         return this.tokenTimeout;
      }

      public Double getTokenTimeoutPercent() {
         return this.tokenTimeoutPercent;
      }

      public void setTokenValue(final String tokenValue) {
         this.tokenValue = tokenValue;
      }

      public void setTokenDevice(final String tokenDevice) {
         this.tokenDevice = tokenDevice;
      }

      public void setTokenTimeout(final String tokenTimeout) {
         this.tokenTimeout = tokenTimeout;
      }

      public void setTokenTimeoutPercent(final Double tokenTimeoutPercent) {
         this.tokenTimeoutPercent = tokenTimeoutPercent;
      }
   }
}
