package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AgentUserPageParam {
   @Schema(
      description = "代理ID"
   )
   private String agentId;
   @Schema(
      description = "登录账号"
   )
   private String loginAccount;
   @Schema(
      description = "账号状态"
   )
   private String userStatus;
   @Schema(
      description = "当前页"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段"
   )
   private String sortField;
   @Schema(
      description = "排序方向"
   )
   private String sortOrder;

   public String getAgentId() {
      return this.agentId;
   }

   public String getLoginAccount() {
      return this.loginAccount;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setLoginAccount(final String loginAccount) {
      this.loginAccount = loginAccount;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }
}
