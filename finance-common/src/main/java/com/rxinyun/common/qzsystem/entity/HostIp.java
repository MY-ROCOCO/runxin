package com.rxinyun.common.qzsystem.entity;

import io.swagger.v3.oas.annotations.media.Schema;

public class HostIp {
   @Schema(
      description = "IP地址"
   )
   private String ip;
   @Schema(
      description = "子网掩码"
   )
   private String mask;

   public String getIp() {
      return this.ip;
   }

   public String getMask() {
      return this.mask;
   }

   public void setIp(final String ip) {
      this.ip = ip;
   }

   public void setMask(final String mask) {
      this.mask = mask;
   }
}
