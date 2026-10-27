package com.rxinyun.admin.marketing.modular.marketing.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class DiscountDetailAddParam {
   @Schema(
      description = "会员组ID"
   )
   private String groupId;
   @Schema(
      description = "会员组名称"
   )
   private String groupName;
   @Schema(
      description = "折扣值: 百分比(如0.1表示10%)"
   )
   private BigDecimal discountValue;
   @Schema(
      description = "使用次数限制: 0表示无限制"
   )
   private Integer useLimit;

   public String getGroupId() {
      return this.groupId;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public BigDecimal getDiscountValue() {
      return this.discountValue;
   }

   public Integer getUseLimit() {
      return this.useLimit;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }

   public void setGroupName(final String groupName) {
      this.groupName = groupName;
   }

   public void setDiscountValue(final BigDecimal discountValue) {
      this.discountValue = discountValue;
   }

   public void setUseLimit(final Integer useLimit) {
      this.useLimit = useLimit;
   }
}
