package com.rxinyun.member.tools.modular.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("DEV_FILE")
public class MemberFile extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "存储引擎"
   )
   private String engine;
   @Schema(
      description = "存储桶"
   )
   private String bucket;
   @Schema(
      description = "文件名称"
   )
   private String name;
   @Schema(
      description = "文件后缀"
   )
   private String suffix;
   @Schema(
      description = "文件大小kb"
   )
   private String sizeKb;
   @Schema(
      description = "文件大小（格式化后）"
   )
   private String sizeInfo;
   @Schema(
      description = "文件的对象名（唯一名称）"
   )
   private String objName;
   @Schema(
      description = "文件存储路径"
   )
   private String storagePath;
   @Schema(
      description = "文件下载路径"
   )
   private String downloadPath;
   @Schema(
      description = "图片缩略图"
   )
   private String thumbnail;
   @Schema(
      description = "扩展信息"
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getEngine() {
      return this.engine;
   }

   public String getBucket() {
      return this.bucket;
   }

   public String getName() {
      return this.name;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public String getSizeKb() {
      return this.sizeKb;
   }

   public String getSizeInfo() {
      return this.sizeInfo;
   }

   public String getObjName() {
      return this.objName;
   }

   public String getStoragePath() {
      return this.storagePath;
   }

   public String getDownloadPath() {
      return this.downloadPath;
   }

   public String getThumbnail() {
      return this.thumbnail;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setEngine(final String engine) {
      this.engine = engine;
   }

   public void setBucket(final String bucket) {
      this.bucket = bucket;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSuffix(final String suffix) {
      this.suffix = suffix;
   }

   public void setSizeKb(final String sizeKb) {
      this.sizeKb = sizeKb;
   }

   public void setSizeInfo(final String sizeInfo) {
      this.sizeInfo = sizeInfo;
   }

   public void setObjName(final String objName) {
      this.objName = objName;
   }

   public void setStoragePath(final String storagePath) {
      this.storagePath = storagePath;
   }

   public void setDownloadPath(final String downloadPath) {
      this.downloadPath = downloadPath;
   }

   public void setThumbnail(final String thumbnail) {
      this.thumbnail = thumbnail;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
