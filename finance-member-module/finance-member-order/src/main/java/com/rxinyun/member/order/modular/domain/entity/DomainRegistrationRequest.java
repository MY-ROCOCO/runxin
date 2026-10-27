package com.rxinyun.member.order.modular.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "domain_registration_request",
   autoResultMap = true
)
@Schema(
   description = "域名上游注册请求记录"
)
public class DomainRegistrationRequest {
   @Schema(
      description = "主键"
   )
   private String id;
   @Schema(
      description = "订单编号"
   )
   private String orderNo;
   @Schema(
      description = "域名"
   )
   private String domainName;
   @Schema(
      description = "模板ID"
   )
   private String templateId;
   @Schema(
      description = "新网模板名称（请求参数，重试时仅读本表）"
   )
   private String templateName;
   @Schema(
      description = "注册年限"
   )
   private Integer years;
   @Schema(
      description = "上游请求ID"
   )
   private String upstreamRequestId;
   @Schema(
      description = "状态"
   )
   private Integer status;
   @Schema(
      description = "上游返回 code"
   )
   private String upstreamCode;
   @Schema(
      description = "上游返回 msg"
   )
   private String upstreamMsg;
   private Date createTime;
   private Date updateTime;

   public String getId() {
      return this.id;
   }

   public String getOrderNo() {
      return this.orderNo;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public Integer getYears() {
      return this.years;
   }

   public String getUpstreamRequestId() {
      return this.upstreamRequestId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getUpstreamCode() {
      return this.upstreamCode;
   }

   public String getUpstreamMsg() {
      return this.upstreamMsg;
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

   public void setOrderNo(final String orderNo) {
      this.orderNo = orderNo;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setTemplateId(final String templateId) {
      this.templateId = templateId;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setYears(final Integer years) {
      this.years = years;
   }

   public void setUpstreamRequestId(final String upstreamRequestId) {
      this.upstreamRequestId = upstreamRequestId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setUpstreamCode(final String upstreamCode) {
      this.upstreamCode = upstreamCode;
   }

   public void setUpstreamMsg(final String upstreamMsg) {
      this.upstreamMsg = upstreamMsg;
   }

   public void setCreateTime(final Date createTime) {
      this.createTime = createTime;
   }

   public void setUpdateTime(final Date updateTime) {
      this.updateTime = updateTime;
   }
}
