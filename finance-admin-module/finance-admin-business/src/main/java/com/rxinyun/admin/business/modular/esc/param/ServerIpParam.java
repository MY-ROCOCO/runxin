package com.rxinyun.admin.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class ServerIpParam {
   @Schema(
      description = "服务器标识"
   )
   private String id;
   @Schema(
      description = "公网IP数组"
   )
   private List<String> publicNetworkIdList;

   public String getId() {
      return this.id;
   }

   public List<String> getPublicNetworkIdList() {
      return this.publicNetworkIdList;
   }

   public void setId(final String id) {
      this.id = id;
   }

   public void setPublicNetworkIdList(final List<String> publicNetworkIdList) {
      this.publicNetworkIdList = publicNetworkIdList;
   }
}
