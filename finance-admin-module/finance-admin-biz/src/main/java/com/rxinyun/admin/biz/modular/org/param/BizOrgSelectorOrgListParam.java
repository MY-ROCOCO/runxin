package com.rxinyun.admin.biz.modular.org.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class BizOrgSelectorOrgListParam {
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "父id"
   )
   private String parentId;
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

   public String getParentId() {
      return this.parentId;
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

   public void setParentId(final String parentId) {
      this.parentId = parentId;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
