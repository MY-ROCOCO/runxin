package com.rxinyun.admin.business.modular.dns.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.List;

public class DnsDomainParam {
   public static class AddParam {
      @NotBlank(
         message = "会员ID不能为空"
      )
      private String memberId;
      @NotBlank(
         message = "套餐实例不能为空"
      )
      private String packageInstanceId;
      @NotBlank(
         message = "域名不能为空"
      )
      private String domainName;
      @Email(
         message = "邮箱格式不正确"
      )
      private String email;
      @Schema(
         description = "状态：1开启 0暂停"
      )
      private Integer status;
      private String remark;

      public String getMemberId() {
         return this.memberId;
      }

      public String getPackageInstanceId() {
         return this.packageInstanceId;
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

      public void setMemberId(final String memberId) {
         this.memberId = memberId;
      }

      public void setPackageInstanceId(final String packageInstanceId) {
         this.packageInstanceId = packageInstanceId;
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

   public static class EditParam {
      @NotBlank(
         message = "id不能为空"
      )
      private String id;
      @Null(
         message = "编辑时不允许修改会员ID"
      )
      @Schema(
         hidden = true
      )
      private String memberId;
      @NotBlank(
         message = "套餐实例不能为空"
      )
      private String packageInstanceId;
      @NotBlank(
         message = "域名不能为空"
      )
      private String domainName;
      @Email(
         message = "邮箱格式不正确"
      )
      private String email;
      @Schema(
         description = "状态：1开启 0暂停"
      )
      private Integer status;
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

   public static class IdParam {
      @NotBlank(
         message = "id不能为空"
      )
      private String id;

      public String getId() {
         return this.id;
      }

      public void setId(final String id) {
         this.id = id;
      }
   }

   public static class NsRecordParam {
      @NotEmpty(
         message = "域名列表不能为空"
      )
      @Schema(
         description = "批量域名列表，例如 runxinyun.com、baidu.com"
      )
      private List<String> domainNames;
      @Schema(
         description = "DNS服务器，默认8.8.8.8"
      )
      private String dnsServer;

      public List<String> getDomainNames() {
         return this.domainNames;
      }

      public String getDnsServer() {
         return this.dnsServer;
      }

      public void setDomainNames(final List<String> domainNames) {
         this.domainNames = domainNames;
      }

      public void setDnsServer(final String dnsServer) {
         this.dnsServer = dnsServer;
      }
   }

   public static class PageParam {
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
         description = "会员ID"
      )
      private String memberId;
      @Schema(
         description = "套餐实例ID"
      )
      private String packageInstanceId;
      @Schema(
         description = "状态：1开启 0暂停"
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

      public String getMemberId() {
         return this.memberId;
      }

      public String getPackageInstanceId() {
         return this.packageInstanceId;
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

      public void setMemberId(final String memberId) {
         this.memberId = memberId;
      }

      public void setPackageInstanceId(final String packageInstanceId) {
         this.packageInstanceId = packageInstanceId;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }
   }

   public static class StatusParam extends DnsDomainParam.IdParam {
      @NotNull(
         message = "状态不能为空"
      )
      @Schema(
         description = "状态：1开启 0暂停"
      )
      private Integer status;

      public Integer getStatus() {
         return this.status;
      }

      public void setStatus(final Integer status) {
         this.status = status;
      }
   }
}
