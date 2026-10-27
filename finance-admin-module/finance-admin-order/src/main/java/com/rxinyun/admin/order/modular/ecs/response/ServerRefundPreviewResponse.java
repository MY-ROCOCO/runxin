package com.rxinyun.admin.order.modular.ecs.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerRefundPreviewResponse {
   @Schema(
      description = "试算应退金额"
   )
   private BigDecimal refundAmount;
   @Schema(
      description = "FULL 或 PRORATED"
   )
   private String refundMode;
   @Schema(
      description = "基数 S：关联已支付订单实付之和"
   )
   private BigDecimal sumPayPrice;
   @Schema(
      description = "服务总时长毫秒 T_total"
   )
   private Long totalMillis;
   @Schema(
      description = "剩余时长毫秒 T_left"
   )
   private Long leftMillis;
   @Schema(
      description = "服务起点（buyTime）"
   )
   private Date buyTime;
   @Schema(
      description = "到期时间"
   )
   private Date endTime;
   @Schema(
      description = "试算时刻"
   )
   private Date now;
   @Schema(
      description = "参与计价的订单数"
   )
   private Integer orderCount;
   @Schema(
      description = "订单号列表"
   )
   private List<String> orderNos = new ArrayList<>();
   @Schema(
      description = "1 云 2 独立 5 虚拟主机"
   )
   private Integer productTypeResolved;
   @Schema(
      description = "提示（如已到期按天退为 0）"
   )
   private String hint;

   public BigDecimal getRefundAmount() {
      return this.refundAmount;
   }

   public String getRefundMode() {
      return this.refundMode;
   }

   public BigDecimal getSumPayPrice() {
      return this.sumPayPrice;
   }

   public Long getTotalMillis() {
      return this.totalMillis;
   }

   public Long getLeftMillis() {
      return this.leftMillis;
   }

   public Date getBuyTime() {
      return this.buyTime;
   }

   public Date getEndTime() {
      return this.endTime;
   }

   public Date getNow() {
      return this.now;
   }

   public Integer getOrderCount() {
      return this.orderCount;
   }

   public List<String> getOrderNos() {
      return this.orderNos;
   }

   public Integer getProductTypeResolved() {
      return this.productTypeResolved;
   }

   public String getHint() {
      return this.hint;
   }

   public void setRefundAmount(final BigDecimal refundAmount) {
      this.refundAmount = refundAmount;
   }

   public void setRefundMode(final String refundMode) {
      this.refundMode = refundMode;
   }

   public void setSumPayPrice(final BigDecimal sumPayPrice) {
      this.sumPayPrice = sumPayPrice;
   }

   public void setTotalMillis(final Long totalMillis) {
      this.totalMillis = totalMillis;
   }

   public void setLeftMillis(final Long leftMillis) {
      this.leftMillis = leftMillis;
   }

   public void setBuyTime(final Date buyTime) {
      this.buyTime = buyTime;
   }

   public void setEndTime(final Date endTime) {
      this.endTime = endTime;
   }

   public void setNow(final Date now) {
      this.now = now;
   }

   public void setOrderCount(final Integer orderCount) {
      this.orderCount = orderCount;
   }

   public void setOrderNos(final List<String> orderNos) {
      this.orderNos = orderNos;
   }

   public void setProductTypeResolved(final Integer productTypeResolved) {
      this.productTypeResolved = productTypeResolved;
   }

   public void setHint(final String hint) {
      this.hint = hint;
   }
}
