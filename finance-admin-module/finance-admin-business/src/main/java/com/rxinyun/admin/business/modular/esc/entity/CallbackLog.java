package com.rxinyun.admin.business.modular.esc.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

@TableName("esc_callback_log")
public class CallbackLog {
   private String callbackType;
   private String callbackParam;
   private String action;
   private String requestData;
   private String responseData;
   private Date callbackTime;
   private String serverId;
   private Integer status;
   private String errorMsg;

   public String getCallbackType() {
      return this.callbackType;
   }

   public String getCallbackParam() {
      return this.callbackParam;
   }

   public String getAction() {
      return this.action;
   }

   public String getRequestData() {
      return this.requestData;
   }

   public String getResponseData() {
      return this.responseData;
   }

   public Date getCallbackTime() {
      return this.callbackTime;
   }

   public String getServerId() {
      return this.serverId;
   }

   public Integer getStatus() {
      return this.status;
   }

   public String getErrorMsg() {
      return this.errorMsg;
   }

   public void setCallbackType(final String callbackType) {
      this.callbackType = callbackType;
   }

   public void setCallbackParam(final String callbackParam) {
      this.callbackParam = callbackParam;
   }

   public void setAction(final String action) {
      this.action = action;
   }

   public void setRequestData(final String requestData) {
      this.requestData = requestData;
   }

   public void setResponseData(final String responseData) {
      this.responseData = responseData;
   }

   public void setCallbackTime(final Date callbackTime) {
      this.callbackTime = callbackTime;
   }

   public void setServerId(final String serverId) {
      this.serverId = serverId;
   }

   public void setStatus(final Integer status) {
      this.status = status;
   }

   public void setErrorMsg(final String errorMsg) {
      this.errorMsg = errorMsg;
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CallbackLog other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$status = this.getStatus();
         Object other$status = other.getStatus();
         if (this$status == null ? other$status == null : this$status.equals(other$status)) {
            Object this$callbackType = this.getCallbackType();
            Object other$callbackType = other.getCallbackType();
            if (this$callbackType == null ? other$callbackType == null : this$callbackType.equals(other$callbackType)) {
               Object this$callbackParam = this.getCallbackParam();
               Object other$callbackParam = other.getCallbackParam();
               if (this$callbackParam == null ? other$callbackParam == null : this$callbackParam.equals(other$callbackParam)) {
                  Object this$action = this.getAction();
                  Object other$action = other.getAction();
                  if (this$action == null ? other$action == null : this$action.equals(other$action)) {
                     Object this$requestData = this.getRequestData();
                     Object other$requestData = other.getRequestData();
                     if (this$requestData == null ? other$requestData == null : this$requestData.equals(other$requestData)) {
                        Object this$responseData = this.getResponseData();
                        Object other$responseData = other.getResponseData();
                        if (this$responseData == null ? other$responseData == null : this$responseData.equals(other$responseData)) {
                           Object this$callbackTime = this.getCallbackTime();
                           Object other$callbackTime = other.getCallbackTime();
                           if (this$callbackTime == null ? other$callbackTime == null : this$callbackTime.equals(other$callbackTime)) {
                              Object this$serverId = this.getServerId();
                              Object other$serverId = other.getServerId();
                              if (this$serverId == null ? other$serverId == null : this$serverId.equals(other$serverId)) {
                                 Object this$errorMsg = this.getErrorMsg();
                                 Object other$errorMsg = other.getErrorMsg();
                                 return this$errorMsg == null ? other$errorMsg == null : this$errorMsg.equals(other$errorMsg);
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
      return other instanceof CallbackLog;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $status = this.getStatus();
      result = result * 59 + ($status == null ? 43 : $status.hashCode());
      Object $callbackType = this.getCallbackType();
      result = result * 59 + ($callbackType == null ? 43 : $callbackType.hashCode());
      Object $callbackParam = this.getCallbackParam();
      result = result * 59 + ($callbackParam == null ? 43 : $callbackParam.hashCode());
      Object $action = this.getAction();
      result = result * 59 + ($action == null ? 43 : $action.hashCode());
      Object $requestData = this.getRequestData();
      result = result * 59 + ($requestData == null ? 43 : $requestData.hashCode());
      Object $responseData = this.getResponseData();
      result = result * 59 + ($responseData == null ? 43 : $responseData.hashCode());
      Object $callbackTime = this.getCallbackTime();
      result = result * 59 + ($callbackTime == null ? 43 : $callbackTime.hashCode());
      Object $serverId = this.getServerId();
      result = result * 59 + ($serverId == null ? 43 : $serverId.hashCode());
      Object $errorMsg = this.getErrorMsg();
      return result * 59 + ($errorMsg == null ? 43 : $errorMsg.hashCode());
   }

   @Override
   public String toString() {
      return "CallbackLog(callbackType="
         + this.getCallbackType()
         + ", callbackParam="
         + this.getCallbackParam()
         + ", action="
         + this.getAction()
         + ", requestData="
         + this.getRequestData()
         + ", responseData="
         + this.getResponseData()
         + ", callbackTime="
         + this.getCallbackTime()
         + ", serverId="
         + this.getServerId()
         + ", status="
         + this.getStatus()
         + ", errorMsg="
         + this.getErrorMsg()
         + ")";
   }
}
