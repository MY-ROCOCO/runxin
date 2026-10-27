package com.rxinyun.member.admin.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class BaseBalance {
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

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BaseBalance other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$id = this.getId();
         Object other$id = other.getId();
         if (this$id == null ? other$id == null : this$id.equals(other$id)) {
            Object this$memberNo = this.getMemberNo();
            Object other$memberNo = other.getMemberNo();
            if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
               Object this$balance = this.getBalance();
               Object other$balance = other.getBalance();
               if (this$balance == null ? other$balance == null : this$balance.equals(other$balance)) {
                  Object this$createTime = this.getCreateTime();
                  Object other$createTime = other.getCreateTime();
                  if (this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime)) {
                     Object this$updateTime = this.getUpdateTime();
                     Object other$updateTime = other.getUpdateTime();
                     return this$updateTime == null ? other$updateTime == null : this$updateTime.equals(other$updateTime);
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof BaseBalance;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
      Object $balance = this.getBalance();
      result = result * 59 + ($balance == null ? 43 : $balance.hashCode());
      Object $createTime = this.getCreateTime();
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $updateTime = this.getUpdateTime();
      return result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
   }

   @Override
   public String toString() {
      return "BaseBalance(id="
         + this.getId()
         + ", memberNo="
         + this.getMemberNo()
         + ", balance="
         + this.getBalance()
         + ", createTime="
         + this.getCreateTime()
         + ", updateTime="
         + this.getUpdateTime()
         + ")";
   }
}
