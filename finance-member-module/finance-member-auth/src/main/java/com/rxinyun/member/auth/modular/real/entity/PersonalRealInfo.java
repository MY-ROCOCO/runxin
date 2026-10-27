package com.rxinyun.member.auth.modular.real.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@TableName(
   value = "PERSONAL_REAL_INFO",
   autoResultMap = true
)
public class PersonalRealInfo extends CommonEntity {
   @Schema(
      description = "唯一标识符"
   )
   private String id;
   @Schema(
      description = "用户编号"
   )
   private Long memberNo;
   @Schema(
      description = "姓名"
   )
   private String name;
   @Schema(
      description = "认证方式，枚举类型，可选值为 ALIPAY"
   )
   private String authType;
   @Schema(
      description = "证件类型：身份证：IDENTITY_CARD"
   )
   private String certType;
   @Schema(
      description = "身份证号码，必填且唯一"
   )
   private String certNumber;
   @Schema(
      description = "性别，枚举类型，可选值为 MALE 和 FEMALE"
   )
   private String gender;
   @Schema(
      description = "出生日期，可选"
   )
   private Date birthday;
   @Schema(
      description = "地址"
   )
   private String address;
   @Schema(
      description = "手机号"
   )
   private String phoneNumber;
   @Schema(
      description = "邮箱"
   )
   private String email;
   @Schema(
      description = "认证状态，枚举类型，待认证: PENDING, 已认证： APPROVED"
   )
   private String status;
   @Schema(
      description = "认证失败原因，如果状态为 REJECTED，则填写"
   )
   private String reason;

   public String getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public String getName() {
      return this.name;
   }

   public String getAuthType() {
      return this.authType;
   }

   public String getCertType() {
      return this.certType;
   }

   public String getCertNumber() {
      return this.certNumber;
   }

   public String getGender() {
      return this.gender;
   }

   public Date getBirthday() {
      return this.birthday;
   }

   public String getAddress() {
      return this.address;
   }

   public String getPhoneNumber() {
      return this.phoneNumber;
   }

   public String getEmail() {
      return this.email;
   }

   public String getStatus() {
      return this.status;
   }

   public String getReason() {
      return this.reason;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setAuthType(final String authType) {
      this.authType = authType;
   }

   public void setCertType(final String certType) {
      this.certType = certType;
   }

   public void setCertNumber(final String certNumber) {
      this.certNumber = certNumber;
   }

   public void setGender(final String gender) {
      this.gender = gender;
   }

   public void setBirthday(final Date birthday) {
      this.birthday = birthday;
   }

   public void setAddress(final String address) {
      this.address = address;
   }

   public void setPhoneNumber(final String phoneNumber) {
      this.phoneNumber = phoneNumber;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setStatus(final String status) {
      this.status = status;
   }

   public void setReason(final String reason) {
      this.reason = reason;
   }
}
