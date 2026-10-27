package com.rxinyun.admin.member.modular.groups.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

@TableName(
   value = "CLIENT_MEMBER_GROUPS",
   autoResultMap = true
)
public class ClientMemberGroups {
   @Schema(
      description = "会员组ID"
   )
   private String id;
   @Schema(
      description = "会员组名称 不能重复"
   )
   private String groupName;
   @Schema(
      description = "会员组描述"
   )
   private String groupDescription;
   @TableField(
      exist = false
   )
   @Schema(
      description = "会员组人数"
   )
   private Long groupNumber;
   @Schema(
      description = "会员组等级"
   )
   private Integer power;

   public String getId() {
      return this.id;
   }

   public String getGroupName() {
      return this.groupName;
   }

   public String getGroupDescription() {
      return this.groupDescription;
   }

   public Long getGroupNumber() {
      return this.groupNumber;
   }

   public Integer getPower() {
      return this.power;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setGroupName(final String groupName) {
      this.groupName = groupName;
   }

   public void setGroupDescription(final String groupDescription) {
      this.groupDescription = groupDescription;
   }

   public void setGroupNumber(final Long groupNumber) {
      this.groupNumber = groupNumber;
   }

   public void setPower(final Integer power) {
      this.power = power;
   }
}
