package com.rxinyun.common.enums.business;

public enum ServerStatusEnum {
   CREATING("CREATING", "创建中"),
   STARTING("STARTING", "启动中"),
   RUNNING("RUNNING", "运行中"),
   STOPPING("STOPPING", "关机中"),
   STOP("STOP", "关机"),
   LOCKING("LOCKING", "锁定中"),
   LOCKED("LOCKED", "锁定"),
   UNLOCKING("UNLOCKING", "解锁中"),
   UNLOCK("UNLOCK", "解锁"),
   REINSTALLING("REINSTALLING", "系统重装中"),
   REBUILDING("REBUILDING", "重建中"),
   UPGRADE("UPGRADE", "升级中"),
   DELETING("DELETING", "删除中"),
   REBOOTING("REBOOTING", "重启中"),
   POWERING("POWERING", "断电中"),
   POWER("POWER", "断电"),
   DISCONNECTING("DISCONNECTING", "断网中"),
   DISCONNECT("DISCONNECT", "断网"),
   CONNECTING("CONNECTING", "断网恢复中"),
   CONNECT("CONNECT", "断网恢复"),
   UNKNOWN("UNKNOWN", "未知状态");

   private final String code;
   private final String description;

   private ServerStatusEnum(String code, String description) {
      this.code = code;
      this.description = description;
   }

   public String getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }

   public static ServerStatusEnum getByCode(String code) {
      if (code == null) {
         return UNKNOWN;
      } else {
         for (ServerStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
               return status;
            }
         }

         return UNKNOWN;
      }
   }
}
