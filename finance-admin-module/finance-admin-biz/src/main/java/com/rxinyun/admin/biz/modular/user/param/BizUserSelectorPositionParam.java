package com.rxinyun.admin.biz.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizUserSelectorPositionParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "机构id"
   )
   private String orgId;
   @Schema(
      description = "名称关键词"
   )
   private String searchKey;

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getOrgId() {
      return this.orgId;
   }

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setOrgId(final String orgId) {
      this.orgId = orgId;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
