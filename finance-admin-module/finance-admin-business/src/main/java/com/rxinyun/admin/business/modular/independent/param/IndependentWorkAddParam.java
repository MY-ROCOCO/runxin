package com.rxinyun.admin.business.modular.independent.param;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class IndependentWorkAddParam {
   @Schema(
      description = "独立服务器人工处理工单ID"
   )
   private String id;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "地域ID"
   )
   private String regionId;
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "独立服务器ID"
   )
   private String independentId;
   @Schema(
      description = "操作类型：1.创建 2.续费 3.升级 4.重建 5.重装系统 6.开机 7.关机 8.重启 9.重置密码 10.新增IP 11.断开电源 12.删除 13.锁定 14.解锁 15.到期自动关机 16.到期自动删除"
   )
   private Integer operateType;
   @Schema(
      description = "处理状态：0.未处理 1.处理中 2.处理完成 3.处理失败"
   )
   private Integer businessStatus;
   @Schema(
      description = "处理人"
   )
   private String independentUser;
   @Schema(
      description = "提交时间"
   )
   private Date submitTime;
   @Schema(
      description = "完成时间"
   )
   private Date completeTime;
   @Schema(
      description = "扩展信息"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getRegionId() {
      return this.regionId;
   }

   public String getLineId() {
      return this.lineId;
   }

   public String getIndependentId() {
      return this.independentId;
   }

   public Integer getOperateType() {
      return this.operateType;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public String getIndependentUser() {
      return this.independentUser;
   }

   public Date getSubmitTime() {
      return this.submitTime;
   }

   public Date getCompleteTime() {
      return this.completeTime;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setRegionId(final String regionId) {
      this.regionId = regionId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setIndependentId(final String independentId) {
      this.independentId = independentId;
   }

   public void setOperateType(final Integer operateType) {
      this.operateType = operateType;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setIndependentUser(final String independentUser) {
      this.independentUser = independentUser;
   }

   public void setSubmitTime(final Date submitTime) {
      this.submitTime = submitTime;
   }

   public void setCompleteTime(final Date completeTime) {
      this.completeTime = completeTime;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
