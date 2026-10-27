package com.rxinyun.agent.modular.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@TableName(
   value = "agent_commission_record",
   autoResultMap = true
)
public class AgentCommissionRecord extends CommonEntity {
   @TableId(
      type = IdType.INPUT
   )
   private String id;
   @Schema(
      description = "代理主体ID"
   )
   private String agentId;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "来源订单号"
   )
   private String sourceOrderNo;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "计佣基数"
   )
   private BigDecimal baseAmount;
   @Schema(
      description = "分成比例"
   )
   private BigDecimal ratio;
   @Schema(
      description = "分成金额"
   )
   private BigDecimal commissionAmount;
   @Schema(
      description = "记录类型"
   )
   private String recordType;
   @Schema(
      description = "记录状态"
   )
   private String recordStatus;
   @Schema(
      description = "关联记录ID"
   )
   private String relatedRecordId;
   @Schema(
      description = "来源退款批次号"
   )
   private String sourceRefundNo;
   @Schema(
      description = "幂等键"
   )
   private String idempotentKey;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getAgentId() {
      return this.agentId;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getSourceOrderNo() {
      return this.sourceOrderNo;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public BigDecimal getBaseAmount() {
      return this.baseAmount;
   }

   public BigDecimal getRatio() {
      return this.ratio;
   }

   public BigDecimal getCommissionAmount() {
      return this.commissionAmount;
   }

   public String getRecordType() {
      return this.recordType;
   }

   public String getRecordStatus() {
      return this.recordStatus;
   }

   public String getRelatedRecordId() {
      return this.relatedRecordId;
   }

   public String getSourceRefundNo() {
      return this.sourceRefundNo;
   }

   public String getIdempotentKey() {
      return this.idempotentKey;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setAgentId(final String agentId) {
      this.agentId = agentId;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setSourceOrderNo(final String sourceOrderNo) {
      this.sourceOrderNo = sourceOrderNo;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setBaseAmount(final BigDecimal baseAmount) {
      this.baseAmount = baseAmount;
   }

   public void setRatio(final BigDecimal ratio) {
      this.ratio = ratio;
   }

   public void setCommissionAmount(final BigDecimal commissionAmount) {
      this.commissionAmount = commissionAmount;
   }

   public void setRecordType(final String recordType) {
      this.recordType = recordType;
   }

   public void setRecordStatus(final String recordStatus) {
      this.recordStatus = recordStatus;
   }

   public void setRelatedRecordId(final String relatedRecordId) {
      this.relatedRecordId = relatedRecordId;
   }

   public void setSourceRefundNo(final String sourceRefundNo) {
      this.sourceRefundNo = sourceRefundNo;
   }

   public void setIdempotentKey(final String idempotentKey) {
      this.idempotentKey = idempotentKey;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
