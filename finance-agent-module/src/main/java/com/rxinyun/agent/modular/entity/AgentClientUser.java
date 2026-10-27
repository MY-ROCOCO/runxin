package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.handler.CommonSm4CbcTypeHandler;
import com.rxinyun.common.pojo.CommonEntity;
import java.util.Date;

@TableName(
   value = "CLIENT_USER",
   autoResultMap = true
)
public class AgentClientUser extends CommonEntity {
   @TableId
   private Long id;
   private String account;
   private String name;
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED,
      typeHandler = CommonSm4CbcTypeHandler.class
   )
   private String phone;
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String email;
   private Date registerTime;
   private String userStatus;

   public Long getId() {
      return this.id;
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

   public Date getRegisterTime() {
      return this.registerTime;
   }

   public String getUserStatus() {
      return this.userStatus;
   }

   public void setId(final Long id) {
      this.id = id;
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

   public void setRegisterTime(final Date registerTime) {
      this.registerTime = registerTime;
   }

   public void setUserStatus(final String userStatus) {
      this.userStatus = userStatus;
   }
}
