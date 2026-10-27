package com.rxinyun.admin.member.modular.user.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class UpdateMemberGroupParam {
   @Schema(
      description = "会员ID"
   )
   private Long id;
   @Schema(
      description = "要变更的用户组ID"
   )
   private String groupId;

   public Long getId() {
      return this.id;
   }

   public String getGroupId() {
      return this.groupId;
   }

   public void setId(final Long id) {
      this.id = id;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }
}
