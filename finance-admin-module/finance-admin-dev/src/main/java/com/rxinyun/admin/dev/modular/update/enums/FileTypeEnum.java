package com.rxinyun.admin.dev.modular.update.enums;

public enum FileTypeEnum {
   FRONT("FRONT", "前端用户端包"),
   ADMIN_FRONT("ADMIN_FRONT", "管理后台前端包"),
   BACK("BACK", "后端服务包");

   private final String code;
   private final String message;

   private FileTypeEnum(String code, String message) {
      this.code = code;
      this.message = message;
   }

   public String getCode() {
      return this.code;
   }

   public String getMessage() {
      return this.message;
   }
}
