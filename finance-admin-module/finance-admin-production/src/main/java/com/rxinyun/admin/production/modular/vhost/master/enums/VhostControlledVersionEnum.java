package com.rxinyun.admin.production.modular.vhost.master.enums;

import com.rxinyun.common.exception.CommonException;

public enum VhostControlledVersionEnum {
   BAOTA("宝塔虚拟主机");

   private final String label;

   private VhostControlledVersionEnum(String label) {
      this.label = label;
   }

   public static VhostControlledVersionEnum of(String code) {
      for (VhostControlledVersionEnum item : values()) {
         if (item.name().equals(code)) {
            return item;
         }
      }

      throw new CommonException("受控版本仅支持宝塔虚拟主机", new Object[0]);
   }

   public String getLabel() {
      return this.label;
   }
}
