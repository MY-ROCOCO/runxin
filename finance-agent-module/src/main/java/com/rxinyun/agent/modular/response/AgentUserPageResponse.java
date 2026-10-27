package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class AgentUserPageResponse {
   @Schema(
      description = "代理账号ID"
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
      description = "登录账号"
   )
   private String loginAccount;
   @Schema(
      description = "账号状态"
   )
   private String userStatus;
   @Schema(
      description = "最近登录时间"
   )
   private Date latestLoginTime;
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

   public String getLoginAccount() {
      return this.loginAccount;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public Date getLatestLoginTime() {
      return this.latestLoginTime;
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

   public void setLoginAccount(final String loginAccount) {
      this.loginAccount = loginAccount;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }

   public void setLatestLoginTime(final Date latestLoginTime) {
      this.latestLoginTime = latestLoginTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AgentUserPageResponse other)) {
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
                     Object this$loginAccount = this.getLoginAccount();
                     Object other$loginAccount = other.getLoginAccount();
                     if (this$loginAccount == null ? other$loginAccount == null : this$loginAccount.equals(other$loginAccount)) {
                        Object this$userStatus = this.getUserStatus();
                        Object other$userStatus = other.getUserStatus();
                        if (this$userStatus == null ? other$userStatus == null : this$userStatus.equals(other$userStatus)) {
                           Object this$latestLoginTime = this.getLatestLoginTime();
                           Object other$latestLoginTime = other.getLatestLoginTime();
                           if (this$latestLoginTime == null ? other$latestLoginTime == null : this$latestLoginTime.equals(other$latestLoginTime)) {
                              Object this$createTime = this.getCreateTime();
                              Object other$createTime = other.getCreateTime();
                              return this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime);
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
      return other instanceof AgentUserPageResponse;
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
      Object $loginAccount = this.getLoginAccount();
      result = result * 59 + ($loginAccount == null ? 43 : $loginAccount.hashCode());
      Object $userStatus = this.getUserStatus();
      result = result * 59 + ($userStatus == null ? 43 : $userStatus.hashCode());
      Object $latestLoginTime = this.getLatestLoginTime();
      result = result * 59 + ($latestLoginTime == null ? 43 : $latestLoginTime.hashCode());
      Object $createTime = this.getCreateTime();
      return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
   }

   @Override
   public String toString() {
      return "AgentUserPageResponse(id="
         + this.getId()
         + ", agentId="
         + this.getAgentId()
         + ", agentCode="
         + this.getAgentCode()
         + ", agentName="
         + this.getAgentName()
         + ", loginAccount="
         + this.getLoginAccount()
         + ", userStatus="
         + this.getUserStatus()
         + ", latestLoginTime="
         + this.getLatestLoginTime()
         + ", createTime="
         + this.getCreateTime()
         + ")";
   }
}
