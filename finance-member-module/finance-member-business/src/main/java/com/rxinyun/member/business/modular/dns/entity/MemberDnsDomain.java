package com.rxinyun.member.business.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "DNS_DOMAIN",
   autoResultMap = true
)
public class MemberDnsDomain extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "会员ID"
   )
   private String memberId;
   @Schema(
      description = "DNS套餐实例ID"
   )
   private String packageInstanceId;
   @Schema(
      description = "DNS套餐实例名称"
   )
   @TableField(
      exist = false
   )
   private String packageInstanceName;
   @Schema(
      description = "主NS域名"
   )
   @TableField(
      exist = false
   )
   private String primaryNs;
   @Schema(
      description = "备NS域名"
   )
   @TableField(
      exist = false
   )
   private String secondaryNs;
   @Schema(
      description = "套餐最低TTL"
   )
   @TableField(
      exist = false
   )
   private Integer minTtl;
   @Schema(
      description = "托管域名"
   )
   private String domainName;
   @Schema(
      description = "托管域名联系人邮箱"
   )
   private String email;
   @Schema(
      description = "状态：1开启 0暂停"
   )
   private Integer status;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getId() {
      return this.id;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public String getPackageInstanceId() {
      return this.packageInstanceId;
   }

   public String getPackageInstanceName() {
      return this.packageInstanceName;
   }

   public String getPrimaryNs() {
      return this.primaryNs;
   }

   public String getSecondaryNs() {
      return this.secondaryNs;
   }

   public Integer getMinTtl() {
      return this.minTtl;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getEmail() {
      return this.email;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setPackageInstanceId(final String packageInstanceId) {
      this.packageInstanceId = packageInstanceId;
   }

   public void setPackageInstanceName(final String packageInstanceName) {
      this.packageInstanceName = packageInstanceName;
   }

   public void setPrimaryNs(final String primaryNs) {
      this.primaryNs = primaryNs;
   }

   public void setSecondaryNs(final String secondaryNs) {
      this.secondaryNs = secondaryNs;
   }

   public void setMinTtl(final Integer minTtl) {
      this.minTtl = minTtl;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
