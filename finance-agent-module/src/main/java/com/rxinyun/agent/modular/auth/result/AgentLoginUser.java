package com.rxinyun.agent.modular.auth.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class AgentLoginUser implements Serializable {
   @Schema(
      description = "代理账号ID"
   )
   private String id;
   @Schema(
      description = "代理主体ID"
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
      description = "登录账号"
   )
   private String account;
   @Schema(
      description = "账号状态"
   )
   private String userStatus;
   @Schema(
      description = "代理状态"
   )
   private String agentStatus;
   @Schema(
      description = "最新登录时间"
   )
   private Date latestLoginTime;

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

   public String getAccount() {
      return this.account;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public String getAgentStatus() {
      return this.agentStatus;
   }

   public Date getLatestLoginTime() {
      return this.latestLoginTime;
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

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }

   public void setAgentStatus(final String agentStatus) {
      this.agentStatus = agentStatus;
   }

   public void setLatestLoginTime(final Date latestLoginTime) {
      this.latestLoginTime = latestLoginTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AgentLoginUser other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
            Object this$agentId = this.getAgentId();
            Object other$agentId = other.getAgentId();
            if (this$agentId == null ? other$agentId == null : this$agentId.equals(other$agentId)) {
               Object this$agentCode = this.getAgentCode();
               Object other$agentCode = other.getAgentCode();
               if (this$agentCode == null ? other$agentCode == null : this$agentCode.equals(other$agentCode)) {
                  Object this$agentName = this.getAgentName();
                  Object other$agentName = other.getAgentName();
                  if (this$agentName == null ? other$agentName == null : this$agentName.equals(other$agentName)) {
                     Object this$account = this.getAccount();
                     Object other$account = other.getAccount();
                     if (this$account == null ? other$account == null : this$account.equals(other$account)) {
                        Object this$userStatus = this.getUserStatus();
                        Object other$userStatus = other.getUserStatus();
                        if (this$userStatus == null ? other$userStatus == null : this$userStatus.equals(other$userStatus)) {
                           Object this$agentStatus = this.getAgentStatus();
                           Object other$agentStatus = other.getAgentStatus();
                           if (this$agentStatus == null ? other$agentStatus == null : this$agentStatus.equals(other$agentStatus)) {
                              Object this$latestLoginTime = this.getLatestLoginTime();
                              Object other$latestLoginTime = other.getLatestLoginTime();
                              return this$latestLoginTime == null ? other$latestLoginTime == null : this$latestLoginTime.equals(other$latestLoginTime);
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AgentLoginUser;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $agentId = this.getAgentId();
      result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
      Object $agentCode = this.getAgentCode();
      result = result * 59 + ($agentCode == null ? 43 : $agentCode.hashCode());
      Object $agentName = this.getAgentName();
      result = result * 59 + ($agentName == null ? 43 : $agentName.hashCode());
      Object $account = this.getAccount();
      result = result * 59 + ($account == null ? 43 : $account.hashCode());
      Object $userStatus = this.getUserStatus();
      result = result * 59 + ($userStatus == null ? 43 : $userStatus.hashCode());
      Object $agentStatus = this.getAgentStatus();
      result = result * 59 + ($agentStatus == null ? 43 : $agentStatus.hashCode());
      Object $latestLoginTime = this.getLatestLoginTime();
      return result * 59 + ($latestLoginTime == null ? 43 : $latestLoginTime.hashCode());
   }

   @Override
   public String toString() {
      return "AgentLoginUser(id="
         + this.getId()
         + ", agentId="
         + this.getAgentId()
         + ", agentCode="
         + this.getAgentCode()
         + ", agentName="
         + this.getAgentName()
         + ", account="
         + this.getAccount()
         + ", userStatus="
         + this.getUserStatus()
         + ", agentStatus="
         + this.getAgentStatus()
         + ", latestLoginTime="
         + this.getLatestLoginTime()
         + ")";
   }
}
