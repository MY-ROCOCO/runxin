package com.rxinyun.member.news.modular.news.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class NewsTypeDto {
   @Schema(
      description = "新闻类型id"
   )
   private String id;
   @Schema(
      description = "新闻类型名称"
   )
   private String name;
   @Schema(
      description = "新闻类型排序",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "sort不能为空"
   )
   private Long sort;
   @Schema(
      description = "新闻类型发布状态,0为未发布,1为发布,默认发布"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Long getSort() {
      return this.sort;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSort(final Long sort) {
      this.sort = sort;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }
}
