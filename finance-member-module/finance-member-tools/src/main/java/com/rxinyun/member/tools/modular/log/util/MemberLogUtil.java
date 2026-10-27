package com.rxinyun.member.tools.modular.log.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonJoinPointUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.CommonUaUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.tools.modular.log.entity.MemberLog;
import com.rxinyun.member.tools.modular.log.enums.MemberLogCategoryEnum;
import com.rxinyun.member.tools.modular.log.enums.MemberLogExeStatusEnum;
import com.rxinyun.member.tools.modular.log.service.MemberLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;

public class MemberLogUtil {
   private static final MemberLogService memberLogService = (MemberLogService)SpringUtil.getBean(MemberLogService.class);

   public static void executeOperationLog(CommonLog commonLog, String userName, JoinPoint joinPoint, String resultJson) {
      HttpServletRequest request = CommonServletUtil.getRequest();
      String requestURI = request.getRequestURI();
      String method = request.getRequestURI();
      MemberLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(MemberLogCategoryEnum.OPERATE.getValue());
         devLog.setName(commonLog.value());
         devLog.setExeStatus(MemberLogExeStatusEnum.SUCCESS.getValue());
         devLog.setClassName(joinPoint.getTarget().getClass().getName());
         devLog.setMethodName(joinPoint.getSignature().getName());
         devLog.setReqMethod(method);
         devLog.setReqUrl(requestURI);
         devLog.setParamJson(CommonJoinPointUtil.getArgsJsonString(joinPoint));
         devLog.setResultJson(resultJson);
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         memberLogService.save(devLog);
      });
   }

   public static void executeExceptionLog(CommonLog commonLog, String userName, JoinPoint joinPoint, Exception exception) {
      HttpServletRequest request = CommonServletUtil.getRequest();
      String requestURI = request.getRequestURI();
      String method = request.getRequestURI();
      MemberLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setCategory(MemberLogCategoryEnum.EXCEPTION.getValue());
         devLog.setName(commonLog.value());
         devLog.setExeStatus(MemberLogExeStatusEnum.FAIL.getValue());
         devLog.setExeMessage(ExceptionUtil.stacktraceToString(exception, Integer.MAX_VALUE));
         devLog.setClassName(joinPoint.getTarget().getClass().getName());
         devLog.setMethodName(joinPoint.getSignature().getName());
         devLog.setReqMethod(method);
         devLog.setReqUrl(requestURI);
         devLog.setParamJson(CommonJoinPointUtil.getArgsJsonString(joinPoint));
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         memberLogService.save(devLog);
      });
   }

   public static void executeLoginLog(String memberNo, String userName) {
      MemberLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setMemberNo(memberNo);
         devLog.setCategory(MemberLogCategoryEnum.LOGIN.getValue());
         devLog.setName("用户登录");
         devLog.setExeStatus(MemberLogExeStatusEnum.SUCCESS.getValue());
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         memberLogService.save(devLog);
      });
   }

   public static void executeLogoutLog(String memberNo, String userName) {
      MemberLog devLog = genBasOpLog();
      ThreadUtil.execute(() -> {
         devLog.setMemberNo(memberNo);
         devLog.setCategory(MemberLogCategoryEnum.LOGOUT.getValue());
         devLog.setName("用户登出");
         devLog.setExeStatus(MemberLogExeStatusEnum.SUCCESS.getValue());
         devLog.setOpTime(TimeUtil.toDate(TimeUtil.now()));
         devLog.setOpUser(userName);
         creatLogSignValue(devLog);
         memberLogService.save(devLog);
      });
   }

   private static MemberLog genBasOpLog() {
      HttpServletRequest request = CommonServletUtil.getRequest();
      String ip = CommonIpAddressUtil.getIp(request);

      String loginId;
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         if (ObjectUtil.isNotEmpty(clientLoginUser) && ObjectUtil.isNotEmpty(clientLoginUser.getId())) {
            loginId = String.valueOf(clientLoginUser.getId());
         } else {
            loginId = "-1";
         }
      } catch (Exception var4) {
         loginId = "-1";
      }

      MemberLog devLog = new MemberLog();
      devLog.setOpIp(CommonIpAddressUtil.getIp(request));
      devLog.setOpAddress(CommonIpAddressUtil.getCityInfo(ip));
      devLog.setOpBrowser(CommonUaUtil.getBrowser(request));
      devLog.setOpOs(CommonUaUtil.getOs(request));
      devLog.setMemberNo(loginId);
      devLog.setCreateUser(loginId);
      return devLog;
   }

   private static void creatLogSignValue(MemberLog devLog) {
      String logStr = devLog.toString().replaceAll(" +", "");
      devLog.setSignData(CommonCryptogramUtil.doSignature(logStr));
   }
}
