package com.rxinyun.admin.production.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DnsAdminParam {
   public static class BasePageParam {
      @Schema(
         description = "当前页码"
      )
      private Integer current;
      @Schema(
         description = "每页条数"
      )
      private Integer size;
      @Schema(
         description = "排序字段，字段驼峰名称"
      )
      private String sortField;
      @Schema(
         description = "排序方式，升序：ASCEND；降序：DESCEND"
      )
      private String sortOrder;
      @Schema(
         description = "关键词"
      )
      private String searchKey;
      @Schema(
         description = "状态"
      )
      private Integer status;

      public Integer getCurrent() {
         return this.current;
      }

      public Integer getSize() {
         return this.size;
      }

      public String getSortField() {
         return this.sortField;
      }

      public String getSortOrder() {
         return this.sortOrder;
      }

      public String getSearchKey() {
         return this.searchKey;
      }

      public Integer getStatus() {
         return this.status;
      }

      public void setCurrent(final Integer current) {
         this.current = current;
      }

      public void setSize(final Integer size) {
         this.size = size;
      }

      public void setSortField(final String sortField) {
         this.sortField = sortField;
      }

      public void setSortOrder(final String sortOrder) {
         this.sortOrder = sortOrder;
      }

      public void setSearchKey(final String searchKey) {
         this.searchKey = searchKey;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }
   }

   public static class IdParam {
      @NotBlank(
         message = "id不能为空"
      )
      @Schema(
         description = "主键ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class LineGroupAddParam {
      @NotBlank(
         message = "名称不能为空"
      )
      @Schema(
         description = "线路分组名称",
         requiredMode = RequiredMode.REQUIRED
      )
      private String name;
      @Schema(
         description = "状态：1开启 0关闭"
      )
      private Integer status;
      @Schema(
         description = "备注"
      )
      private String remark;
      @Schema(
         description = "绑定的DNS节点ID列表"
      )
      private List<String> nodeIds;

      public String getName() {
         return this.name;
      }

      public Integer getStatus() {
         return this.status;
      }

      public String getRemark() {
         return this.remark;
      }

      public List<String> getNodeIds() {
         return this.nodeIds;
      }

      public void setName(final String name) {
         this.name = name;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }

      public void setNodeIds(final List<String> nodeIds) {
         this.nodeIds = nodeIds;
      }
   }

   public static class LineGroupEditParam extends DnsAdminParam.LineGroupAddParam {
      @NotBlank(
         message = "id不能为空"
      )
      @Schema(
         description = "主键ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class NodeAddParam {
      @NotBlank(
         message = "节点名称不能为空"
      )
      private String name;
      @NotBlank(
         message = "节点API地址不能为空"
      )
      private String apiUrl;
      @NotBlank(
         message = "Java服务回调地址不能为空"
      )
      private String callbackUrl;
      private Integer status;
      private String remark;

      public String getName() {
         return this.name;
      }

      public String getApiUrl() {
         return this.apiUrl;
      }

      public String getCallbackUrl() {
         return this.callbackUrl;
      }

      public Integer getStatus() {
         return this.status;
      }

      public String getRemark() {
         return this.remark;
      }

      public void setName(final String name) {
         this.name = name;
      }

      public void setApiUrl(final String apiUrl) {
         this.apiUrl = apiUrl;
      }

      public void setCallbackUrl(final String callbackUrl) {
         this.callbackUrl = callbackUrl;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }
   }

   public static class NodeCallbackLogPageParam extends DnsAdminParam.BasePageParam {
      @Schema(
         description = "DNS节点任务ID"
      )
      private String taskId;
      @Schema(
         description = "请求唯一标识"
      )
      private String requestId;
      @Schema(
         description = "DNS节点ID"
      )
      private String nodeId;
      @Schema(
         description = "节点侧任务ID"
      )
      private String nodeTaskId;
      @Schema(
         description = "事件类型：NODE_INSTALLED、TASK_RESULT"
      )
      private String eventType;
      @Schema(
         description = "回调处理状态：1成功 0失败"
      )
      private Integer processStatus;

      public String getTaskId() {
         return this.taskId;
      }

      public String getRequestId() {
         return this.requestId;
      }

      public String getNodeId() {
         return this.nodeId;
      }

      public String getNodeTaskId() {
         return this.nodeTaskId;
      }

      public String getEventType() {
         return this.eventType;
      }

      public Integer getProcessStatus() {
         return this.processStatus;
      }

      public void setTaskId(final String taskId) {
         this.taskId = taskId;
      }

      public void setRequestId(final String requestId) {
         this.requestId = requestId;
      }

      public void setNodeId(final String nodeId) {
         this.nodeId = nodeId;
      }

      public void setNodeTaskId(final String nodeTaskId) {
         this.nodeTaskId = nodeTaskId;
      }

      public void setEventType(final String eventType) {
         this.eventType = eventType;
      }

      public void setProcessStatus(final Integer processStatus) {
         this.processStatus = processStatus;
      }
   }

   public static class NodeEditParam {
      @NotBlank(
         message = "id不能为空"
      )
      private String id;
      @NotBlank(
         message = "节点名称不能为空"
      )
      private String name;
      @NotBlank(
         message = "节点API地址不能为空"
      )
      private String apiUrl;
      @NotBlank(
         message = "Java服务回调地址不能为空"
      )
      private String callbackUrl;
      private Integer status;
      private String remark;

      public String getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public String getApiUrl() {
         return this.apiUrl;
      }

      public String getCallbackUrl() {
         return this.callbackUrl;
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

      public void setName(final String name) {
         this.name = name;
      }

      public void setApiUrl(final String apiUrl) {
         this.apiUrl = apiUrl;
      }

      public void setCallbackUrl(final String callbackUrl) {
         this.callbackUrl = callbackUrl;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }
   }

   public static class NodeTaskPageParam extends DnsAdminParam.BasePageParam {
      @Schema(
         description = "DNS节点ID"
      )
      private String nodeId;
      @Schema(
         description = "业务类型：DOMAIN、RECORD"
      )
      private String bizType;
      @Schema(
         description = "业务ID"
      )
      private String bizId;
      @Schema(
         description = "任务动作"
      )
      private String action;
      @Schema(
         description = "任务状态：1待发送 2执行中 3执行成功 -1执行失败 -2已取消"
      )
      private Integer taskStatus;

      public String getNodeId() {
         return this.nodeId;
      }

      public String getBizType() {
         return this.bizType;
      }

      public String getBizId() {
         return this.bizId;
      }

      public String getAction() {
         return this.action;
      }

      public Integer getTaskStatus() {
         return this.taskStatus;
      }

      public void setNodeId(final String nodeId) {
         this.nodeId = nodeId;
      }

      public void setBizType(final String bizType) {
         this.bizType = bizType;
      }

      public void setBizId(final String bizId) {
         this.bizId = bizId;
      }

      public void setAction(final String action) {
         this.action = action;
      }

      public void setTaskStatus(final Integer taskStatus) {
         this.taskStatus = taskStatus;
      }
   }

   public static class PackageAddParam {
      @NotBlank(
         message = "套餐名称不能为空"
      )
      @Schema(
         description = "DNS套餐名称",
         requiredMode = RequiredMode.REQUIRED
      )
      private String name;
      @NotBlank(
         message = "线路分组不能为空"
      )
      @Schema(
         description = "绑定的线路分组ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String lineGroupId;
      @NotNull(
         message = "单域名月价格不能为空"
      )
      @DecimalMin(
         value = "0.00",
         inclusive = false,
         message = "单域名月价格必须大于0"
      )
      @Schema(
         description = "单个托管域名的月价格，必须大于0",
         requiredMode = RequiredMode.REQUIRED
      )
      private BigDecimal singleDomainPrice;
      @NotNull(
         message = "最低TTL不能为空"
      )
      @Min(
         value = 1L,
         message = "最低TTL必须大于0"
      )
      @Schema(
         description = "套餐允许设置的最低TTL，单位秒，必须大于0",
         requiredMode = RequiredMode.REQUIRED
      )
      private Integer minTtl;
      @NotNull(
         message = "默认域名配额不能为空"
      )
      @Min(
         value = 1L,
         message = "默认域名配额必须大于0"
      )
      @Schema(
         description = "默认可托管域名数量配额，必须大于0",
         requiredMode = RequiredMode.REQUIRED
      )
      private Integer defaultDomainQuota;
      @Schema(
         description = "主NS域名"
      )
      private String primaryNs;
      @Schema(
         description = "备NS域名"
      )
      private String secondaryNs;
      @Schema(
         description = "状态：1开启 0关闭"
      )
      private Integer status;
      @Schema(
         description = "备注"
      )
      private String remark;

      public String getName() {
         return this.name;
      }

      public String getLineGroupId() {
         return this.lineGroupId;
      }

      public BigDecimal getSingleDomainPrice() {
         return this.singleDomainPrice;
      }

      public Integer getMinTtl() {
         return this.minTtl;
      }

      public Integer getDefaultDomainQuota() {
         return this.defaultDomainQuota;
      }

      public String getPrimaryNs() {
         return this.primaryNs;
      }

      public String getSecondaryNs() {
         return this.secondaryNs;
      }

      public Integer getStatus() {
         return this.status;
      }

      public String getRemark() {
         return this.remark;
      }

      public void setName(final String name) {
         this.name = name;
      }

      public void setLineGroupId(final String lineGroupId) {
         this.lineGroupId = lineGroupId;
      }

      public void setSingleDomainPrice(final BigDecimal singleDomainPrice) {
         this.singleDomainPrice = singleDomainPrice;
      }

      public void setMinTtl(final Integer minTtl) {
         this.minTtl = minTtl;
      }

      public void setDefaultDomainQuota(final Integer defaultDomainQuota) {
         this.defaultDomainQuota = defaultDomainQuota;
      }

      public void setPrimaryNs(final String primaryNs) {
         this.primaryNs = primaryNs;
      }

      public void setSecondaryNs(final String secondaryNs) {
         this.secondaryNs = secondaryNs;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }
   }

   public static class PackageEditParam extends DnsAdminParam.PackageAddParam {
      @NotBlank(
         message = "id不能为空"
      )
      @Schema(
         description = "主键ID",
         requiredMode = RequiredMode.REQUIRED
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class PackageInstanceAddParam {
      @NotBlank(
         message = "会员ID不能为空"
      )
      private String memberId;
      @NotBlank(
         message = "套餐模板不能为空"
      )
      private String packageId;
      @Min(
         value = 1L,
         message = "域名配额必须大于0"
      )
      private Integer domainQuota;
      @Min(
         value = 1L,
         message = "周期月数必须大于0"
      )
      private Integer periodMonths;
      private Date startTime;
      private String sourceOrderNo;
      private Integer status;
      private String remark;

      public String getMemberId() {
         return this.memberId;
      }

      public String getPackageId() {
         return this.packageId;
      }

      public Integer getDomainQuota() {
         return this.domainQuota;
      }

      public Integer getPeriodMonths() {
         return this.periodMonths;
      }

      public Date getStartTime() {
         return this.startTime;
      }

      public String getSourceOrderNo() {
         return this.sourceOrderNo;
      }

      public Integer getStatus() {
         return this.status;
      }

      public String getRemark() {
         return this.remark;
      }

      public void setMemberId(final String memberId) {
         this.memberId = memberId;
      }

      public void setPackageId(final String packageId) {
         this.packageId = packageId;
      }

      public void setDomainQuota(final Integer domainQuota) {
         this.domainQuota = domainQuota;
      }

      public void setPeriodMonths(final Integer periodMonths) {
         this.periodMonths = periodMonths;
      }

      public void setStartTime(final Date startTime) {
         this.startTime = startTime;
      }

      public void setSourceOrderNo(final String sourceOrderNo) {
         this.sourceOrderNo = sourceOrderNo;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }
   }

   public static class PackageInstanceEditParam {
      @NotBlank(
         message = "id不能为空"
      )
      private String id;
      @Min(
         value = 1L,
         message = "域名配额必须大于0"
      )
      private Integer domainQuota;
      @Min(
         value = 1L,
         message = "周期月数必须大于0"
      )
      private Integer periodMonths;
      private Date nextDueTime;
      private Integer status;
      private String remark;

      public String getId() {
         return this.id;
      }

      public Integer getDomainQuota() {
         return this.domainQuota;
      }

      public Integer getPeriodMonths() {
         return this.periodMonths;
      }

      public Date getNextDueTime() {
         return this.nextDueTime;
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

      public void setDomainQuota(final Integer domainQuota) {
         this.domainQuota = domainQuota;
      }

      public void setPeriodMonths(final Integer periodMonths) {
         this.periodMonths = periodMonths;
      }

      public void setNextDueTime(final Date nextDueTime) {
         this.nextDueTime = nextDueTime;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }

      public void setRemark(final String remark) {
         this.remark = remark;
      }
   }

   public static class PackageInstancePageParam extends DnsAdminParam.BasePageParam {
      @Schema(
         description = "会员ID"
      )
      private String memberId;
      @Schema(
         description = "套餐模板ID"
      )
      private String packageId;

      public String getMemberId() {
         return this.memberId;
      }

      public String getPackageId() {
         return this.packageId;
      }

      public void setMemberId(final String memberId) {
         this.memberId = memberId;
      }

      public void setPackageId(final String packageId) {
         this.packageId = packageId;
      }
   }

   public static class StatusParam extends DnsAdminParam.IdParam {
      @NotNull(
         message = "状态不能为空"
      )
      @Schema(
         description = "状态"
      )
      private Integer status;

      public Integer getStatus() {
         return this.status;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }
   }

   public static class TokenResetParam extends DnsAdminParam.IdParam {
      @Size(
         min = 16,
         message = "Token至少16位"
      )
      @Schema(
         description = "自定义Token，不传则自动生成"
      )
      private String token;

      public String getToken() {
         return this.token;
      }

      public void setToken(final String token) {
         this.token = token;
      }
   }
}
