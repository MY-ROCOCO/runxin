package com.rxinyun.member.tools.modular.log.result;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberLogOpBarChartDataResult {
   @Schema(
      description = "日期"
   )
   private String date;
   @Schema(
      description = "名称"
   )
   private String name;
   @Schema(
      description = "数量"
   )
   private Long count;

   public String getDate() {
      return this.date;
   }

   public String getName() {
      return this.name;
   }

   public Long getCount() {
      return this.count;
   }

   public void setDate(final String date) {
      this.date = date;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setCount(final Long count) {
      this.count = count;
   }
}
