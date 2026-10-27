package com.rxinyun.admin.biz.modular.notice.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class BizNoticeAddParam {
   @Schema(
      description = "标题",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "title不能为空"
   )
   private String title;
   @Schema(
      description = "封面图"
   )
   private String image;
   @Schema(
      description = "内容",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "content不能为空"
   )
   private String content;
   @Schema(
      description = "摘要",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "digest不能为空"
   )
   private String digest;
   @Schema(
      description = "类型",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "type不能为空"
   )
   private String type;
   @Schema(
      description = "发布位置",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "place不能为空"
   )
   private String place;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "排序码"
   )
   private Integer sortCode;

   public String getTitle() {
      return this.title;
   }

   public String getImage() {
      return this.image;
   }

   public String getContent() {
      return this.content;
   }

   public String getDigest() {
      return this.digest;
   }

   public String getType() {
      return this.type;
   }

   public String getPlace() {
      return this.place;
   }

   public String getRemark() {
      return this.remark;
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

   public void setContent(final String content) {
      this.content = content;
   }

   public void setDigest(final String digest) {
      this.digest = digest;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setPlace(final String place) {
      this.place = place;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setSortCode(final Integer sortCode) {
      this.sortCode = sortCode;
   }
}
