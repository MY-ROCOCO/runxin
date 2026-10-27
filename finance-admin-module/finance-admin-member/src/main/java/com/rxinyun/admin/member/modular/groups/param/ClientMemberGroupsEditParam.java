package com.rxinyun.admin.member.modular.groups.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClientMemberGroupsEditParam {
   @NotBlank(
      message = "会员组id不能为空"
   )
   @Schema(
      description = "会员组ID"
   )
   private String id;
   @NotBlank(
      message = "会员组名称不能为空"
   )
   @Schema(
      description = "会员组名称 不能重复"
   )
   private String groupName;
   @Schema(
      description = "会员组描述"
   )
   private String groupDescription;
   @NotNull(
      message = "会员组等级不能为空"
   )
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

   public void setPower(final Integer power) {
      this.power = power;
   }
}
