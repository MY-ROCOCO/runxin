package com.rxinyun.admin.biz.modular.index.result;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;

public class BizIndexSlideshowDetailResult {
   @TableId
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "标题"
   )
   private String title;
   @Schema(
      description = "封面图"
   )
   private String image;
   @Schema(
      description = "内容"
   )
   private String content;
   @Schema(
      description = "摘要"
   )
   private String digest;
   @Schema(
      description = "类型"
   )
   private String type;
   @Schema(
      description = "状态"
   )
   private String status;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "排序"
   )
   private String sortCode;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

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

   public String getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getSortCode() {
      return this.sortCode;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
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

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setSortCode(final String sortCode) {
      this.sortCode = sortCode;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
