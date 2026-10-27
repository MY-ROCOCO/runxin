package com.rxinyun.member.auth.modular.real.param;

import io.swagger.v3.oas.annotations.media.Schema;

public class PersonalRealInfoIdParam {
   @Schema(
      description = "个人认证唯一标识符"
   )
   private String id;
   @Schema(
      description = "用户编号"
   )
   private Long memberNo;

   public String getId() {
      return this.id;
   }

   public Long getMemberNo() {
      return this.memberNo;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberNo(final Long memberNo) {
      this.memberNo = memberNo;
   }
}
