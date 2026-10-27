package com.rxinyun.member.tools.modular.file.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class MemberFilePageParam {
   @Schema(
      description = "文件引擎"
   )
   private String engine;
   @Schema(
      description = "文件名关键词"
   )
   private String searchKey;

   public String getEngine() {
      return this.engine;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setEngine(final String engine) {
      this.engine = engine;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
