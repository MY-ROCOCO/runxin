package com.rxinyun.admin.member.modular.groups.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class ClientGroupIdParam implements Serializable {
   @Schema(
      description = "会员组ID"
   )
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(final String id) {
      this.id = id;
   }
}
