package com.rxinyun.admin.biz.modular.index.result;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class BizIndexNoticeListResult {
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
      description = "摘要"
   )
   private String digest;
   @Schema(
      description = "类型"
   )
   private String type;
   @Schema(
      description = "排序"
   )
   private String sortCode;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getTitle() {
      return this.title;
   }

   public String getImage() {
      return this.image;
   }

   public String getDigest() {
      return this.digest;
   }

   public String getType() {
      return this.type;
   }

   public String getSortCode() {
      return this.sortCode;
   }

   public Date getCreateTime() {
      return this.createTime;
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

   public void setDigest(final String digest) {
      this.digest = digest;
   }

   public void setType(final String type) {
      this.type = type;
   }

   public void setSortCode(final String sortCode) {
      this.sortCode = sortCode;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }
}
