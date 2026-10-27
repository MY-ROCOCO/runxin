package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@TableName(
   value = "agent_info",
   autoResultMap = true
)
public class AgentInfo extends CommonEntity {
   @TableId(
      type = IdType.INPUT
   )
   private String id;
   @Schema(
      description = "代理编码"
   )
   private String agentCode;
   @Schema(
      description = "代理名称"
   )
   private String agentName;
   @Schema(
      description = "联系人"
   )
   private String contactName;
   @Schema(
      description = "联系电话"
   )
   private String contactPhone;
   @Schema(
      description = "状态"
   )
   private String status;
   @Schema(
      description = "代理余额"
   )
   private BigDecimal balance;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getAgentCode() {
      return this.agentCode;
   }

   public String getAgentName() {
      return this.agentName;
   }

   public String getContactName() {
      return this.contactName;
   }

   public String getContactPhone() {
      return this.contactPhone;
   }

   public String getStatus() {
      return this.status;
   }

   public BigDecimal getBalance() {
      return this.balance;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAgentCode(final String agentCode) {
      this.agentCode = agentCode;
   }

   public void setAgentName(final String agentName) {
      this.agentName = agentName;
   }

   public void setContactName(final String contactName) {
      this.contactName = contactName;
   }

   public void setContactPhone(final String contactPhone) {
      this.contactPhone = contactPhone;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setBalance(final BigDecimal balance) {
      this.balance = balance;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
