package com.rxinyun.member.auth.modular.real.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class CompanyRealInfoAddOrUpdateParam {
   @Schema(
      description = "认证方式，枚举类型，可选值为 ALIPAY"
   )
   private String authType;
   @Schema(
      description = "用户编号"
   )
   private Long memberNo;
   @Schema(
      description = "企业名称，必填"
   )
   private String companyName;
   @Schema(
      description = "统一社会信用代码，必填且唯一"
   )
   private String businessLicenseNumber;
   @Schema(
      description = "营业执照图片，必填"
   )
   private String businessLicenseImage;
   @Schema(
      description = "注册地址，必填"
   )
   private String registeredAddress;
   @Schema(
      description = "法定代表人姓名，必填"
   )
   private String legalRepresentative;
   @Schema(
      description = "法人身份证号，必填"
   )
   private String legalRepresentativeNumber;
   @Schema(
      description = "负责人姓名，必填"
   )
   private String contactPerson;
   @Schema(
      description = "负责人身份证号，必填"
   )
   private String contactPersonNumber;
   @Schema(
      description = "联系电话，可选"
   )
   private String contactPhone;
   @Schema(
      description = "电子邮件，可选"
   )
   private String email;
   @Schema(
      description = "授权书图片，可选"
   )
   private String authorizationImage;
   @Schema(
      description = "认证状态，枚举类型，可选值为 PENDING, APPROVED, REJECTED"
   )
   private String status;
   @Schema(
      description = "认证失败原因，如果状态为 REJECTED，则填写"
   )
   private String reason;

   public String getAuthType() {
      return this.authType;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getCompanyName() {
      return this.companyName;
   }

   public String getBusinessLicenseNumber() {
      return this.businessLicenseNumber;
   }

   public String getBusinessLicenseImage() {
      return this.businessLicenseImage;
   }

   public String getRegisteredAddress() {
      return this.registeredAddress;
   }

   public String getLegalRepresentative() {
      return this.legalRepresentative;
   }

   public String getLegalRepresentativeNumber() {
      return this.legalRepresentativeNumber;
   }

   public String getContactPerson() {
      return this.contactPerson;
   }

   public String getContactPersonNumber() {
      return this.contactPersonNumber;
   }

   public String getContactPhone() {
      return this.contactPhone;
   }

   public String getEmail() {
      return this.email;
   }

   public String getAuthorizationImage() {
      return this.authorizationImage;
   }

   public String getStatus() {
      return this.status;
   }

   public String getReason() {
      return this.reason;
   }

   public void setAuthType(final String authType) {
      this.authType = authType;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setCompanyName(final String companyName) {
      this.companyName = companyName;
   }

   public void setBusinessLicenseNumber(final String businessLicenseNumber) {
      this.businessLicenseNumber = businessLicenseNumber;
   }

   public void setBusinessLicenseImage(final String businessLicenseImage) {
      this.businessLicenseImage = businessLicenseImage;
   }

   public void setRegisteredAddress(final String registeredAddress) {
      this.registeredAddress = registeredAddress;
   }

   public void setLegalRepresentative(final String legalRepresentative) {
      this.legalRepresentative = legalRepresentative;
   }

   public void setLegalRepresentativeNumber(final String legalRepresentativeNumber) {
      this.legalRepresentativeNumber = legalRepresentativeNumber;
   }

   public void setContactPerson(final String contactPerson) {
      this.contactPerson = contactPerson;
   }

   public void setContactPersonNumber(final String contactPersonNumber) {
      this.contactPersonNumber = contactPersonNumber;
   }

   public void setContactPhone(final String contactPhone) {
      this.contactPhone = contactPhone;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setAuthorizationImage(final String authorizationImage) {
      this.authorizationImage = authorizationImage;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setReason(final String reason) {
      this.reason = reason;
   }
}
