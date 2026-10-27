package com.rxinyun.business.core.enums;

public enum TaskTypeEnum {
   CREATE("CREATE", "创建服务器"),
   UPGRADE_BANDWIDTH("UPGRADE_BANDWIDTH", "升级带宽"),
   UPGRADE_CPU_RAM("UPGRADE_CPU_RAM", "升级CPU和内存"),
   UPGRADE_HARD_DISKS("UPGRADE_HARD_DISKS", "升级硬盘"),
   UPGRADE_DEFENSE("UPGRADE_DEFENSE", "升级防御"),
   RENEW("RENEW", "续费服务器"),
   START("START", "启动服务器"),
   STOP("STOP", "关闭服务器"),
   RESTART("RESTART", "重启服务器"),
   REINSTALL("REINSTALL", "重装系统"),
   REBUILD("REBUILD", "重建服务器"),
   UPDATE_PASSWORD("UPDATE_PASSWORD", "修改密码"),
   LOCK("LOCK", "锁定服务器"),
   UNLOCK("UNLOCK", "解锁服务器"),
   ADD_IP("ADD_IP", "添加IP"),
   RESET_IP("RESET_IP", "重置IP"),
   DELETE("DELETE", "删除服务器"),
   DISCONNECT("DISCONNECT", "服务器断网"),
   RECONNECT("RECONNECT", "服务器恢复网络"),
   POWER("POWER", "关闭服务器电源");

   private final String code;
   private final String description;

   private TaskTypeEnum(String code, String description) {
      this.code = code;
      this.description = description;
   }

   public static TaskTypeEnum getByCode(String code) {
      for (TaskTypeEnum value : values()) {
         if (value.getCode().equals(code)) {
            return value;
         }
      }

      return null;
   }

   public static boolean isValid(String code) {
      return getByCode(code) != null;
   }

   public String getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }
}
