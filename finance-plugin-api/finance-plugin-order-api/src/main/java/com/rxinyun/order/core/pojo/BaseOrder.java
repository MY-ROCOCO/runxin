package com.rxinyun.order.core.pojo;

import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class BaseOrder extends CommonEntity {
   @Schema(
      description = "订单ID"
   )
   private String id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "产品类型: 1: 云服务器  2 独立服务器"
   )
   private Integer productType;
   @Schema(
      description = "订单类型 (1: 新购订单, 2: 升级订单, 3: 续费订单, 4: 团购订单, 5: 预订订单)"
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
      description = "服务器月单价"
   )
   private BigDecimal orderMonthsPrice;
   @Schema(
      description = "订单成本价"
   )
   private BigDecimal orderCostPrice;
   @Schema(
      description = "订单原价"
   )
   private BigDecimal orderOriginalPrice;
   @Schema(
      description = "订单总价"
   )
   private BigDecimal orderTotalPrice;
   @Schema(
      description = "订单积分"
   )
   private Integer orderIntegral;
   @Schema(
      description = "订单实付金额"
   )
   private BigDecimal orderPayPrice;
   @Schema(
      description = "支付流水号"
   )
   private String orderPayNo;
   @Schema(
      description = "订单数量"
   )
   private Integer orderNumber;
   @Schema(
      description = "支付类型 (1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   private Integer orderPayType;
   @Schema(
      description = "USDT支付时应付金额(含随机小数，用于到账匹配)"
   )
   private BigDecimal expectedAmountUsdt;
   @Schema(
      description = "是否使用优惠券 (1: 是, 0: 否)"
   )
   private Integer orderCoupon;
   @Schema(
      description = "优惠券编号"
   )
   private String orderCouponNo;
   @Schema(
      description = "优惠金额"
   )
   private BigDecimal orderCouponPrice;
   @Schema(
      description = "折扣金额"
   )
   private BigDecimal orderDiscountPrice;
   @Schema(
      description = "订单创建时间"
   )
   private Date orderCreateTime;
   @Schema(
      description = "订单支付时间"
   )
   private Date orderPayTime;
   @Schema(
      description = "订单到期时间"
   )
   private Date orderExpiresTime;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
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

   public BigDecimal getOrderMonthsPrice() {
      return this.orderMonthsPrice;
   }

   public BigDecimal getOrderCostPrice() {
      return this.orderCostPrice;
   }

   public BigDecimal getOrderOriginalPrice() {
      return this.orderOriginalPrice;
   }

   public BigDecimal getOrderTotalPrice() {
      return this.orderTotalPrice;
   }

   public Integer getOrderIntegral() {
      return this.orderIntegral;
   }

   public BigDecimal getOrderPayPrice() {
      return this.orderPayPrice;
   }

   public String getOrderPayNo() {
      return this.orderPayNo;
   }

   public Integer getOrderNumber() {
      return this.orderNumber;
   }

   public Integer getOrderPayType() {
      return this.orderPayType;
   }

   public BigDecimal getExpectedAmountUsdt() {
      return this.expectedAmountUsdt;
   }

   public Integer getOrderCoupon() {
      return this.orderCoupon;
   }

   public String getOrderCouponNo() {
      return this.orderCouponNo;
   }

   public BigDecimal getOrderCouponPrice() {
      return this.orderCouponPrice;
   }

   public BigDecimal getOrderDiscountPrice() {
      return this.orderDiscountPrice;
   }

   public Date getOrderCreateTime() {
      return this.orderCreateTime;
   }

   public Date getOrderPayTime() {
      return this.orderPayTime;
   }

   public Date getOrderExpiresTime() {
      return this.orderExpiresTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
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

   public void setOrderMonthsPrice(final BigDecimal orderMonthsPrice) {
      this.orderMonthsPrice = orderMonthsPrice;
   }

   public void setOrderCostPrice(final BigDecimal orderCostPrice) {
      this.orderCostPrice = orderCostPrice;
   }

   public void setOrderOriginalPrice(final BigDecimal orderOriginalPrice) {
      this.orderOriginalPrice = orderOriginalPrice;
   }

   public void setOrderTotalPrice(final BigDecimal orderTotalPrice) {
      this.orderTotalPrice = orderTotalPrice;
   }

   public void setOrderIntegral(final Integer orderIntegral) {
      this.orderIntegral = orderIntegral;
   }

   public void setOrderPayPrice(final BigDecimal orderPayPrice) {
      this.orderPayPrice = orderPayPrice;
   }

   public void setOrderPayNo(final String orderPayNo) {
      this.orderPayNo = orderPayNo;
   }

   public void setOrderNumber(final Integer orderNumber) {
      this.orderNumber = orderNumber;
   }

   public void setOrderPayType(final Integer orderPayType) {
      this.orderPayType = orderPayType;
   }

   public void setExpectedAmountUsdt(final BigDecimal expectedAmountUsdt) {
      this.expectedAmountUsdt = expectedAmountUsdt;
   }

   public void setOrderCoupon(final Integer orderCoupon) {
      this.orderCoupon = orderCoupon;
   }

   public void setOrderCouponNo(final String orderCouponNo) {
      this.orderCouponNo = orderCouponNo;
   }

   public void setOrderCouponPrice(final BigDecimal orderCouponPrice) {
      this.orderCouponPrice = orderCouponPrice;
   }

   public void setOrderDiscountPrice(final BigDecimal orderDiscountPrice) {
      this.orderDiscountPrice = orderDiscountPrice;
   }

   public void setOrderCreateTime(final Date orderCreateTime) {
      this.orderCreateTime = orderCreateTime;
   }

   public void setOrderPayTime(final Date orderPayTime) {
      this.orderPayTime = orderPayTime;
   }

   public void setOrderExpiresTime(final Date orderExpiresTime) {
      this.orderExpiresTime = orderExpiresTime;
   }
}
