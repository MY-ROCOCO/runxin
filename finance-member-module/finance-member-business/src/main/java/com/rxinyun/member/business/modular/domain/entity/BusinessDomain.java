package com.rxinyun.member.business.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "BUSINESS_DOMAIN",
   autoResultMap = true
)
public class BusinessDomain extends CommonEntity {
   @Schema(
      description = "ID"
   )
   private String id;
   @Schema(
      description = "会员ID"
   )
   private String memberId;
   @Schema(
      description = "域名"
   )
   private String domainName;
   @Schema(
      description = "后缀"
   )
   private String tld;
   @Schema(
      description = "接口商，如 XINWANG"
   )
   private String registrar;
   @Schema(
      description = "状态：1=正常 0=即将到期 -1=已过期 2=锁定"
   )
   private Integer status;
   @Schema(
      description = "到期时间"
   )
   private Date expireTime;
   @Schema(
      description = "关联域名模板ID"
   )
   private String templateId;
   @TableField(
      exist = false
   )
   @Schema(
      description = "模板名称（关联查询，不落库）"
   )
   private String templateName;
   @Schema(
      description = "备注"
   )
   private String remark;
   @Schema(
      description = "注册状态（新网 state：create-waiting=注册中 create-success=注册成功，点击查询后回写）"
   )
   private String registrationStatus;

   public String getId() {
      return this.id;
   }

   public String getMemberId() {
      return this.memberId;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getTld() {
      return this.tld;
   }

   public String getRegistrar() {
      return this.registrar;
   }

   public Integer getStatus() {
      return this.status;
   }

   public Date getExpireTime() {
      return this.expireTime;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public String getRemark() {
      return this.remark;
   }

   public String getRegistrationStatus() {
      return this.registrationStatus;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberId(final String memberId) {
      this.memberId = memberId;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setTld(final String tld) {
      this.tld = tld;
   }

   public void setRegistrar(final String registrar) {
      this.registrar = registrar;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setExpireTime(final Date expireTime) {
      this.expireTime = expireTime;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public void setRegistrationStatus(final String registrationStatus) {
      this.registrationStatus = registrationStatus;
   }
}
