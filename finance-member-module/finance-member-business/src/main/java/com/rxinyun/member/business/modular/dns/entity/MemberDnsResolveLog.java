package com.rxinyun.member.business.modular.dns.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "DNS_RESOLVE_LOG",
   autoResultMap = true
)
@Schema(
   description = "会员端-DNS解析记录操作日志"
)
public class MemberDnsResolveLog extends CommonEntity {
   @Schema(
      description = "主键ID"
   )
   private String id;
   @Schema(
      description = "会员ID"
   )
   private String memberId;
   @Schema(
      description = "托管域名ID"
   )
   private String zoneId;
   @Schema(
      description = "解析记录ID"
   )
   private String recordId;
   @Schema(
      description = "托管域名"
   )
   private String domainName;
   @Schema(
      description = "完整解析记录，如 www.example.com"
   )
   private String recordName;
   @Schema(
      description = "记录类型，如 A/AAAA/CNAME/MX/NS/TXT"
   )
   private String recordType;
   @Schema(
      description = "解析记录值"
   )
   private String recordValue;
   @Schema(
      description = "TTL时间，单位秒"
   )
   private Integer ttl;
   @Schema(
      description = "MX优先级"
   )
   private Integer priority;
   @Schema(
      description = "解析线路"
   )
   private String line;
   @Schema(
      description = "是否启用：1启用 0暂停"
   )
   private Integer enabled;
   @Schema(
      description = "操作类型：ADD新增 EDIT编辑 STATUS状态 DELETE删除"
   )
   private String operateType;
   @Schema(
      description = "操作来源：MEMBER会员端 ADMIN后台"
   )
   private String operateSource;
   @Schema(
      description = "操作人ID"
   )
   private String operateUser;
   @Schema(
      description = "操作前记录JSON"
   )
   private String beforeContent;
   @Schema(
      description = "操作后记录JSON"
   )
   private String afterContent;
   @Schema(
      description = "操作时间"
   )
   private Date operateTime;
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

   public String getZoneId() {
      return this.zoneId;
   }

   public String getRecordId() {
      return this.recordId;
   }

   public String getDomainName() {
      return this.domainName;
   }

   public String getRecordName() {
      return this.recordName;
   }

   public String getRecordType() {
      return this.recordType;
   }

   public String getRecordValue() {
      return this.recordValue;
   }

   public Integer getTtl() {
      return this.ttl;
   }

   public Integer getPriority() {
      return this.priority;
   }

   public String getLine() {
      return this.line;
   }

   public Integer getEnabled() {
      return this.enabled;
   }

   public String getOperateType() {
      return this.operateType;
   }

   public String getOperateSource() {
      return this.operateSource;
   }

   public String getOperateUser() {
      return this.operateUser;
   }

   public String getBeforeContent() {
      return this.beforeContent;
   }

   public String getAfterContent() {
      return this.afterContent;
   }

   public Date getOperateTime() {
      return this.operateTime;
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

   public void setZoneId(final String zoneId) {
      this.zoneId = zoneId;
   }

   public void setRecordId(final String recordId) {
      this.recordId = recordId;
   }

   public void setDomainName(final String domainName) {
      this.domainName = domainName;
   }

   public void setRecordName(final String recordName) {
      this.recordName = recordName;
   }

   public void setRecordType(final String recordType) {
      this.recordType = recordType;
   }

   public void setRecordValue(final String recordValue) {
      this.recordValue = recordValue;
   }

   public void setTtl(final Integer ttl) {
      this.ttl = ttl;
   }

   public void setPriority(final Integer priority) {
      this.priority = priority;
   }

   public void setLine(final String line) {
      this.line = line;
   }

   public void setEnabled(final Integer enabled) {
      this.enabled = enabled;
   }

   public void setOperateType(final String operateType) {
      this.operateType = operateType;
   }

   public void setOperateSource(final String operateSource) {
      this.operateSource = operateSource;
   }

   public void setOperateUser(final String operateUser) {
      this.operateUser = operateUser;
   }

   public void setBeforeContent(final String beforeContent) {
      this.beforeContent = beforeContent;
   }

   public void setAfterContent(final String afterContent) {
      this.afterContent = afterContent;
   }

   public void setOperateTime(final Date operateTime) {
      this.operateTime = operateTime;
   }

   public void setRemark(final String remark) {
      this.remark = remark;
   }
}
