package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "域名注册结果状态"
)
public class DomainCreateStatusVO implements Serializable {
   @Schema(
      description = "新网返回 code，0 表示成功"
   )
   private String code;
   @Schema(
      description = "新网返回 msg"
   )
   private String msg;
   @Schema(
      description = "新网 requestId"
   )
   private String requestId;
   @Schema(
      description = "域名"
   )
   private String domainName;
   @Schema(
      description = "注册状态（新网 state 原值：create-waiting/create-success 等）"
   )
   private String status;
   @Schema(
      description = "注册状态中文：注册中/注册成功 等"
   )
   private String statusLabel;
   @Schema(
      description = "备注"
   )
   private String remark;

   public String getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getStatus() {
      return this.status;
   }

   public String getStatusLabel() {
      return this.statusLabel;
   }

   public String getRemark() {
      return this.remark;
   }

   public void setCode(final String code) {
      this.code = code;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }

   public void setRequestId(final String requestId) {
      this.requestId = requestId;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setStatusLabel(final String statusLabel) {
      this.statusLabel = statusLabel;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }

   public DomainCreateStatusVO() {
   }

   public DomainCreateStatusVO(
      final String code, final String msg, final String requestId, final String domainName, final String status, final String statusLabel, final String remark
   ) {
      this.code = code;
      this.msg = msg;
      this.requestId = requestId;
      this.domainName = domainName;
      this.status = status;
      this.statusLabel = statusLabel;
      this.remark = remark;
   }
}
