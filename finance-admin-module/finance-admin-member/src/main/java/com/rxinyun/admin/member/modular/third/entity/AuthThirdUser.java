package com.rxinyun.admin.member.modular.third.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.rxinyun.common.pojo.CommonEntity;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName("AUTH_THIRD_USER")
public class AuthThirdUser extends CommonEntity {
   @Schema(
      description = "id"
   )
   private String id;
   @Schema(
      description = "三方用户id"
   )
   private String thirdId;
   @Schema(
      description = "系统用户id"
   )
   private Long userId;
   @Schema(
      description = "头像"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String avatar;
   @Schema(
      description = "姓名"
   )
   private String name;
   @Schema(
      description = "昵称"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String nickname;
   @Schema(
      description = "性别"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String gender;
   @Schema(
      description = "分类"
   )
   private String category;
   @Schema(
      description = "扩展信息"
   )
   @TableField(
      insertStrategy = FieldStrategy.IGNORED,
      updateStrategy = FieldStrategy.IGNORED
   )
   private String extJson;

   public String getId() {
      return this.id;
   }

   public String getThirdId() {
      return this.thirdId;
   }

   public Long getUserId() {
      return this.userId;
   }

   public String getAvatar() {
      return this.avatar;
   }

   public String getName() {
      return this.name;
   }

   public String getNickname() {
      return this.nickname;
   }

   public String getGender() {
      return this.gender;
   }

   public String getCategory() {
      return this.category;
   }

   public String getExtJson() {
      return this.extJson;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setThirdId(final String thirdId) {
      this.thirdId = thirdId;
   }

   public void setUserId(final Long userId) {
      this.userId = userId;
   }

   public void setAvatar(final String avatar) {
      this.avatar = avatar;
   }

   public void setName(final String name) {
      this.name = name;
   }

   public void setNickname(final String nickname) {
      this.nickname = nickname;
   }

   public void setGender(final String gender) {
      this.gender = gender;
   }

   public void setCategory(final String category) {
      this.category = category;
   }

   public void setExtJson(final String extJson) {
      this.extJson = extJson;
   }
}
