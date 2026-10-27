package com.rxinyun.member.tools.core.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.tools.modular.log.util.MemberLogUtil;
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
public class MemberLogAop {
   @Pointcut("@annotation(com.rxinyun.common.annotation.CommonLog)")
   private void getLogPointCut() {
   }

   @AfterReturning(
      pointcut = "getLogPointCut()",
      returning = "result"
   )
   public void doAfterReturning(JoinPoint joinPoint, Object result) {
      if (this.isMemberRequest()) {
         MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
         Method method = methodSignature.getMethod();
         CommonLog commonLog = method.getAnnotation(CommonLog.class);
         String userName = "未知";

         try {
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            if (ObjectUtil.isNotNull(clientLoginUser)) {
               userName = clientLoginUser.getName();
            }
         } catch (Exception var8) {
         }

         MemberLogUtil.executeOperationLog(commonLog, userName, joinPoint, JSONUtil.toJsonStr(result));
      }
   }

   @AfterThrowing(
      pointcut = "getLogPointCut()",
      throwing = "exception"
   )
   public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
      if (this.isMemberRequest()) {
         MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
         Method method = methodSignature.getMethod();
         CommonLog commonLog = method.getAnnotation(CommonLog.class);
         String userName = "未知";

         try {
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            if (ObjectUtil.isNotNull(clientLoginUser)) {
               userName = clientLoginUser.getName();
            }
         } catch (Exception var8) {
         }

         MemberLogUtil.executeExceptionLog(commonLog, userName, joinPoint, exception);
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
