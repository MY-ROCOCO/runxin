package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ServerPageParam implements Serializable {
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
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "主机状态: CREATING 创建中,  RUNNING 运行中, STOP 已关机, LOCKED 锁定, REINSTALLING 系统重装中"
   )
   private String serverStatus;
   @Schema(
      description = "业务状态: 业务状态枚举 1正常 -1过期 -2 删除"
   )
   private Integer businessStatus;
   @Schema(
      description = "宿主机ID，按宿主机筛选其下的云服务器"
   )
   private String masterId;
   @Schema(
      description = "服务器IP，根据 business_server_ip 表绑定关系筛选云服务器（模糊匹配）"
   )
   private String serverIp;
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

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getServerStatus() {
      return this.serverStatus;
   }

   public Integer getBusinessStatus() {
      return this.businessStatus;
   }

   public String getMasterId() {
      return this.masterId;
   }

   public String getServerIp() {
      return this.serverIp;
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

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setServerStatus(final String serverStatus) {
      this.serverStatus = serverStatus;
   }

   public void setBusinessStatus(final Integer businessStatus) {
      this.businessStatus = businessStatus;
   }

   public void setMasterId(final String masterId) {
      this.masterId = masterId;
   }

   public void setServerIp(final String serverIp) {
      this.serverIp = serverIp;
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
