package com.rxinyun.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

public class CommonResult<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int CODE_SUCCESS = 200;
   public static final int CODE_ERROR = 500;
   @Schema(
      description = "状态码"
   )
   private int code;
   @Schema(
      description = "提示语"
   )
   private String msg;
   @Schema(
      description = "返回数据"
   )
   private T data;

   public CommonResult() {
   }

   public CommonResult(int code, String msg, T data) {
      this.setCode(code);
      this.setMsg(msg);
      this.setData(data);
   }

   public Integer getCode() {
      return this.code;
   }

   public String getMsg() {
      return this.msg;
   }

   public T getData() {
      return this.data;
   }

   public CommonResult<T> setCode(int code) {
      this.code = code;
      return this;
   }

   public CommonResult<T> setMsg(String msg) {
      this.msg = msg;
      return this;
   }

   public CommonResult<T> setData(T data) {
      this.data = data;
      return this;
   }

   public static <T> CommonResult<T> ok() {
      return new CommonResult<>(200, "操作成功", null);
   }

   public static <T> CommonResult<T> ok(String msg) {
      return new CommonResult<>(200, msg, null);
   }

   public static <T> CommonResult<T> code(int code) {
      return new CommonResult<>(code, null, null);
   }

   public static <T> CommonResult<T> data(T data) {
      return new CommonResult<>(200, "操作成功", data);
   }

   public static <T> CommonResult<T> error() {
      return new CommonResult<>(500, "服务器异常", null);
   }

   public static <T> CommonResult<T> error(String msg, T data) {
      return new CommonResult<>(500, msg, data);
   }

   public static <T> CommonResult<T> error(String msg) {
      return new CommonResult<>(500, msg, null);
   }

   public static <T> CommonResult<T> error(int code, String msg) {
      return new CommonResult<>(code, msg, null);
   }

   public static <T> CommonResult<T> get(int code, String msg, T data) {
      return new CommonResult<>(code, msg, data);
   }

   @Override
   public String toString() {
      return "{\"code\": " + this.getCode() + ", \"msg\": \"" + this.getMsg() + "\", \"data\": \"" + this.getData() + "\"}";
   }
}
