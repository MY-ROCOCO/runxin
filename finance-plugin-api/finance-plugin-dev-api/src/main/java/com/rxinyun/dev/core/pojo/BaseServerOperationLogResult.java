package com.rxinyun.dev.core.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

public class BaseServerOperationLogResult {
   @Schema(
      description = "日志ID"
   )
   private String id;
   @Schema(
      description = "操作内容"
   )
   private String operationContent;
   @Schema(
      description = "操作类型"
   )
   private String operationType;
   @Schema(
      description = "操作时间"
   )
   private Date operationTime;
   @Schema(
      description = "操作人"
   )
   private String operatorName;
   @Schema(
      description = "操作人身份：管理员/会员"
   )
   private String operatorIdentity;

   public String getId() {
      return this.id;
   }

   public String getOperationContent() {
      return this.operationContent;
   }

   public String getOperationType() {
      return this.operationType;
   }

   public Date getOperationTime() {
      return this.operationTime;
   }

   public String getOperatorName() {
      return this.operatorName;
   }

   public String getOperatorIdentity() {
      return this.operatorIdentity;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setOperationContent(final String operationContent) {
      this.operationContent = operationContent;
   }

   public void setOperationType(final String operationType) {
      this.operationType = operationType;
   }

   public void setOperationTime(final Date operationTime) {
      this.operationTime = operationTime;
   }

   public void setOperatorName(final String operatorName) {
      this.operatorName = operatorName;
   }

   public void setOperatorIdentity(final String operatorIdentity) {
      this.operatorIdentity = operatorIdentity;
   }
}
