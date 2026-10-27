package com.rxinyun.admin.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class IndependentWorkPageParam implements Serializable {
   @Schema(
      description = "人工服务工单ID"
   )
   private Long id;
   @Schema(
      description = "独立服务器ID"
   )
   private String serverId;
   @Schema(
      description = "名称"
   )
   private String name;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "操作类型：1.创建 2.续费 3.升级 4.重建 5.重装系统 6.开机 7.关机 8.重启 9.重置密码 10.新增IP 11.断开电源 12.删除 13.锁定 14.解锁 15.到期自动关机 16.到期自动删除"
   )
   private Integer operateType;
   @Schema(
      description = "处理状态：0.未处理 1.处理中 2.处理完成 3.处理失败"
   )
   private Integer businessStatus;
   @Schema(
      description = "当前页码"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;
   @Schema(
      description = "排序字段，字段驼峰名称，如：userName"
   )
   private String sortField;
   @Schema(
      description = "排序方式，升序：ASCEND；降序：DESCEND"
   )
   private String sortOrder;
   @Schema(
      description = "账号、姓名关键词"
   )
   private String searchKey;

   public Long getId() {
      return this.id;
   }

   public String getServerId() {
      return this.serverId;
   }

   public String getName() {
      return this.name;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public Integer getOperateType() {
      return this.operateType;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
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

   public String getSearchKey() {
      return this.searchKey;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setOperateType(final Integer operateType) {
      this.operateType = operateType;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
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

   public void setSearchKey(final String searchKey) {
      this.searchKey = searchKey;
   }
}
