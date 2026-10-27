package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "ORDERS",
   autoResultMap = true
)
public class AgentOrder {
   private String id;
   private String orderNo;
   private Integer productType;
   private Integer orderType;
   private Integer orderStatus;
   private Long memberNo;
   private String memberName;
   private String memberPhone;
   private BigDecimal totalPrice;
   private BigDecimal payPrice;
   private Date payTime;
   private Date createTime;

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

   public Date getPayTime() {
      return this.payTime;
   }

   public Date getCreateTime() {
      return this.createTime;
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

   public void setPayTime(final Date payTime) {
      this.payTime = payTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
