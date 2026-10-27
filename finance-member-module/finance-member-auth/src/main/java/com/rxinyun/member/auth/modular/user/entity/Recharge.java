package com.rxinyun.member.auth.modular.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "MEMBER_RECHARGE",
   autoResultMap = true
)
public class Recharge extends CommonEntity {
   @Schema(
      description = "充值记录ID"
   )
   private String id;
   @Schema(
      description = "交易流水号"
   )
   private String transactionId;
   @Schema(
      description = "充值时间"
   )
   private Date rechargeTime;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "充值金额"
   )
   private BigDecimal amount;
   @Schema(
      description = "充值状态(-1: 已取消, 1: 未完成, 2: 已完成)"
   )
   private Integer status;
   @Schema(
      description = "支付方式(1: 微信, 2: 支付宝, 3: 积分, 4: 余额, 5: USDT)"
   )
   @NotBlank(
      message = "支付方式不能为空"
   )
   private Integer payType;
   @Schema(
      description = "USDT支付时应付金额(含随机小数，用于到账匹配)"
   )
   private BigDecimal expectedUsdtAmount;

   public String getId() {
      return this.id;
   }

   public String getTransactionId() {
      return this.transactionId;
   }

   public Date getRechargeTime() {
      return this.rechargeTime;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public BigDecimal getAmount() {
      return this.amount;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getPayType() {
      return this.payType;
   }

   public BigDecimal getExpectedUsdtAmount() {
      return this.expectedUsdtAmount;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setTransactionId(final String transactionId) {
      this.transactionId = transactionId;
   }

   public void setRechargeTime(final Date rechargeTime) {
      this.rechargeTime = rechargeTime;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setAmount(final BigDecimal amount) {
      this.amount = amount;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setPayType(final Integer payType) {
      this.payType = payType;
   }

   public void setExpectedUsdtAmount(final BigDecimal expectedUsdtAmount) {
      this.expectedUsdtAmount = expectedUsdtAmount;
   }
}
