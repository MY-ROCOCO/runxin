package com.rxinyun.member.production.modular.domain.template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

public class XinwangTemplateCreateDto {
   @Schema(
      description = "模板名称",
      requiredMode = RequiredMode.REQUIRED
   )
   private String templateName;
   @Schema(
      description = "用户类型：C-企业，P-个人",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userType;
   @Schema(
      description = "注册所有人中文",
      requiredMode = RequiredMode.REQUIRED
   )
   private String organizeNameCn;
   @Schema(
      description = "注册所有人英文",
      requiredMode = RequiredMode.REQUIRED
   )
   private String organizeNameUk;
   @Schema(
      description = "注册联系人中文",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userNameCn;
   @Schema(
      description = "注册联系人英文-姓",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userSurnameUk;
   @Schema(
      description = "注册联系人英文-名",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userNameUk;
   @Schema(
      description = "注册人英文国家，如 CN",
      requiredMode = RequiredMode.REQUIRED
   )
   private String publicCountryUk;
   @Schema(
      description = "注册人英文省份"
   )
   private String publicProvinceUk;
   @Schema(
      description = "注册人英文城市"
   )
   private String publicCityUk;
   @Schema(
      description = "注册人中文地址",
      requiredMode = RequiredMode.REQUIRED
   )
   private String publicStreetCn;
   @Schema(
      description = "注册人英文地址",
      requiredMode = RequiredMode.REQUIRED
   )
   private String publicStreetUk;
   @Schema(
      description = "注册人邮政编码",
      requiredMode = RequiredMode.REQUIRED
   )
   private String publicZipCode;
   @Schema(
      description = "注册人电话国家码，默认86"
   )
   private String userPhoneInter;
   @Schema(
      description = "注册人电话区号，座机必填"
   )
   private String userPhoneArea;
   @Schema(
      description = "注册人电话号码",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userPhoneNumber;
   @Schema(
      description = "注册人电话分机，默认0"
   )
   private String userPhoneExt;
   @Schema(
      description = "注册人传真国家码，默认86"
   )
   private String userFaxInter;
   @Schema(
      description = "注册人传真区号"
   )
   private String userFaxArea;
   @Schema(
      description = "注册人传真号码",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userFaxNumber;
   @Schema(
      description = "注册人传真分机，默认0"
   )
   private String userFaxExt;
   @Schema(
      description = "注册人邮箱",
      requiredMode = RequiredMode.REQUIRED
   )
   private String userEmail;
   @Schema(
      description = "证件类型，如 SFZ/YYZZ 等，见附录"
   )
   private String idType;
   @Schema(
      description = "注册所有人证件号码",
      requiredMode = RequiredMode.REQUIRED
   )
   private String organCode;
   @Schema(
      description = "注册所有人证件扫描件，jpg 的 base64 编码（提交上游时必填）"
   )
   private String organizeFile;

   public String getTemplateName() {
      return this.templateName;
   }

   public String getUserType() {
      return this.userType;
   }

   public String getOrganizeNameCn() {
      return this.organizeNameCn;
   }

   public String getOrganizeNameUk() {
      return this.organizeNameUk;
   }

   public String getUserNameCn() {
      return this.userNameCn;
   }

   public String getUserSurnameUk() {
      return this.userSurnameUk;
   }

   public String getUserNameUk() {
      return this.userNameUk;
   }

   public String getPublicCountryUk() {
      return this.publicCountryUk;
   }

   public String getPublicProvinceUk() {
      return this.publicProvinceUk;
   }

   public String getPublicCityUk() {
      return this.publicCityUk;
   }

   public String getPublicStreetCn() {
      return this.publicStreetCn;
   }

   public String getPublicStreetUk() {
      return this.publicStreetUk;
   }

   public String getPublicZipCode() {
      return this.publicZipCode;
   }

   public String getUserPhoneInter() {
      return this.userPhoneInter;
   }

   public String getUserPhoneArea() {
      return this.userPhoneArea;
   }

   public String getUserPhoneNumber() {
      return this.userPhoneNumber;
   }

   public String getUserPhoneExt() {
      return this.userPhoneExt;
   }

   public String getUserFaxInter() {
      return this.userFaxInter;
   }

   public String getUserFaxArea() {
      return this.userFaxArea;
   }

   public String getUserFaxNumber() {
      return this.userFaxNumber;
   }

   public String getUserFaxExt() {
      return this.userFaxExt;
   }

   public String getUserEmail() {
      return this.userEmail;
   }

   public String getIdType() {
      return this.idType;
   }

   public String getOrganCode() {
      return this.organCode;
   }

   public String getOrganizeFile() {
      return this.organizeFile;
   }

   public void setTemplateName(final String templateName) {
      this.templateName = templateName;
   }

   public void setUserType(final String userType) {
      this.userType = userType;
   }

   public void setOrganizeNameCn(final String organizeNameCn) {
      this.organizeNameCn = organizeNameCn;
   }

   public void setOrganizeNameUk(final String organizeNameUk) {
      this.organizeNameUk = organizeNameUk;
   }

   public void setUserNameCn(final String userNameCn) {
      this.userNameCn = userNameCn;
   }

   public void setUserSurnameUk(final String userSurnameUk) {
      this.userSurnameUk = userSurnameUk;
   }

   public void setUserNameUk(final String userNameUk) {
      this.userNameUk = userNameUk;
   }

   public void setPublicCountryUk(final String publicCountryUk) {
      this.publicCountryUk = publicCountryUk;
   }

   public void setPublicProvinceUk(final String publicProvinceUk) {
      this.publicProvinceUk = publicProvinceUk;
   }

   public void setPublicCityUk(final String publicCityUk) {
      this.publicCityUk = publicCityUk;
   }

   public void setPublicStreetCn(final String publicStreetCn) {
      this.publicStreetCn = publicStreetCn;
   }

   public void setPublicStreetUk(final String publicStreetUk) {
      this.publicStreetUk = publicStreetUk;
   }

   public void setPublicZipCode(final String publicZipCode) {
      this.publicZipCode = publicZipCode;
   }

   public void setUserPhoneInter(final String userPhoneInter) {
      this.userPhoneInter = userPhoneInter;
   }

   public void setUserPhoneArea(final String userPhoneArea) {
      this.userPhoneArea = userPhoneArea;
   }

   public void setUserPhoneNumber(final String userPhoneNumber) {
      this.userPhoneNumber = userPhoneNumber;
   }

   public void setUserPhoneExt(final String userPhoneExt) {
      this.userPhoneExt = userPhoneExt;
   }

   public void setUserFaxInter(final String userFaxInter) {
      this.userFaxInter = userFaxInter;
   }

   public void setUserFaxArea(final String userFaxArea) {
      this.userFaxArea = userFaxArea;
   }

   public void setUserFaxNumber(final String userFaxNumber) {
      this.userFaxNumber = userFaxNumber;
   }

   public void setUserFaxExt(final String userFaxExt) {
      this.userFaxExt = userFaxExt;
   }

   public void setUserEmail(final String userEmail) {
      this.userEmail = userEmail;
   }

   public void setIdType(final String idType) {
      this.idType = idType;
   }

   public void setOrganCode(final String organCode) {
      this.organCode = organCode;
   }

   public void setOrganizeFile(final String organizeFile) {
      this.organizeFile = organizeFile;
   }
}
