package com.rxinyun.member.auth.core.util;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberAuthExceptionUtil {
   private static final Logger log = LoggerFactory.getLogger(MemberAuthExceptionUtil.class);

   public static CommonResult<String> getCommonResult(Exception e) {
      CommonResult<String> commonResult;
      if (e instanceof NotLoginException notLoginException) {
         commonResult = CommonResult.get(401, notLoginException.getMessage(), null);
      } else if (e instanceof NotRoleException notRoleException) {
         commonResult = CommonResult.get(403, "无此角色：" + notRoleException.getRole() + "，接口地址：" + CommonServletUtil.getRequest().getServletPath(), null);
      } else if (e instanceof NotPermissionException notPermissionException) {
         commonResult = CommonResult.get(403, "无此权限：" + notPermissionException.getPermission(), null);
      } else if (e instanceof DisableServiceException disableServiceException) {
         commonResult = CommonResult.get(403, "账号被封禁：" + disableServiceException.getDisableTime() + "秒后解封", null);
      } else if (e instanceof SaTokenException saTokenException) {
         commonResult = CommonResult.error(saTokenException.getMessage());
      } else {
         log.error(">>> 服务器未知异常，请求地址：{}，具体信息：", CommonServletUtil.getRequest().getRequestURL(), e);
         commonResult = CommonResult.error();
      }

      return commonResult;
   }
}
