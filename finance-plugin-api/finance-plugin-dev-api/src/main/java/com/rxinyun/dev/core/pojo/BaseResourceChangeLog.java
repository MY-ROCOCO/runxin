package com.rxinyun.dev.core.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public class BaseResourceChangeLog extends CommonEntity {
   @Schema(
      description = "主键 ID"
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
      description = "变更类型：CREATE-创建/UPDATE-更新/DELETE-删除"
   )
   private String changeType;
   @Schema(
      description = "是否已被消费：0-未处理/1-已处理"
   )
   private Integer processed;
   @Schema(
      description = "处理时间"
   )
   @JsonFormat(
      pattern = "yyyy-MM-dd HH:mm:ss"
   )
   private LocalDateTime processTime;
   @Schema(
      description = "上游/资源池实例主键（与业务表 UPSTREAM_SERVER_ID 一致，拉取方按此查询同步）"
   )
   private String upstreamServerId;
   @Schema(
      description = "资源同步目标类型：CLOUD-云服务器/INDEPENDENT-独立服务器"
   )
   private String poolSyncTarget;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getChangeType() {
      return this.changeType;
   }

   public Integer getProcessed() {
      return this.processed;
   }

   public LocalDateTime getProcessTime() {
      return this.processTime;
   }

   public String getUpstreamServerId() {
      return this.upstreamServerId;
   }

   public String getPoolSyncTarget() {
      return this.poolSyncTarget;
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

   public void setChangeType(final String changeType) {
      this.changeType = changeType;
   }

   public void setProcessed(final Integer processed) {
      this.processed = processed;
   }

   @JsonFormat(
      pattern = "yyyy-MM-dd HH:mm:ss"
   )
   public void setProcessTime(final LocalDateTime processTime) {
      this.processTime = processTime;
   }

   public void setUpstreamServerId(final String upstreamServerId) {
      this.upstreamServerId = upstreamServerId;
   }

   public void setPoolSyncTarget(final String poolSyncTarget) {
      this.poolSyncTarget = poolSyncTarget;
   }
}
