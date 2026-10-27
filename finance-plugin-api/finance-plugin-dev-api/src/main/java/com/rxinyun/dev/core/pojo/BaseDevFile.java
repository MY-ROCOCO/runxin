package com.rxinyun.dev.core.pojo;

import java.io.Serializable;

public class BaseDevFile implements Serializable {
   private String id;
   private String name;
   private String suffix;
   private String downloadPath;
   private String thumbnail;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public String getDownloadPath() {
      return this.downloadPath;
   }

   public String getThumbnail() {
      return this.thumbnail;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setSuffix(final String suffix) {
      this.suffix = suffix;
   }

   public void setDownloadPath(final String downloadPath) {
      this.downloadPath = downloadPath;
   }

   public void setThumbnail(final String thumbnail) {
      this.thumbnail = thumbnail;
   }
}
