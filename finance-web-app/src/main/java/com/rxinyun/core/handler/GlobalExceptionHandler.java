package com.rxinyun.core.handler;

import com.rxinyun.common.pojo.CommonResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
   public GlobalExceptionHandler() {
   }

   @ResponseBody
   @ExceptionHandler({NoHandlerFoundException.class})
   public CommonResult<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
      return CommonResult.error(HttpStatus.NOT_FOUND.value(), "Resource not found");
   }

   @ResponseBody
   @ExceptionHandler({NoResourceFoundException.class})
   public CommonResult<String> handleNoResourceFoundException(NoResourceFoundException ex) {
      return CommonResult.error(HttpStatus.NOT_FOUND.value(), "Static resource not found");
   }

   @ResponseBody
   @ExceptionHandler
   public CommonResult<String> handleException(Exception e) {
      return GlobalExceptionUtil.getCommonResult(e);
   }
}
