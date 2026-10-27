package com.rxinyun.admin.member.modular.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "MEMBER_BALANCE",
   autoResultMap = true
)
public class AdminMemberBalance {
   @Schema(
      description = "余额表ID"
   )
   private Long id;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "余额金额"
   )
   private BigDecimal balance;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "更新时间"
   )
   private Date updateTime;

   public Long getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public BigDecimal getBalance() {
      return this.balance;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setBalance(final BigDecimal balance) {
      this.balance = balance;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }
}
