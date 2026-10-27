package com.rxinyun.member.marketing.modular.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "COUPON_ORDER_RELATION",
   autoResultMap = true
)
public class CouponOrderRelation extends CommonEntity {
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "模板ID"
   )
   private String templateId;
   @Schema(
      description = "模板名称"
   )
   private String templateName;
   @Schema(
      description = "会员券ID"
   )
   private String memberCouponId;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "优惠金额"
   )
   private BigDecimal couponPrice;
   @Schema(
      description = "使用次数"
   )
   private Integer useCount;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "锁定时间"
   )
   private Date lockTime;
   @Schema(
      description = "使用时间"
   )
   private Date useTime;
   @Schema(
      description = "释放时间"
   )
   private Date releaseTime;
   @Schema(
      description = "快照"
   )
   private String snapshotJson;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getMemberCouponId() {
      return this.memberCouponId;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public BigDecimal getCouponPrice() {
      return this.couponPrice;
   }

   public Integer getUseCount() {
      return this.useCount;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Date getLockTime() {
      return this.lockTime;
   }

   public Date getUseTime() {
      return this.useTime;
   }

   public Date getReleaseTime() {
      return this.releaseTime;
   }

   public String getSnapshotJson() {
      return this.snapshotJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setMemberCouponId(final String memberCouponId) {
      this.memberCouponId = memberCouponId;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setCouponPrice(final BigDecimal couponPrice) {
      this.couponPrice = couponPrice;
   }

   public void setUseCount(final Integer useCount) {
      this.useCount = useCount;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setLockTime(final Date lockTime) {
      this.lockTime = lockTime;
   }

   public void setUseTime(final Date useTime) {
      this.useTime = useTime;
   }

   public void setReleaseTime(final Date releaseTime) {
      this.releaseTime = releaseTime;
   }

   public void setSnapshotJson(final String snapshotJson) {
      this.snapshotJson = snapshotJson;
   }
}
