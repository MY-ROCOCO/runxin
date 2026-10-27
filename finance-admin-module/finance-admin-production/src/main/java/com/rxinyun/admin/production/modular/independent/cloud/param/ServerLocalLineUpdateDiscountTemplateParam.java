package com.rxinyun.admin.production.modular.independent.cloud.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class ServerLocalLineUpdateDiscountTemplateParam {
   @Schema(
      description = "线路ID"
   )
   private String lineId;
   @Schema(
      description = "折扣模板ID"
   )
   private String discountTemplateId;

   public String getLineId() {
      return this.lineId;
   }

   public String getDiscountTemplateId() {
      return this.discountTemplateId;
   }

   public void setLineId(final String lineId) {
      this.lineId = lineId;
   }

   public void setDiscountTemplateId(final String discountTemplateId) {
      this.discountTemplateId = discountTemplateId;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ServerLocalLineUpdateDiscountTemplateParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$lineId = this.getLineId();
         Object other$lineId = other.getLineId();
         if (this$lineId == null ? other$lineId == null : this$lineId.equals(other$lineId)) {
            Object this$discountTemplateId = this.getDiscountTemplateId();
            Object other$discountTemplateId = other.getDiscountTemplateId();
            return this$discountTemplateId == null ? other$discountTemplateId == null : this$discountTemplateId.equals(other$discountTemplateId);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof ServerLocalLineUpdateDiscountTemplateParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $lineId = this.getLineId();
      result = result * 59 + ($lineId == null ? 43 : $lineId.hashCode());
      Object $discountTemplateId = this.getDiscountTemplateId();
      return result * 59 + ($discountTemplateId == null ? 43 : $discountTemplateId.hashCode());
   }

   @Override
   public String toString() {
      return "ServerLocalLineUpdateDiscountTemplateParam(lineId=" + this.getLineId() + ", discountTemplateId=" + this.getDiscountTemplateId() + ")";
   }
}
