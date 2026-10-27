package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "agent_withdraw_record",
   autoResultMap = true
)
public class AgentWithdrawRecord extends CommonEntity {
   @TableId(
      type = IdType.INPUT
   )
   private String id;
   @Schema(
      description = "代理ID"
   )
   private String agentId;
   @Schema(
      description = "提现单号"
   )
   private String withdrawNo;
   @Schema(
      description = "提现金额"
   )
   private BigDecimal amount;
   @Schema(
      description = "状态"
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

   public String getId() {
      return this.id;
   }

   public String getAgentId() {
      return this.agentId;
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

   public void setId(final String id) {
      this.id = id;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
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
}
