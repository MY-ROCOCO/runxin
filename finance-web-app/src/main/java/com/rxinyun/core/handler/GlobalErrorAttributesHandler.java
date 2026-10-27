package com.rxinyun.core.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonServletUtil;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class GlobalErrorAttributesHandler extends DefaultErrorAttributes {
   public GlobalErrorAttributesHandler() {
   }

   public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions attributeOptions) {
      Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, attributeOptions);
      Object status = defaultErrorAttributes.get("status");
      Object path = defaultErrorAttributes.get("path");
      if (ObjectUtil.isNotEmpty(status)) {
         if (404 == Convert.toInt(status)) {
            return ObjectUtil.isNotEmpty(path)
               ? BeanUtil.beanToMap(CommonResult.get(404, "路径不存在，请求地址：" + Convert.toStr(path), null), new String[0])
               : BeanUtil.beanToMap(CommonResult.get(404, "路径不存在", null), new String[0]);
         } else {
            return BeanUtil.beanToMap(CommonResult.get(500, "服务器异常，请求地址：" + Convert.toStr(path), null), new String[0]);
         }
      } else {
         Throwable throwable = this.getError(webRequest);
         if (ObjectUtil.isNotEmpty(throwable)) {
            return throwable instanceof CommonException commonException
               ? BeanUtil.beanToMap(CommonResult.error(commonException.getMsg()), new String[0])
               : BeanUtil.beanToMap(CommonResult.get(500, "服务器异常，请求地址：" + CommonServletUtil.getRequest().getRequestURL(), null), new String[0]);
         } else {
            return BeanUtil.beanToMap(CommonResult.error(), new String[0]);
         }
      }
   }
}
