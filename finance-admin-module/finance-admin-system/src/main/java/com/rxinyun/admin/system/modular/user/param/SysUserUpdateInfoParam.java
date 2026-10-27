package com.rxinyun.admin.system.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;

public class SysUserUpdateInfoParam {
   @Schema(
      description = "id",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "id不能为空"
   )
   private String id;
   @Schema(
      description = "姓名",
      requiredMode = RequiredMode.REQUIRED
   )
   @NotBlank(
      message = "name不能为空"
   )
   private String name;
   @Schema(
      description = "手机"
   )
   private String phone;
   @Schema(
      description = "昵称"
   )
   private String nickname;
   @Schema(
      description = "性别"
   )
   private String gender;
   @Schema(
      description = "出生日期"
   )
   private String birthday;
   @Schema(
      description = "邮箱"
   )
   private String email;
   @Schema(
      description = "签名，图片base64"
   )
   private String signature;

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public String getPhone() {
      return this.phone;
   }

   public String getNickname() {
      return this.nickname;
   }

   public String getGender() {
      return this.gender;
   }

   public String getBirthday() {
      return this.birthday;
   }

   public String getEmail() {
      return this.email;
   }

   public String getSignature() {
      return this.signature;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setPhone(final String phone) {
      this.phone = phone;
   }

   public void setNickname(final String nickname) {
      this.nickname = nickname;
   }

   public void setGender(final String gender) {
      this.gender = gender;
   }

   public void setBirthday(final String birthday) {
      this.birthday = birthday;
   }

   public void setEmail(final String email) {
      this.email = email;
   }

   public void setSignature(final String signature) {
      this.signature = signature;
   }
}
