package com.rxinyun.agent.modular.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class AgentCommissionPageParam {
   @Schema(
      description = "代理ID，代理端可不传"
   )
   private String agentId;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "订单号"
   )
   private String sourceOrderNo;
   @Schema(
      description = "记录类型"
   )
   private String recordType;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "当前页"
   )
   private Integer current;
   @Schema(
      description = "每页条数"
   )
   private Integer size;

   public String getAgentId() {
      return this.agentId;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getSourceOrderNo() {
      return this.sourceOrderNo;
   }

   public String getRecordType() {
      return this.recordType;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public Integer getCurrent() {
      return this.current;
   }

   public Integer getSize() {
      return this.size;
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

   public void setRecordType(final String recordType) {
      this.recordType = recordType;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setCurrent(final Integer current) {
      this.current = current;
   }

   public void setSize(final Integer size) {
      this.size = size;
   }
}
