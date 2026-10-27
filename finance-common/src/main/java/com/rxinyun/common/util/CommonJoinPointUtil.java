package com.rxinyun.common.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.multipart.MultipartFile;

public class CommonJoinPointUtil {
   public static String getArgsJsonString(JoinPoint joinPoint) {
      Signature signature = joinPoint.getSignature();
      String[] parameterNames = ((MethodSignature)signature).getParameterNames();
      Map<String, Object> map = MapUtil.newHashMap();
      Object[] args = joinPoint.getArgs();

      for (int i = 0; i < args.length; i++) {
         if (ObjectUtil.isNotEmpty(args[i]) && isUsefulParam(args[i])) {
            if (JSONUtil.isTypeJSON(StrUtil.toString(args[i]))) {
               try {
                  JSONObject jsonObject = JSONUtil.parseObj(args[i]);
                  if (ObjectUtil.isNotEmpty(jsonObject)) {
                     map.put(parameterNames[i], jsonObject);
                  } else {
                     map.put(parameterNames[i], JSONUtil.parseArray(args[i]));
                  }
               } catch (Exception var7) {
                  map.put(parameterNames[i], null);
               }
            } else {
               map.put(parameterNames[i], JSONUtil.toJsonStr(args[i]));
            }
         }
      }

      return JSONUtil.toJsonStr(map);
   }

   private static boolean isUsefulParam(Object arg) {
      return !(arg instanceof MultipartFile) && !(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse);
   }
}
