package com.rxinyun.member.order.modular.ecs.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public class MemberPayParam implements Serializable {
   @Schema(
      description = "订单编号"
   )
   @NotBlank(
      message = "订单编号不能为空"
   )
   private String orderNo;
   @Schema(
      description = "订单操作类型: 1: 购买, 2: 续费, 3: 升级"
   )
   private String actionType;
   @Schema(
      description = "增值服务"
   )
   private Integer valueAddedGroupId;
   @Schema(
      description = "优惠券编码"
   )
   private String couponCode;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   @NotNull(
      message = "支付方式不能为空"
   )
   private Integer payType;
   @Schema(
      description = "支付平台(OA:公众号, PC:电脑端, H5:手机端, MP:小程序, APP:APP)"
   )
   @NotBlank(
      message = "支付平台不能为空"
   )
   private String platform;

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getActionType() {
      return this.actionType;
   }

   public Integer getValueAddedGroupId() {
      return this.valueAddedGroupId;
   }

   public String getCouponCode() {
      return this.couponCode;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public String getPlatform() {
      return this.platform;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setActionType(final String actionType) {
      this.actionType = actionType;
   }

   public void setValueAddedGroupId(final Integer valueAddedGroupId) {
      this.valueAddedGroupId = valueAddedGroupId;
   }

   public void setCouponCode(final String couponCode) {
      this.couponCode = couponCode;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setPlatform(final String platform) {
      this.platform = platform;
   }
}
