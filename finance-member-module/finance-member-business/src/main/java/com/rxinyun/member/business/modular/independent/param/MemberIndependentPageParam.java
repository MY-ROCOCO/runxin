package com.rxinyun.member.business.modular.independent.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberIndependentPageParam implements Serializable {
   @Schema(
      description = "区域标识"
   )
   private Long id;
   @Schema(
      hidden = true
   )
   private String name;
   @Schema(
      description = "主机名称，模糊查询"
   )
   private String serverName;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "主机状态: CREATING 创建中,  RUNNING 运行中, STOP 已关机, LOCKED 锁定, REINSTALLING 系统重装中"
   )
   private String status;
   @Schema(
      description = "业务状态: 业务状态枚举 1正常 -1过期 -2 删除"
   )
   private Integer businessStatus;
   @Schema(
      description = "服务器IP，模糊查询"
   )
   private String serverIp;
   @Schema(
      description = "备注"
   )
   private String remark;
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

   public String getName() {
      return this.name;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getStatus() {
      return this.status;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public String getServerIp() {
      return this.serverIp;
   }

   public String getRemark() {
      return this.remark;
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

   public void setName(final String name) {
      this.name = name;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setServerIp(final String serverIp) {
      this.serverIp = serverIp;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
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
