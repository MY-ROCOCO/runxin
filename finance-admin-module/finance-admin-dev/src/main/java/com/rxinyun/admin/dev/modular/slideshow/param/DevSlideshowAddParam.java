package com.rxinyun.admin.dev.modular.slideshow.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DevSlideshowAddParam {
   @Schema(
      description = "标题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "title不能为空"
   )
   private String title;
   @Schema(
      description = "图片"
   )
   private String image;
   @Schema(
      description = "跳转链接"
   )
   private String jumpLink;
   @Schema(
      description = "轮播图描述文字"
   )
   private String description;
   @Schema(
      description = "按钮是否显示"
   )
   private String buttonVisible;
   @Schema(
      description = "按钮显示名称"
   )
   private String buttonName;
   @Schema(
      description = "排序",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotNull(
      message = "sortCode不能为空"
   )
   private Integer sortCode;

   public String getTitle() {
      return this.title;
   }

   public String getImage() {
      return this.image;
   }

   public String getJumpLink() {
      return this.jumpLink;
   }

   public String getDescription() {
      return this.description;
   }

   public String getButtonVisible() {
      return this.buttonVisible;
   }

   public String getButtonName() {
      return this.buttonName;
   }

   public Integer getSortCode() {
      return this.sortCode;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setImage(final String image) {
      this.image = image;
   }

   public void setJumpLink(final String jumpLink) {
      this.jumpLink = jumpLink;
   }

   public void setDescription(final String description) {
      this.description = description;
   }

   public void setButtonVisible(final String buttonVisible) {
      this.buttonVisible = buttonVisible;
   }

   public void setButtonName(final String buttonName) {
      this.buttonName = buttonName;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }
}
