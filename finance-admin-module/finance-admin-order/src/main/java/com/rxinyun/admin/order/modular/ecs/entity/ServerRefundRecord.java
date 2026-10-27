package com.rxinyun.admin.order.modular.ecs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName(
   value = "SERVER_REFUND_RECORD",
   autoResultMap = true
)
public class ServerRefundRecord {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "服务器ID"
   )
   private String serverId;
   @Schema(
      description = "服务器类型：1云服务器 2独立服务器 5虚拟主机"
   )
   private Integer productType;
   @Schema(
      description = "服务器主机名称"
   )
   private String serverName;
   @Schema(
      description = "服务器公网IP"
   )
   private String publicIp;
   @Schema(
      description = "退款金额"
   )
   private BigDecimal refundAmount;
   @Schema(
      description = "状态：0待处理 1已退款 -1已拒绝"
   )
   private Integer status;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;
   @Schema(
      description = "更新时间"
   )
   private Date updateTime;

   public String getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getPublicIp() {
      return this.publicIp;
   }

   public BigDecimal getRefundAmount() {
      return this.refundAmount;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Date getUpdateTime() {
      return this.updateTime;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setServerName(final String serverName) {
      this.serverName = serverName;
   }

   public void setPublicIp(final String publicIp) {
      this.publicIp = publicIp;
   }

   public void setRefundAmount(final BigDecimal refundAmount) {
      this.refundAmount = refundAmount;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }
}
