package com.rxinyun.production.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "提交域名模板审核结果"
)
public class DomainTemplateSubmitResultVO implements Serializable {
   @Schema(
      description = "上游返回 code，0 表示成功"
   )
   private String code;
   @Schema(
      description = "上游返回 msg"
   )
   private String msg;
   @Schema(
      description = "请求ID"
   )
   private String requestId;
   @Schema(
      description = "是否提交成功"
   )
   private Boolean success;

   public String getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public String getRequestId() {
      return this.requestId;
   }

   public Boolean getSuccess() {
      return this.success;
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

   public void setSuccess(final Boolean success) {
      this.success = success;
   }

   public DomainTemplateSubmitResultVO() {
   }

   public DomainTemplateSubmitResultVO(final String code, final String msg, final String requestId, final Boolean success) {
      this.code = code;
      this.msg = msg;
      this.requestId = requestId;
      this.success = success;
   }
}
