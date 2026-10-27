package com.rxinyun.common.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class CommonEntity implements Serializable {
   @JsonIgnore
   @TableLogic
   @Schema(
      description = "删除标志"
   )
   @TableField(
      fill = FieldFill.INSERT
   )
   private String deleteFlag;
   @Schema(
      description = "创建时间"
   )
   @TableField(
      fill = FieldFill.INSERT
   )
   private Date createTime;
   @Schema(
      description = "创建人"
   )
   @TableField(
      fill = FieldFill.INSERT
   )
   private String createUser;
   @Schema(
      description = "创建人名称"
   )
   @TableField(
      exist = false
   )
   private String createUserName;
   @Schema(
      description = "更新时间"
   )
   @TableField(
      fill = FieldFill.UPDATE
   )
   private Date updateTime;
   @Schema(
      description = "更新人"
   )
   @TableField(
      fill = FieldFill.UPDATE
   )
   private String updateUser;
   @Schema(
      description = "更新人名称"
   )
   @TableField(
      exist = false
   )
   private String updateUserName;

   public String getDeleteFlag() {
      return this.deleteFlag;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public String getCreateUser() {
      return this.createUser;
   }

   public String getCreateUserName() {
      return this.createUserName;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public String getUpdateUser() {
      return this.updateUser;
   }

   public String getUpdateUserName() {
      return this.updateUserName;
   }

   @JsonIgnore
   public void setDeleteFlag(final String deleteFlag) {
      this.deleteFlag = deleteFlag;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setCreateUser(final String createUser) {
      this.createUser = createUser;
   }

   public void setCreateUserName(final String createUserName) {
      this.createUserName = createUserName;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setUpdateUser(final String updateUser) {
      this.updateUser = updateUser;
   }

   public void setUpdateUserName(final String updateUserName) {
      this.updateUserName = updateUserName;
   }
}
