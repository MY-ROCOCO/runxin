package com.rxinyun.admin.dev.modular.log.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName("MEMBER_LOG")
public class MemberLog {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
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
      description = "类名称"
   )
   private String className;
   @Schema(
      description = "方法名称"
   )
   private String methodName;
   @Schema(
      description = "请求方式"
   )
   private String reqMethod;
   @Schema(
      description = "请求地址"
   )
   private String reqUrl;
   @Schema(
      description = "请求参数"
   )
   private String paramJson;
   @Schema(
      description = "返回结果"
   )
   private String resultJson;
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

   public Long getMemberNo() {
      return this.memberNo;
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

   public String getClassName() {
      return this.className;
   }

   public String getMethodName() {
      return this.methodName;
   }

   public String getReqMethod() {
      return this.reqMethod;
   }

   public String getReqUrl() {
      return this.reqUrl;
   }

   public String getParamJson() {
      return this.paramJson;
   }

   public String getResultJson() {
      return this.resultJson;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
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

   public void setClassName(final String className) {
      this.className = className;
   }

   public void setMethodName(final String methodName) {
      this.methodName = methodName;
   }

   public void setReqMethod(final String reqMethod) {
      this.reqMethod = reqMethod;
   }

   public void setReqUrl(final String reqUrl) {
      this.reqUrl = reqUrl;
   }

   public void setParamJson(final String paramJson) {
      this.paramJson = paramJson;
   }

   public void setResultJson(final String resultJson) {
      this.resultJson = resultJson;
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
