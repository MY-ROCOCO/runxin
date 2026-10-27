package com.rxinyun.admin.dev.core.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.dev.modular.log.util.LogUtil;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.util.CommonServletUtil;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order
@Component
public class DevLogAop {
   @Pointcut("@annotation(com.rxinyun.common.annotation.CommonLog)")
   private void getLogPointCut() {
   }

   @AfterReturning(
      pointcut = "getLogPointCut()",
      returning = "result"
   )
   public void doAfterReturning(JoinPoint joinPoint, Object result) {
      if (!this.isMemberRequest()) {
         MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
         Method method = methodSignature.getMethod();
         CommonLog commonLog = method.getAnnotation(CommonLog.class);
         String userName = "未知";

         try {
            try {
               SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
               if (ObjectUtil.isNotNull(loginUser)) {
                  userName = loginUser.getName();
               }
            } catch (Exception var8) {
            }
         } catch (Exception var9) {
         }

         LogUtil.executeOperationLog(commonLog, userName, joinPoint, JSONUtil.toJsonStr(result));
      }
   }

   @AfterThrowing(
      pointcut = "getLogPointCut()",
      throwing = "exception"
   )
   public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
      if (!this.isMemberRequest()) {
         MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
         Method method = methodSignature.getMethod();
         CommonLog commonLog = method.getAnnotation(CommonLog.class);
         String userName = "未知";

         try {
            SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
            if (ObjectUtil.isNotNull(loginUser)) {
               userName = loginUser.getName();
            }
         } catch (Exception var8) {
         }

         LogUtil.executeExceptionLog(commonLog, userName, joinPoint, exception);
      }
   }

   private boolean isMemberRequest() {
      try {
         String requestUri = CommonServletUtil.getRequest().getRequestURI();
         return requestUri != null && requestUri.startsWith("/member/");
      } catch (Exception var2) {
         return false;
      }
   }
}
