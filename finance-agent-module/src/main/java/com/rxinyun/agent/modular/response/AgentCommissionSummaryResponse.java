package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public class AgentCommissionSummaryResponse {
   @Schema(
      description = "累计正向分成"
   )
   private BigDecimal totalCommission = BigDecimal.ZERO;
   @Schema(
      description = "累计退款冲减"
   )
   private BigDecimal totalRefundDeduct = BigDecimal.ZERO;
   @Schema(
      description = "净分成"
   )
   private BigDecimal netCommission = BigDecimal.ZERO;

   public BigDecimal getTotalCommission() {
      return this.totalCommission;
   }

   public BigDecimal getTotalRefundDeduct() {
      return this.totalRefundDeduct;
   }

   public BigDecimal getNetCommission() {
      return this.netCommission;
   }

   public void setTotalCommission(final BigDecimal totalCommission) {
      this.totalCommission = totalCommission;
   }

   public void setTotalRefundDeduct(final BigDecimal totalRefundDeduct) {
      this.totalRefundDeduct = totalRefundDeduct;
   }

   public void setNetCommission(final BigDecimal netCommission) {
      this.netCommission = netCommission;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AgentCommissionSummaryResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$totalCommission = this.getTotalCommission();
         Object other$totalCommission = other.getTotalCommission();
         if (this$totalCommission == null ? other$totalCommission == null : this$totalCommission.equals(other$totalCommission)) {
            Object this$totalRefundDeduct = this.getTotalRefundDeduct();
            Object other$totalRefundDeduct = other.getTotalRefundDeduct();
            if (this$totalRefundDeduct == null ? other$totalRefundDeduct == null : this$totalRefundDeduct.equals(other$totalRefundDeduct)) {
               Object this$netCommission = this.getNetCommission();
               Object other$netCommission = other.getNetCommission();
               return this$netCommission == null ? other$netCommission == null : this$netCommission.equals(other$netCommission);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AgentCommissionSummaryResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $totalCommission = this.getTotalCommission();
      result = result * 59 + ($totalCommission == null ? 43 : $totalCommission.hashCode());
      Object $totalRefundDeduct = this.getTotalRefundDeduct();
      result = result * 59 + ($totalRefundDeduct == null ? 43 : $totalRefundDeduct.hashCode());
      Object $netCommission = this.getNetCommission();
      return result * 59 + ($netCommission == null ? 43 : $netCommission.hashCode());
   }

   @Override
   public String toString() {
      return "AgentCommissionSummaryResponse(totalCommission="
         + this.getTotalCommission()
         + ", totalRefundDeduct="
         + this.getTotalRefundDeduct()
         + ", netCommission="
         + this.getNetCommission()
         + ")";
   }
}
