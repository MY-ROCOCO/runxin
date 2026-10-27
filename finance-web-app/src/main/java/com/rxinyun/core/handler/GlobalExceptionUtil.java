package com.rxinyun.core.handler;

import cn.dev33.satoken.exception.SaTokenException;
import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.admin.auth.core.util.AuthExceptionUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonServletUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.mybatis.spring.MyBatisSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

public class GlobalExceptionUtil {
   private static final Logger log = LoggerFactory.getLogger(GlobalExceptionUtil.class);

   private GlobalExceptionUtil() {
   }

   public static CommonResult<String> getCommonResult(Exception e) {
      CommonResult<String> commonResult;
      if (e instanceof HttpRequestMethodNotSupportedException) {
         String method = CommonServletUtil.getRequest().getMethod();
         if (HttpMethod.GET.toString().equals(method)) {
            commonResult = CommonResult.get(405, "请求方法应为POST", null);
         } else if (HttpMethod.POST.toString().equals(method)) {
            commonResult = CommonResult.get(405, "请求方法应为GET", null);
         } else {
            commonResult = CommonResult.get(405, "请求方法仅支持GET或POST", null);
         }
      } else if (e instanceof HttpMessageNotReadableException) {
         log.error(">>> 参数传递格式异常：", e);
         if (e.getMessage().contains("JSON parse error")) {
            commonResult = CommonResult.get(415, "参数格式错误", null);
         } else {
            commonResult = CommonResult.get(415, "请使用JSON方式传参", null);
         }
      } else if (e instanceof HttpMediaTypeNotSupportedException) {
         log.error(">>> 参数传递格式异常：", e);
         commonResult = CommonResult.get(415, "参数格式错误", null);
      } else if (e instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
         commonResult = CommonResult.get(415, getArgNotValidMessage(methodArgumentNotValidException.getBindingResult()), null);
      } else if (e instanceof BindException bindException) {
         commonResult = CommonResult.get(415, getArgNotValidMessage(bindException.getBindingResult()), null);
      } else if (e instanceof ConstraintViolationException constraintViolationException) {
         commonResult = CommonResult.get(415, getArgNotValidMessage(constraintViolationException.getConstraintViolations()), null);
      } else if (e instanceof MissingServletRequestParameterException missingServletRequestParameterException) {
         commonResult = CommonResult.get(415, missingServletRequestParameterException.getMessage(), null);
      } else if (e instanceof MultipartException) {
         log.error(">>> 文件上传参数异常：", e);
         commonResult = CommonResult.error("请使用multipart/form-data方式上传文件");
      } else if (e instanceof MissingServletRequestPartException) {
         log.error(">>> 文件上传参数异常：", e);
         commonResult = CommonResult.error("请选择要上传的文件并检查文件参数名称是否正确");
      } else {
         if (e instanceof SaTokenException) {
            return AuthExceptionUtil.getCommonResult(e);
         }

         if (e instanceof MyBatisSystemException) {
            Throwable cause = e.getCause();
            if (cause instanceof PersistenceException) {
               if (cause.getCause() instanceof CommonException commonException) {
                  commonResult = CommonResult.get(commonException.getCode(), commonException.getMsg(), null);
               } else {
                  log.error(">>> 数据操作异常：", e);
                  commonResult = CommonResult.error("数据操作异常");
               }
            } else {
               log.error(">>> 数据操作异常：", e);
               commonResult = CommonResult.error("数据操作异常");
            }
         } else if (e instanceof CommonException commonException) {
            commonResult = CommonResult.get(commonException.getCode(), commonException.getMsg(), null);
         } else {
            try {
               if (CommonServletUtil.getRequest() != null) {
                  log.error(">>> 服务器未知异常，请求地址：{}，具体信息：", CommonServletUtil.getRequest().getRequestURL(), e);
               } else {
                  log.error(">>> 服务器未知异常，具体信息：", e);
               }
            } catch (Throwable var10) {
               log.error(">>> 服务器未知异常，具体信息：", e);
            }

            commonResult = CommonResult.error();
         }
      }

      return commonResult;
   }

   public static String getArgNotValidMessage(Set<ConstraintViolation<?>> constraintViolationSet) {
      return CollectionUtils.isEmpty(constraintViolationSet)
         ? ""
         : constraintViolationSet.stream().<CharSequence>map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
   }

   public static String getArgNotValidMessage(BindingResult bindingResult) {
      return ObjectUtil.isNull(bindingResult)
         ? ""
         : bindingResult.getAllErrors().stream().<CharSequence>map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(","));
   }
}
