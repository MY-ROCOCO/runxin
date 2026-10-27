package com.rxinyun.common.exception;

import cn.hutool.core.util.StrUtil;

public class CommonException extends RuntimeException {
   private Integer code;
   private String msg;

   public CommonException() {
      super("服务器异常");
      this.code = 500;
      this.msg = "服务器异常";
   }

   public CommonException(String msg, Object... arguments) {
      super(StrUtil.format(msg, arguments));
      this.code = 500;
      this.msg = StrUtil.format(msg, arguments);
   }

   public CommonException(Integer code, String msg, Object... arguments) {
      super(StrUtil.format(msg, arguments));
      this.code = code;
      this.msg = StrUtil.format(msg, arguments);
   }

   public Integer getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public void setCode(final Integer code) {
      this.code = code;
   }

   public void setMsg(final String msg) {
      this.msg = msg;
   }
}
