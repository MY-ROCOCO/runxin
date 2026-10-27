package com.rxinyun.admin.business.modular.esc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

@TableName("server_plus_record")
public class ServerTransferRecord {
   @Schema(
      description = "转让服务器操作记录ID",
      required = true
   )
   private String id;
   @Schema(
      description = "产品类型： 1 云服务器 2 独立服务器 5 虚拟主机",
      required = true
   )
   private Integer productType;
   @Schema(
      description = "主机名称",
      required = true
   )
   private String hostName;
   @Schema(
      description = "原始会员编号"
   )
   private String memberNo;
   @Schema(
      description = "目标会员编号"
   )
   private String targetMemberNo;
   @Schema(
      description = "支付金额",
      required = true
   )
   private BigDecimal payAmount;
   @Schema(
      description = "操作时间",
      required = true
   )
   private Date operateTime;
   @Schema(
      description = "创建时间",
      defaultValue = "CURRENT_TIMESTAMP"
   )
   private Date createTime;
   @Schema(
      description = "转让云服务器操作状态, 默认为0转让中, 1是成功, 2是失败",
      defaultValue = "0"
   )
   private Integer status;

   public String getId() {
      return this.id;
   }

   public Integer getProductType() {
      return this.productType;
   }

   public String getHostName() {
      return this.hostName;
   }

   public String getMemberNo() {
      return this.memberNo;
   }

   public String getTargetMemberNo() {
      return this.targetMemberNo;
   }

   public BigDecimal getPayAmount() {
      return this.payAmount;
   }

   public Date getOperateTime() {
      return this.operateTime;
   }

   public Date getCreateTime() {
      return this.createTime;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setProductType(final Integer productType) {
      this.productType = productType;
   }

   public void setHostName(final String hostName) {
      this.hostName = hostName;
   }

   public void setMemberNo(final String memberNo) {
      this.memberNo = memberNo;
   }

   public void setTargetMemberNo(final String targetMemberNo) {
      this.targetMemberNo = targetMemberNo;
   }

   public void setPayAmount(final BigDecimal payAmount) {
      this.payAmount = payAmount;
   }

   public void setOperateTime(final Date operateTime) {
      this.operateTime = operateTime;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ServerTransferRecord other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$productType = this.getProductType();
         Object other$productType = other.getProductType();
         if (this$productType == null ? other$productType == null : this$productType.equals(other$productType)) {
            Object this$status = this.getStatus();
            Object other$status = other.getStatus();
            if (this$status == null ? other$status == null : this$status.equals(other$status)) {
               Object this$id = this.getId();
               Object other$id = other.getId();
               if (this$id == null ? other$id == null : this$id.equals(other$id)) {
                  Object this$hostName = this.getHostName();
                  Object other$hostName = other.getHostName();
                  if (this$hostName == null ? other$hostName == null : this$hostName.equals(other$hostName)) {
                     Object this$memberNo = this.getMemberNo();
                     Object other$memberNo = other.getMemberNo();
                     if (this$memberNo == null ? other$memberNo == null : this$memberNo.equals(other$memberNo)) {
                        Object this$targetMemberNo = this.getTargetMemberNo();
                        Object other$targetMemberNo = other.getTargetMemberNo();
                        if (this$targetMemberNo == null ? other$targetMemberNo == null : this$targetMemberNo.equals(other$targetMemberNo)) {
                           Object this$payAmount = this.getPayAmount();
                           Object other$payAmount = other.getPayAmount();
                           if (this$payAmount == null ? other$payAmount == null : this$payAmount.equals(other$payAmount)) {
                              Object this$operateTime = this.getOperateTime();
                              Object other$operateTime = other.getOperateTime();
                              if (this$operateTime == null ? other$operateTime == null : this$operateTime.equals(other$operateTime)) {
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
      }
   }

   protected boolean canEqual(final Object other) {
      return other instanceof ServerTransferRecord;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $productType = this.getProductType();
      result = result * 59 + ($productType == null ? 43 : $productType.hashCode());
      Object $status = this.getStatus();
      result = result * 59 + ($status == null ? 43 : $status.hashCode());
      Object $id = this.getId();
      result = result * 59 + ($id == null ? 43 : $id.hashCode());
      Object $hostName = this.getHostName();
      result = result * 59 + ($hostName == null ? 43 : $hostName.hashCode());
      Object $memberNo = this.getMemberNo();
      result = result * 59 + ($memberNo == null ? 43 : $memberNo.hashCode());
      Object $targetMemberNo = this.getTargetMemberNo();
      result = result * 59 + ($targetMemberNo == null ? 43 : $targetMemberNo.hashCode());
      Object $payAmount = this.getPayAmount();
      result = result * 59 + ($payAmount == null ? 43 : $payAmount.hashCode());
      Object $operateTime = this.getOperateTime();
      result = result * 59 + ($operateTime == null ? 43 : $operateTime.hashCode());
      Object $createTime = this.getCreateTime();
      return result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
   }

   @Override
   public String toString() {
      return "ServerTransferRecord(id="
         + this.getId()
         + ", productType="
         + this.getProductType()
         + ", hostName="
         + this.getHostName()
         + ", memberNo="
         + this.getMemberNo()
         + ", targetMemberNo="
         + this.getTargetMemberNo()
         + ", payAmount="
         + this.getPayAmount()
         + ", operateTime="
         + this.getOperateTime()
         + ", createTime="
         + this.getCreateTime()
         + ", status="
         + this.getStatus()
         + ")";
   }
}
