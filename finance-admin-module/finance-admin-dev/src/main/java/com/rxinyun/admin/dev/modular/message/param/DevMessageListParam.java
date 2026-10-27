package com.rxinyun.admin.dev.modular.message.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class DevMessageListParam {
   @Schema(
      description = "接收人id集合"
   )
   private List<String> receiverIdList;
   @Schema(
      description = "条数"
   )
   private Integer limit;

   public List<String> getReceiverIdList() {
      return this.receiverIdList;
   }

   public Integer getLimit() {
      return this.limit;
   }

   public void setReceiverIdList(final List<String> receiverIdList) {
      this.receiverIdList = receiverIdList;
   }

   public void setLimit(final Integer limit) {
      this.limit = limit;
   }
}
