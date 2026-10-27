package com.rxinyun.member.business.modular.esc.param;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   description = "云服务器网络历史数据响应"
)
public class MemberServerNetworkHistoryResult implements Serializable {
   @Schema(
      description = "网络数据值，格式：[上行带宽,下行速度]"
   )
   private String vm_value;
   @Schema(
      description = "采集时间"
   )
   private String optime;

   public String getVm_value() {
      return this.vm_value;
   }

   public String getOptime() {
      return this.optime;
   }

   public void setVm_value(final String vm_value) {
      this.vm_value = vm_value;
   }

   public void setOptime(final String optime) {
      this.optime = optime;
   }
}
