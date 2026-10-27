package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class AgentMemberPageResponse {
   @Schema(
      description = "绑定记录ID"
   )
   private String bindId;
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
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "会员账号"
   )
   private String account;
   @Schema(
      description = "会员姓名"
   )
   private String name;
   @Schema(
      description = "会员手机号"
   )
   private String phone;
   @Schema(
      description = "会员邮箱"
   )
   private String email;
   @Schema(
      description = "会员状态"
   )
   private String userStatus;
   @Schema(
      description = "注册时间"
   )
   private Date registerTime;
   @Schema(
      description = "绑定时间"
   )
   private Date bindTime;

   public String getBindId() {
      return this.bindId;
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

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getAccount() {
      return this.account;
   }

   public String getName() {
      return this.name;
   }

   public String getPhone() {
      return this.phone;
   }

   public String getEmail() {
      return this.email;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public Date getRegisterTime() {
      return this.registerTime;
   }

   public Date getBindTime() {
      return this.bindTime;
   }

   public void setBindId(final String bindId) {
      this.bindId = bindId;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setAccount(final String account) {
      this.account = account;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }

   public void setRegisterTime(final Date registerTime) {
      this.registerTime = registerTime;
   }

   public void setBindTime(final Date bindTime) {
      this.bindTime = bindTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AgentMemberPageResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$memberNo = this.getMemberNo();
         Object other$memberNo = other.getMemberNo();
         if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
            Object this$bindId = this.getBindId();
            Object other$bindId = other.getBindId();
            if (this$bindId == null ? other$bindId == null : this$bindId.equals(other$bindId)) {
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
                           Object this$name = this.getName();
                           Object other$name = other.getName();
                           if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                              Object this$phone = this.getPhone();
                              Object other$phone = other.getPhone();
                              if (this$phone == null ? other$phone == null : this$phone.equals(other$phone)) {
                                 Object this$email = this.getEmail();
                                 Object other$email = other.getEmail();
                                 if (this$email == null ? other$email == null : this$email.equals(other$email)) {
                                    Object this$userStatus = this.getUserStatus();
                                    Object other$userStatus = other.getUserStatus();
                                    if (this$userStatus == null ? other$userStatus == null : this$userStatus.equals(other$userStatus)) {
                                       Object this$registerTime = this.getRegisterTime();
                                       Object other$registerTime = other.getRegisterTime();
                                       if (this$registerTime == null ? other$registerTime == null : this$registerTime.equals(other$registerTime)) {
                                          Object this$bindTime = this.getBindTime();
                                          Object other$bindTime = other.getBindTime();
                                          return this$bindTime == null ? other$bindTime == null : this$bindTime.equals(other$bindTime);
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
      return other instanceof AgentMemberPageResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
      Object $bindId = this.getBindId();
      result = result * 59 + ($bindId == null ? 43 : $bindId.hashCode());
      Object $agentId = this.getAgentId();
      result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
      Object $agentCode = this.getAgentCode();
      result = result * 59 + ($agentCode == null ? 43 : $agentCode.hashCode());
      Object $agentName = this.getAgentName();
      result = result * 59 + ($agentName == null ? 43 : $agentName.hashCode());
      Object $account = this.getAccount();
      result = result * 59 + ($account == null ? 43 : $account.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $phone = this.getPhone();
      result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
      Object $email = this.getEmail();
      result = result * 59 + ($email == null ? 43 : $email.hashCode());
      Object $userStatus = this.getUserStatus();
      result = result * 59 + ($userStatus == null ? 43 : $userStatus.hashCode());
      Object $registerTime = this.getRegisterTime();
      result = result * 59 + ($registerTime == null ? 43 : $registerTime.hashCode());
      Object $bindTime = this.getBindTime();
      return result * 59 + ($bindTime == null ? 43 : $bindTime.hashCode());
   }

   @Override
   public String toString() {
      return "AgentMemberPageResponse(bindId="
         + this.getBindId()
         + ", agentId="
         + this.getAgentId()
         + ", agentCode="
         + this.getAgentCode()
         + ", agentName="
         + this.getAgentName()
         + ", memberNo="
         + this.getMemberNo()
         + ", account="
         + this.getAccount()
         + ", name="
         + this.getName()
         + ", phone="
         + this.getPhone()
         + ", email="
         + this.getEmail()
         + ", userStatus="
         + this.getUserStatus()
         + ", registerTime="
         + this.getRegisterTime()
         + ", bindTime="
         + this.getBindTime()
         + ")";
   }
}
