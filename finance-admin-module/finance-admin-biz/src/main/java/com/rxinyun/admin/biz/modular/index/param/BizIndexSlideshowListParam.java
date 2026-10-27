package com.rxinyun.admin.biz.modular.index.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizIndexSlideshowListParam {
   @Schema(
      description = "位置"
   )
   private String place;

   public String getPlace() {
      return this.place;
   }

   public void setPlace(final String place) {
      this.place = place;
   }
}
