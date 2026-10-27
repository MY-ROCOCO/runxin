package com.rxinyun.common.enums;

public enum UpgradeItemEnum {
   UPGRADE_CORE(1, "UPGRADE_CPU_RAM", "核心"),
   UPGRADE_BANDWIDTH(2, "UPGRADE_BANDWIDTH", "带宽"),
   UPGRADE_DEFENSE(3, "UPGRADE_DEFENSE", "防御"),
   UPGRADE_HARD_DISK(4, "UPGRADE_DISK", "磁盘"),
   UPGRADE_IP(5, "UPGRADE_IP", "IP"),
   UPGRADE_TRAFFIC(6, "UPGRADE_TRAFFIC", "流量");

   private final Integer code;
   private final String value;
   private final String description;

   private UpgradeItemEnum(Integer code, String value, String description) {
      this.code = code;
      this.value = value;
      this.description = description;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getValue() {
      return this.value;
   }

   public String getDescription() {
      return this.description;
   }

   public static String getDescriptionByCode(Integer code) {
      for (UpgradeItemEnum upgradeItem : values()) {
         if (upgradeItem.getCode() == code) {
            return upgradeItem.getDescription();
         }
      }

      return null;
   }
}
