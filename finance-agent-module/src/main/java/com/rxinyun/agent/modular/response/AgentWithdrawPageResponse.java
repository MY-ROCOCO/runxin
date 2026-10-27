package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class AgentWithdrawPageResponse {
   @Schema(
      description = "记录ID"
   )
   private String id;
   @Schema(
      description = "代理ID"
   )
   private String agentId;
   @Schema(
      description = "代理编码"
   )
   private String agentCode;
   @Schema(
      description = "代理名称"
   )
   private String agentName;
   @Schema(
      description = "提现单号"
   )
   private String withdrawNo;
   @Schema(
      description = "提现金额"
   )
   private BigDecimal amount;
   @Schema(
      description = "提现状态"
   )
   private String status;
   @Schema(
      description = "收款人"
   )
   private String accountName;
   @Schema(
      description = "收款账号"
   )
   private String accountNo;
   @Schema(
      description = "开户行"
   )
   private String bankName;
   @Schema(
      description = "申请备注"
   )
   private String remark;
   @Schema(
      description = "审核备注"
   )
   private String reviewRemark;
   @Schema(
      description = "审核时间"
   )
   private Date reviewTime;
   @Schema(
      description = "审核人"
   )
   private String reviewUser;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getAgentId() {
      return this.agentId;
   }

   public String getAgentCode() {
      return this.agentCode;
   }

   public String getAgentName() {
      return this.agentName;
   }

   public String getWithdrawNo() {
      return this.withdrawNo;
   }

   public BigDecimal getAmount() {
      return this.amount;
   }

   public String getStatus() {
      return this.status;
   }

   public String getAccountName() {
      return this.accountName;
   }

   public String getAccountNo() {
      return this.accountNo;
   }

   public String getBankName() {
      return this.bankName;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getReviewRemark() {
      return this.reviewRemark;
   }

   public Date getReviewTime() {
      return this.reviewTime;
   }

   public String getReviewUser() {
      return this.reviewUser;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setAgentCode(final String agentCode) {
      this.agentCode = agentCode;
   }

   public void setAgentName(final String agentName) {
      this.agentName = agentName;
   }

   public void setWithdrawNo(final String withdrawNo) {
      this.withdrawNo = withdrawNo;
   }

   public void setAmount(final BigDecimal amount) {
      this.amount = amount;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setAccountName(final String accountName) {
      this.accountName = accountName;
   }

   public void setAccountNo(final String accountNo) {
      this.accountNo = accountNo;
   }

   public void setBankName(final String bankName) {
      this.bankName = bankName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setReviewRemark(final String reviewRemark) {
      this.reviewRemark = reviewRemark;
   }

   public void setReviewTime(final Date reviewTime) {
      this.reviewTime = reviewTime;
   }

   public void setReviewUser(final String reviewUser) {
      this.reviewUser = reviewUser;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
