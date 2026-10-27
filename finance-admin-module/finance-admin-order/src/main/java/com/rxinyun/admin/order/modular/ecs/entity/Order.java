package com.rxinyun.admin.order.modular.ecs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "ORDERS",
   autoResultMap = true
)
public class Order {
   @Schema(
      description = "订单ID"
   )
   private String id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "计费方式：1：包年包月:2：按量计费 "
   )
   @NotNull(
      message = "计费方式不能为空"
   )
   private Integer billingMode;
   @Schema(
      description = "产品类型: 1 云服务器 2 独立服务器 3 域名 4 DNS云解析"
   )
   private Integer productType;
   @Schema(
      description = "订单类型 (1: 新购订单, 2: 升级订单, 3: 续费订单)"
   )
   private Integer orderType;
   @Schema(
      description = "升级项目 (1: 升级核心配置, 2: 升级带宽, 3: 升级防御, 4: 升级磁盘, 5: 升级IP, 6: 升级流量)"
   )
   private Integer upgradeItem;
   @Schema(
      description = "订单状态 (0: 未支付, -1: 已取消, 1: 已支付, -2:已退款, -3:退款中)"
   )
   private Integer orderStatus;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "会员名称"
   )
   private String memberName;
   @Schema(
      description = "会员手机号"
   )
   private String memberPhone;
   @Schema(
      description = "总价"
   )
   private BigDecimal totalPrice;
   @Schema(
      description = "实付金额"
   )
   private BigDecimal payPrice;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "会员券ID"
   )
   private String memberCouponId;
   @Schema(
      description = "优惠券抵扣金额"
   )
   private BigDecimal couponPrice;
   @Schema(
      description = "优惠券使用次数"
   )
   private Integer couponUseCount;
   @Schema(
      description = "折扣金额"
   )
   private BigDecimal discountPrice;
   @Schema(
      description = "原订单ID（续费或升级时关联）"
   )
   private String originalOrderNo;
   @Schema(
      description = "支付时间"
   )
   private Date payTime;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "更新时间"
   )
   private Date updateTime;
   @Schema(
      description = "支付订单号"
   )
   private String payOrderNo;
   @Schema(
      description = "支付类型 (1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   private Integer payType;
   @Schema(
      description = "USDT 支付时应付金额 (含随机小数，用于到账匹配)"
   )
   private BigDecimal expectedUsdtAmount;
   @Schema(
      description = "实例主键 JSON 数组（与 UPSTREAM_SERVER_ID 同语义）"
   )
   @TableField("UPSTREAM_SERVER_IDS")
   private String upstreamServerIds;
   @Schema(
      description = "发票占用状态：0待审核 1已开具，空表示未占用"
   )
   @TableField(
      exist = false
   )
   private Integer invoiceStatus;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Integer getBillingMode() {
      return this.billingMode;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public Integer getOrderType() {
      return this.orderType;
   }

   public Integer getUpgradeItem() {
      return this.upgradeItem;
   }

   public Integer getOrderStatus() {
      return this.orderStatus;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getMemberName() {
      return this.memberName;
   }

   public String getMemberPhone() {
      return this.memberPhone;
   }

   public BigDecimal getTotalPrice() {
      return this.totalPrice;
   }

   public BigDecimal getPayPrice() {
      return this.payPrice;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public String getMemberCouponId() {
      return this.memberCouponId;
   }

   public BigDecimal getCouponPrice() {
      return this.couponPrice;
   }

   public Integer getCouponUseCount() {
      return this.couponUseCount;
   }

   public BigDecimal getDiscountPrice() {
      return this.discountPrice;
   }

   public String getOriginalOrderNo() {
      return this.originalOrderNo;
   }

   public Date getPayTime() {
      return this.payTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public String getPayOrderNo() {
      return this.payOrderNo;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public BigDecimal getExpectedUsdtAmount() {
      return this.expectedUsdtAmount;
   }

   public String getUpstreamServerIds() {
      return this.upstreamServerIds;
   }

   public Integer getInvoiceStatus() {
      return this.invoiceStatus;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setBillingMode(final Integer billingMode) {
      this.billingMode = billingMode;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setOrderType(final Integer orderType) {
      this.orderType = orderType;
   }

   public void setUpgradeItem(final Integer upgradeItem) {
      this.upgradeItem = upgradeItem;
   }

   public void setOrderStatus(final Integer orderStatus) {
      this.orderStatus = orderStatus;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setMemberName(final String memberName) {
      this.memberName = memberName;
   }

   public void setMemberPhone(final String memberPhone) {
      this.memberPhone = memberPhone;
   }

   public void setTotalPrice(final BigDecimal totalPrice) {
      this.totalPrice = totalPrice;
   }

   public void setPayPrice(final BigDecimal payPrice) {
      this.payPrice = payPrice;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setMemberCouponId(final String memberCouponId) {
      this.memberCouponId = memberCouponId;
   }

   public void setCouponPrice(final BigDecimal couponPrice) {
      this.couponPrice = couponPrice;
   }

   public void setCouponUseCount(final Integer couponUseCount) {
      this.couponUseCount = couponUseCount;
   }

   public void setDiscountPrice(final BigDecimal discountPrice) {
      this.discountPrice = discountPrice;
   }

   public void setOriginalOrderNo(final String originalOrderNo) {
      this.originalOrderNo = originalOrderNo;
   }

   public void setPayTime(final Date payTime) {
      this.payTime = payTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }

   public void setPayOrderNo(final String payOrderNo) {
      this.payOrderNo = payOrderNo;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setExpectedUsdtAmount(final BigDecimal expectedUsdtAmount) {
      this.expectedUsdtAmount = expectedUsdtAmount;
   }

   public void setUpstreamServerIds(final String upstreamServerIds) {
      this.upstreamServerIds = upstreamServerIds;
   }

   public void setInvoiceStatus(final Integer invoiceStatus) {
      this.invoiceStatus = invoiceStatus;
   }
}
