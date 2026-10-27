package com.rxinyun.common.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommonResponseUtil {
   public static void renderError(HttpServletResponse response) throws IOException {
      renderError(response, null);
   }

   public static void renderError(HttpServletResponse response, String msg) throws IOException {
      response.setCharacterEncoding("UTF-8");
      response.setContentType(ContentType.JSON.toString());
      response.getWriter().write(JSONUtil.toJsonStr(ObjectUtil.isNotEmpty(msg) ? CommonResult.error(msg) : CommonResult.error()));
   }

   public static void renderError(HttpServletResponse response, Integer code, String msg) throws IOException {
      response.setCharacterEncoding("UTF-8");
      response.setContentType(ContentType.JSON.toString());
      response.getWriter().write(JSONUtil.toJsonStr(CommonResult.get(code, msg, null)));
   }
}
