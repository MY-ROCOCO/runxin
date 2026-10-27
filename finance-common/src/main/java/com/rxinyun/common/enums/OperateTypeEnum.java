package com.rxinyun.common.enums;

public enum OperateTypeEnum {
   CREATE(1, "创建"),
   RECHARGE(2, "续费"),
   UPGRADE(3, "升级"),
   REBUILD(4, "重建"),
   REINSTALL(5, "重装系统"),
   START(6, "开机"),
   STOP(7, "关机"),
   REBOOT(8, "重启"),
   RESET_PASSWORD(9, "重置密码"),
   ADD_IP(10, "新增IP"),
   SHUT_DOWN(11, "断开电源"),
   DELETE(12, "删除"),
   LOCK(13, "锁定"),
   UNLOCK(14, "解锁"),
   EXPIRE_STOP(15, "到期自动关机"),
   EXPIRE_DELETE(16, "到期自动删除");

   private final Integer code;
   private final String description;

   private OperateTypeEnum(Integer code, String description) {
      this.code = code;
      this.description = description;
   }

   public static String getDescription(Integer code) {
      for (OperateTypeEnum operateTypeEnum : values()) {
         if (operateTypeEnum.getCode().equals(code)) {
            return operateTypeEnum.getDescription();
         }
      }

      return null;
   }

   public Integer getCode() {
      return this.code;
   }

   public String getDescription() {
      return this.description;
   }
}
