package com.rxinyun.admin.auth.modular.login.enums;

public enum AuthExceptionEnum {
   VALID_CODE_EMPTY("验证码不能为空"),
   VALID_CODE_REQ_NO_EMPTY("验证码请求号不能为空"),
   VALID_CODE_ERROR("验证码错误"),
   ACCOUNT_ERROR("账号错误"),
   ACCOUNT_DISABLED("账号已停用"),
   PWD_ERROR("密码错误"),
   PHONE_FORMAT_ERROR("手机号格式错误"),
   PHONE_ERROR("手机号不存在"),
   CLIENT_TYPE_EMPTY("客户端类型不能为空"),
   CLIENT_TYPE_ERROR("客户端类型错误"),
   PWD_DECRYPT_ERROR("密码解密失败，请检查前端公钥");

   private final String value;

   private AuthExceptionEnum(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
