package com.rxinyun.agent.modular.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

public class AgentCommissionPageResponse {
   @Schema(
      description = "流水ID"
   )
   private String id;
   @Schema(
      description = "代理ID"
   )
   private String agentId;
   @Schema(
      description = "会员编号"
   )
   private Long memberNo;
   @Schema(
      description = "会员账号"
   )
   private String memberAccount;
   @Schema(
      description = "订单号"
   )
   private String sourceOrderNo;
   @Schema(
      description = "产品类型"
   )
   private Integer productType;
   @Schema(
      description = "基数"
   )
   private BigDecimal baseAmount;
   @Schema(
      description = "比例"
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
      description = "来源退款批次号"
   )
   private String sourceRefundNo;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "创建时间"
   )
   private Date createTime;

   public String getId() {
      return this.id;
   }

   public String getAgentId() {
      return this.agentId;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getMemberAccount() {
      return this.memberAccount;
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

   public String getSourceRefundNo() {
      return this.sourceRefundNo;
   }

   public String getRemark() {
      return this.remark;
   }

   public Date getCreateTime() {
      return this.createTime;
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

   public void setMemberAccount(final String memberAccount) {
      this.memberAccount = memberAccount;
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

   public void setSourceRefundNo(final String sourceRefundNo) {
      this.sourceRefundNo = sourceRefundNo;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof AgentCommissionPageResponse other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$memberNo = this.getMemberNo();
         Object other$memberNo = other.getMemberNo();
         if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
            Object this$productType = this.getProductType();
            Object other$productType = other.getProductType();
            if (this$productType == null ? other$productType == null : this$productType.equals(other$productType)) {
               Object this$id = this.getId();
               Object other$id = other.getId();
               if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                  Object this$agentId = this.getAgentId();
                  Object other$agentId = other.getAgentId();
                  if (this$agentId == null ? other$agentId == null : this$agentId.equals(other$agentId)) {
                     Object this$memberAccount = this.getMemberAccount();
                     Object other$memberAccount = other.getMemberAccount();
                     if (this$memberAccount == null ? other$memberAccount == null : this$memberAccount.equals(other$memberAccount)) {
                        Object this$sourceOrderNo = this.getSourceOrderNo();
                        Object other$sourceOrderNo = other.getSourceOrderNo();
                        if (this$sourceOrderNo == null ? other$sourceOrderNo == null : this$sourceOrderNo.equals(other$sourceOrderNo)) {
                           Object this$baseAmount = this.getBaseAmount();
                           Object other$baseAmount = other.getBaseAmount();
                           if (this$baseAmount == null ? other$baseAmount == null : this$baseAmount.equals(other$baseAmount)) {
                              Object this$ratio = this.getRatio();
                              Object other$ratio = other.getRatio();
                              if (this$ratio == null ? other$ratio == null : this$ratio.equals(other$ratio)) {
                                 Object this$commissionAmount = this.getCommissionAmount();
                                 Object other$commissionAmount = other.getCommissionAmount();
                                 if (this$commissionAmount == null ? other$commissionAmount == null : this$commissionAmount.equals(other$commissionAmount)) {
                                    Object this$recordType = this.getRecordType();
                                    Object other$recordType = other.getRecordType();
                                    if (this$recordType == null ? other$recordType == null : this$recordType.equals(other$recordType)) {
                                       Object this$sourceRefundNo = this.getSourceRefundNo();
                                       Object other$sourceRefundNo = other.getSourceRefundNo();
                                       if (this$sourceRefundNo == null ? other$sourceRefundNo == null : this$sourceRefundNo.equals(other$sourceRefundNo)) {
                                          Object this$remark = this.getRemark();
                                          Object other$remark = other.getRemark();
                                          if (this$remark == null ? other$remark == null : this$remark.equals(other$remark)) {
                                             Object this$createTime = this.getCreateTime();
                                             Object other$createTime = other.getCreateTime();
                                             return this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime);
                                          } else {
                                             return false;
                                          }
                                       } else {
                                          return false;
                                       }
                                    } else {
                                       return false;
                                    }
                                 } else {
                                    return false;
                                 }
                              } else {
                                 return false;
                              }
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof AgentCommissionPageResponse;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
      Object $productType = this.getProductType();
      result = result * 59 + ($productType == null ? 43 : $productType.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $agentId = this.getAgentId();
      result = result * 59 + ($agentId == null ? 43 : $agentId.hashCode());
      Object $memberAccount = this.getMemberAccount();
      result = result * 59 + ($memberAccount == null ? 43 : $memberAccount.hashCode());
      Object $sourceOrderNo = this.getSourceOrderNo();
      result = result * 59 + ($sourceOrderNo == null ? 43 : $sourceOrderNo.hashCode());
      Object $baseAmount = this.getBaseAmount();
      result = result * 59 + ($baseAmount == null ? 43 : $baseAmount.hashCode());
      Object $ratio = this.getRatio();
      result = result * 59 + ($ratio == null ? 43 : $ratio.hashCode());
      Object $commissionAmount = this.getCommissionAmount();
      result = result * 59 + ($commissionAmount == null ? 43 : $commissionAmount.hashCode());
      Object $recordType = this.getRecordType();
      result = result * 59 + ($recordType == null ? 43 : $recordType.hashCode());
      Object $sourceRefundNo = this.getSourceRefundNo();
      result = result * 59 + ($sourceRefundNo == null ? 43 : $sourceRefundNo.hashCode());
      Object $remark = this.getRemark();
      result = result * 59 + ($remark == null ? 43 : $remark.hashCode());
      Object $createTime = this.getCreateTime();
      return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
   }

   @Override
   public String toString() {
      return "AgentCommissionPageResponse(id="
         + this.getId()
         + ", agentId="
         + this.getAgentId()
         + ", memberNo="
         + this.getMemberNo()
         + ", memberAccount="
         + this.getMemberAccount()
         + ", sourceOrderNo="
         + this.getSourceOrderNo()
         + ", productType="
         + this.getProductType()
         + ", baseAmount="
         + this.getBaseAmount()
         + ", ratio="
         + this.getRatio()
         + ", commissionAmount="
         + this.getCommissionAmount()
         + ", recordType="
         + this.getRecordType()
         + ", sourceRefundNo="
         + this.getSourceRefundNo()
         + ", remark="
         + this.getRemark()
         + ", createTime="
         + this.getCreateTime()
         + ")";
   }
}
