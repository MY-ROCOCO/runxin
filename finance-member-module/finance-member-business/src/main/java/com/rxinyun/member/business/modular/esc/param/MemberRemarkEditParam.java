package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class MemberRemarkEditParam implements Serializable {
   @Schema(
      description = "云服务器ID"
   )
   String id;
   @Schema(
      description = "会员备注"
   )
   String memberRemark;

   public String getId() {
      return this.id;
   }

   public String getMemberRemark() {
      return this.memberRemark;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setMemberRemark(final String memberRemark) {
      this.memberRemark = memberRemark;
   }
}
