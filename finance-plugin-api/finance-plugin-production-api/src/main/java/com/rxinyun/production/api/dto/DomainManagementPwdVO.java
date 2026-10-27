package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "域名管理密码"
)
public class DomainManagementPwdVO implements Serializable {
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
      description = "管理密码"
   )
   private String password;

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

   public String getPassword() {
      return this.password;
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

   public void setPassword(final String password) {
      this.password = password;
   }

   public DomainManagementPwdVO() {
   }

   public DomainManagementPwdVO(final String code, final String msg, final String requestId, final String domainName, final String password) {
      this.code = code;
      this.msg = msg;
      this.requestId = requestId;
      this.domainName = domainName;
      this.password = password;
   }
}
