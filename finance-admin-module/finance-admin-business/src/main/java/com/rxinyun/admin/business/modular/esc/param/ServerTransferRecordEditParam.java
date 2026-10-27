package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Date;

public class ServerTransferRecordEditParam {
   @NotBlank
   @Schema(
      description = "转让服务器操作记录ID",
      required = true
   )
   private String id;
   @NotBlank(
      message = "云服务器主机名称不能为空"
   )
   @Schema(
      description = "主机名称",
      required = true
   )
   private String hostName;
   @NotBlank
   @Schema(
      description = "原始会员编号"
   )
   private String memberNo;
   @NotBlank(
      message = "目标会员标号不能为空"
   )
   @Schema(
      description = "目标会员编号"
   )
   private String targetMemberNo;
   @NotNull(
      message = "支付金额不能为空"
   )
   @PositiveOrZero(
      message = "支付金额不能为负数"
   )
   @Schema(
      description = "支付金额",
      required = true
   )
   private BigDecimal payAmount;
   @Schema(
      description = "操作时间"
   )
   private Date operateTime;
   @NotBlank
   @Schema(
      description = "创建时间",
      defaultValue = "CURRENT_TIMESTAMP"
   )
   private Date createTime;
   @NotNull
   @Positive(
      message = "通过/拒绝 status只能为 1/2"
   )
   @Schema(
      description = "转让云服务器操作状态, 默认为0转让中, 1是成功, 2是失败",
      defaultValue = "0"
   )
   private Integer status;
   @Schema(
      description = "密码",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "密码不能为空"
   )
   private String adminPassword;

   public String getId() {
      return this.id;
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

   public String getAdminPassword() {
      return this.adminPassword;
   }

   public void setId(final String id) {
      this.id = id;
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

   public void setAdminPassword(final String adminPassword) {
      this.adminPassword = adminPassword;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ServerTransferRecordEditParam other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
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
                              if (this$createTime == null ? other$createTime == null : this$createTime.equals(other$createTime)) {
                                 Object this$adminPassword = this.getAdminPassword();
                                 Object other$adminPassword = other.getAdminPassword();
                                 return this$adminPassword == null ? other$adminPassword == null : this$adminPassword.equals(other$adminPassword);
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
      return other instanceof ServerTransferRecordEditParam;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
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
      result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
      Object $adminPassword = this.getAdminPassword();
      return result * 59 + ($adminPassword == null ? 43 : $adminPassword.hashCode());
   }

   @Override
   public String toString() {
      return "ServerTransferRecordEditParam(id="
         + this.getId()
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
         + ", adminPassword="
         + this.getAdminPassword()
         + ")";
   }
}
