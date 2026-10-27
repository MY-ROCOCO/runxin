package com.rxinyun.admin.system.modular.index.result;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class SysIndexVisLogListResult {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "日志分类"
   )
   private String category;
   @Schema(
      description = "日志名称"
   )
   private String name;
   @Schema(
      description = "执行状态"
   )
   private String exeStatus;
   @Schema(
      description = "具体消息"
   )
   private String exeMessage;
   @Schema(
      description = "操作ip"
   )
   private String opIp;
   @Schema(
      description = "操作地址"
   )
   private String opAddress;
   @Schema(
      description = "操作浏览器"
   )
   private String opBrowser;
   @Schema(
      description = "操作系统"
   )
   private String opOs;
   @Schema(
      description = "操作时间"
   )
   private Date opTime;
   @Schema(
      description = "操作人姓名"
   )
   private String opUser;
   @Schema(
      description = "签名数据"
   )
   private String signData;
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

   public String getId() {
      return this.id;
   }

   public String getCategory() {
      return this.category;
   }

   public String getName() {
      return this.name;
   }

   public String getExeStatus() {
      return this.exeStatus;
   }

   public String getExeMessage() {
      return this.exeMessage;
   }

   public String getOpIp() {
      return this.opIp;
   }

   public String getOpAddress() {
      return this.opAddress;
   }

   public String getOpBrowser() {
      return this.opBrowser;
   }

   public String getOpOs() {
      return this.opOs;
   }

   public Date getOpTime() {
      return this.opTime;
   }

   public String getOpUser() {
      return this.opUser;
   }

   public String getSignData() {
      return this.signData;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public String getCreateUser() {
      return this.createUser;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public String getUpdateUser() {
      return this.updateUser;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setExeStatus(final String exeStatus) {
      this.exeStatus = exeStatus;
   }

   public void setExeMessage(final String exeMessage) {
      this.exeMessage = exeMessage;
   }

   public void setOpIp(final String opIp) {
      this.opIp = opIp;
   }

   public void setOpAddress(final String opAddress) {
      this.opAddress = opAddress;
   }

   public void setOpBrowser(final String opBrowser) {
      this.opBrowser = opBrowser;
   }

   public void setOpOs(final String opOs) {
      this.opOs = opOs;
   }

   public void setOpTime(final Date opTime) {
      this.opTime = opTime;
   }

   public void setOpUser(final String opUser) {
      this.opUser = opUser;
   }

   public void setSignData(final String signData) {
      this.signData = signData;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setCreateUser(final String createUser) {
      this.createUser = createUser;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setUpdateUser(final String updateUser) {
      this.updateUser = updateUser;
   }
}
