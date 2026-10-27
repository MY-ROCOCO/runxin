package com.rxinyun.admin.marketing.modular.coupon.param;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   description = "会员优惠券分页查询参数"
)
public class MemberCouponPageParam {
   @Schema(
      description = "搜索关键词，支持优惠券名称/券码模糊搜索",
      example = "新人"
   )
   private String searchKey;
   @Schema(
      description = "模板ID",
      example = "2039000000000000001"
   )
   private String templateId;
   @Schema(
      description = "会员编号",
      example = "10001"
   )
   private Long memberNo;
   @Schema(
      description = "状态：0未使用 1已锁定 2已用尽 3已过期 4已禁用",
      example = "0"
   )
   private Integer status;
   @Schema(
      description = "当前页码",
      example = "1"
   )
   private Integer current;
   @Schema(
      description = "每页条数",
      example = "10"
   )
   private Integer size;
   @Schema(
      description = "排序字段",
      example = "createTime"
   )
   private String sortField;
   @Schema(
      description = "排序方向：ascend/descend",
      example = "descend"
   )
   private String sortOrder;

   public String getSearchKey() {
      return this.searchKey;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
   }

   public String getSortField() {
      return this.sortField;
   }

   public String getSortOrder() {
      return this.sortOrder;
   }

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }

   public void setSortField(final String sortField) {
      this.sortField = sortField;
   }

   public void setSortOrder(final String sortOrder) {
      this.sortOrder = sortOrder;
   }
}
