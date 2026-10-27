package com.rxinyun.admin.marketing.modular.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "MEMBER_COUPON",
   autoResultMap = true
)
public class MemberCoupon extends CommonEntity {
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "模板ID"
   )
   private String templateId;
   @Schema(
      description = "模板名称"
   )
   private String templateName;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "会员名称"
   )
   private String memberName;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "已使用次数"
   )
   private Integer usedCount;
   @Schema(
      description = "剩余可用次数"
   )
   private Integer remainingUseCount;
   @Schema(
      description = "锁定订单号"
   )
   private String lockOrderNo;
   @Schema(
      description = "锁定时间"
   )
   private Date lockTime;
   @Schema(
      description = "最近使用时间"
   )
   private Date lastUseTime;
   @Schema(
      description = "生效时间"
   )
   private Date effectiveTime;
   @Schema(
      description = "过期时间"
   )
   private Date expireTime;

   public String getId() {
      return this.id;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getMemberName() {
      return this.memberName;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getUsedCount() {
      return this.usedCount;
   }

   public Integer getRemainingUseCount() {
      return this.remainingUseCount;
   }

   public String getLockOrderNo() {
      return this.lockOrderNo;
   }

   public Date getLockTime() {
      return this.lockTime;
   }

   public Date getLastUseTime() {
      return this.lastUseTime;
   }

   public Date getEffectiveTime() {
      return this.effectiveTime;
   }

   public Date getExpireTime() {
      return this.expireTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setMemberName(final String memberName) {
      this.memberName = memberName;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setUsedCount(final Integer usedCount) {
      this.usedCount = usedCount;
   }

   public void setRemainingUseCount(final Integer remainingUseCount) {
      this.remainingUseCount = remainingUseCount;
   }

   public void setLockOrderNo(final String lockOrderNo) {
      this.lockOrderNo = lockOrderNo;
   }

   public void setLockTime(final Date lockTime) {
      this.lockTime = lockTime;
   }

   public void setLastUseTime(final Date lastUseTime) {
      this.lastUseTime = lastUseTime;
   }

   public void setEffectiveTime(final Date effectiveTime) {
      this.effectiveTime = effectiveTime;
   }

   public void setExpireTime(final Date expireTime) {
      this.expireTime = expireTime;
   }
}
