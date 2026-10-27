package com.rxinyun.admin.dev.modular.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@TableName(
   value = "RESOURCE_POOL_REQUEST",
   autoResultMap = true
)
public class ResourcePoolRequest extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "订单号"
   )
   private String orderNo;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "请求参数（JSON格式）"
   )
   private String requestParams;
   @Schema(
      description = "资源池ID"
   )
   private String resourcePoolId;
   @TableField(
      exist = false
   )
   @Schema(
      description = "资源池名称"
   )
   private String resourcePoolName;
   @TableField(
      exist = false
   )
   @Schema(
      description = "资源池类型"
   )
   private String resourcePoolType;
   @Schema(
      description = "请求状态：0-待发送，1-已发送，2-成功，3-失败"
   )
   private Integer requestStatus;
   @Schema(
      description = "上游任务ID"
   )
   private String upstreamTaskId;
   @Schema(
      description = "回调数据（JSON 格式）"
   )
   private String callbackData;
   @Schema(
      description = "同步状态：PENDING-待同步/SYNCED-已同步/FAILED-同步失败"
   )
   private String syncStatus;
   @Schema(
      description = "最后同步时间"
   )
   private LocalDateTime lastSyncTime;
   @Schema(
      description = "上游数据版本标识（时间戳或哈希值，用于判断是否变更）"
   )
   private String upstreamDataVersion;
   @Schema(
      description = "操作类型：CREATE/RENEW/UPGRADE_DISK/UPGRADE_BANDWIDTH/UPGRADE_CPU_RAM/UPGRADE_DEFENSE/DELETE"
   )
   private String operationType;
   @Schema(
      description = "产品类型：1云 2独立，与订单 ProductTypeEnum 一致"
   )
   @TableField("PRODUCT_TYPE")
   private Integer productType;
   @Schema(
      description = "A系统服务器ID（续费/升级/删除时使用）"
   )
   private String aServerId;
   @Schema(
      description = "续费月数（RENEW 时使用）"
   )
   private Integer months;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getRequestParams() {
      return this.requestParams;
   }

   public String getResourcePoolId() {
      return this.resourcePoolId;
   }

   public String getResourcePoolName() {
      return this.resourcePoolName;
   }

   public String getResourcePoolType() {
      return this.resourcePoolType;
   }

   public Integer getRequestStatus() {
      return this.requestStatus;
   }

   public String getUpstreamTaskId() {
      return this.upstreamTaskId;
   }

   public String getCallbackData() {
      return this.callbackData;
   }

   public String getSyncStatus() {
      return this.syncStatus;
   }

   public LocalDateTime getLastSyncTime() {
      return this.lastSyncTime;
   }

   public String getUpstreamDataVersion() {
      return this.upstreamDataVersion;
   }

   public String getOperationType() {
      return this.operationType;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getAServerId() {
      return this.aServerId;
   }

   public Integer getMonths() {
      return this.months;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setRequestParams(final String requestParams) {
      this.requestParams = requestParams;
   }

   public void setResourcePoolId(final String resourcePoolId) {
      this.resourcePoolId = resourcePoolId;
   }

   public void setResourcePoolName(final String resourcePoolName) {
      this.resourcePoolName = resourcePoolName;
   }

   public void setResourcePoolType(final String resourcePoolType) {
      this.resourcePoolType = resourcePoolType;
   }

   public void setRequestStatus(final Integer requestStatus) {
      this.requestStatus = requestStatus;
   }

   public void setUpstreamTaskId(final String upstreamTaskId) {
      this.upstreamTaskId = upstreamTaskId;
   }

   public void setCallbackData(final String callbackData) {
      this.callbackData = callbackData;
   }

   public void setSyncStatus(final String syncStatus) {
      this.syncStatus = syncStatus;
   }

   public void setLastSyncTime(final LocalDateTime lastSyncTime) {
      this.lastSyncTime = lastSyncTime;
   }

   public void setUpstreamDataVersion(final String upstreamDataVersion) {
      this.upstreamDataVersion = upstreamDataVersion;
   }

   public void setOperationType(final String operationType) {
      this.operationType = operationType;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setAServerId(final String aServerId) {
      this.aServerId = aServerId;
   }

   public void setMonths(final Integer months) {
      this.months = months;
   }
}
