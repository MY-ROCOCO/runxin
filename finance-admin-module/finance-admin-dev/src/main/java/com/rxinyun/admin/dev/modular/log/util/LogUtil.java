package com.rxinyun.admin.dev.modular.log.util;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.dev.modular.log.entity.SysLog;
import com.rxinyun.admin.dev.modular.log.enums.DevLogCategoryEnum;
import com.rxinyun.admin.dev.modular.log.enums.DevLogExeStatusEnum;
import com.rxinyun.admin.dev.modular.log.service.SysLogService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonJoinPointUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.CommonUaUtil;
import com.rxinyun.common.util.TimeUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;

public class LogUtil {
   private static final SysLogService devLogService = (SysLogService)SpringUtil.getBean(SysLogService.class);
   public static final String SYSTEM_TASK_OPERATOR = "系统任务";

   public static void executeOperationLog(CommonLog commonLog, String userName, JoinPoint joinPoint, String resultJson) {
      HttpServletRequest request = CommonServletUtil.getRequest();
      String requestURI = request.getRequestURI();
      String method = request.getRequestURI();
      SysLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(DevLogCategoryEnum.OPERATE.getValue());
         devLog.setName(commonLog.value());
         devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
         devLog.setClassName(joinPoint.getTarget().getClass().getName());
         devLog.setMethodName(joinPoint.getSignature().getName());
         devLog.setReqMethod(method);
         devLog.setReqUrl(requestURI);
         devLog.setParamJson(CommonJoinPointUtil.getArgsJsonString(joinPoint));
         devLog.setResultJson(resultJson);
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         devLogService.save(devLog);
      });
   }

   public static void executeExceptionLog(CommonLog commonLog, String userName, JoinPoint joinPoint, Exception exception) {
      HttpServletRequest request = CommonServletUtil.getRequest();
      String requestURI = request.getRequestURI();
      String method = request.getRequestURI();
      SysLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(DevLogCategoryEnum.EXCEPTION.getValue());
         devLog.setName(commonLog.value());
         devLog.setExeStatus(DevLogExeStatusEnum.FAIL.getValue());
         devLog.setExeMessage(ExceptionUtil.stacktraceToString(exception, Integer.MAX_VALUE));
         devLog.setClassName(joinPoint.getTarget().getClass().getName());
         devLog.setMethodName(joinPoint.getSignature().getName());
         devLog.setReqMethod(method);
         devLog.setReqUrl(requestURI);
         devLog.setParamJson(CommonJoinPointUtil.getArgsJsonString(joinPoint));
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         devLogService.save(devLog);
      });
   }

   public static void executeLoginLog(String userName) {
      SysLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(DevLogCategoryEnum.LOGIN.getValue());
         devLog.setName("用户登录");
         devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         devLogService.save(devLog);
      });
   }

   public static void executeLogoutLog(String userName) {
      SysLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(DevLogCategoryEnum.LOGOUT.getValue());
         devLog.setName("用户登出");
         devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         devLogService.save(devLog);
      });
   }

   public static void executeSystemOperationLog(String logName, Class<?> targetClass, String methodName, Object paramObj, String resultJson) {
      SysLog devLog = genSystemLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(DevLogCategoryEnum.OPERATE.getValue());
         devLog.setName(logName);
         devLog.setExeStatus(DevLogExeStatusEnum.SUCCESS.getValue());
         devLog.setClassName(targetClass.getName());
         devLog.setMethodName(methodName);
         devLog.setReqMethod("SYSTEM");
         devLog.setReqUrl("SYSTEM:" + targetClass.getSimpleName() + "#" + methodName);
         devLog.setParamJson(JSONUtil.toJsonStr(paramObj));
         devLog.setResultJson(resultJson);
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser("系统任务");
         creatLogSignValue(devLog);
         devLogService.save(devLog);
      });
   }

   public static void executeSystemExceptionLog(String logName, Class<?> targetClass, String methodName, Object paramObj, Exception exception) {
      SysLog devLog = genSystemLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(DevLogCategoryEnum.EXCEPTION.getValue());
         devLog.setName(logName);
         devLog.setExeStatus(DevLogExeStatusEnum.FAIL.getValue());
         devLog.setExeMessage(ExceptionUtil.stacktraceToString(exception, Integer.MAX_VALUE));
         devLog.setClassName(targetClass.getName());
         devLog.setMethodName(methodName);
         devLog.setReqMethod("SYSTEM");
         devLog.setReqUrl("SYSTEM:" + targetClass.getSimpleName() + "#" + methodName);
         devLog.setParamJson(JSONUtil.toJsonStr(paramObj));
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser("系统任务");
         creatLogSignValue(devLog);
         devLogService.save(devLog);
      });
   }

   private static SysLog genBasOpLog() {
      HttpServletRequest request = CommonServletUtil.getRequest();
      String ip = CommonIpAddressUtil.getIp(request);

      String loginId;
      try {
         loginId = StpUtil.getLoginIdAsString();
         if (ObjectUtil.isEmpty(loginId)) {
            loginId = "-1";
         }
      } catch (Exception var4) {
         loginId = "-1";
      }

      SysLog devLog = new SysLog();
      devLog.setOpIp(CommonIpAddressUtil.getIp(request));
      devLog.setOpAddress(CommonIpAddressUtil.getCityInfo(ip));
      devLog.setOpBrowser(CommonUaUtil.getBrowser(request));
      devLog.setOpOs(CommonUaUtil.getOs(request));
      devLog.setCreateUser(loginId);
      return devLog;
   }

   private static SysLog genSystemLog() {
      SysLog devLog = new SysLog();
      devLog.setOpIp("127.0.0.1");
      devLog.setOpAddress("系统任务");
      devLog.setOpBrowser("SYSTEM");
      devLog.setOpOs("SYSTEM");
      devLog.setCreateUser("-1");
      return devLog;
   }

   private static void creatLogSignValue(SysLog devLog) {
      String logStr = devLog.toString().replaceAll(" +", "");
      devLog.setSignData(CommonCryptogramUtil.doSignature(logStr));
   }
}
