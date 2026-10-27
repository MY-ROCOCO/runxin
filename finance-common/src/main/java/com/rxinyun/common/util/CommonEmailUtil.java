package com.rxinyun.common.util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.exception.CommonException;

public class CommonEmailUtil {
   public static boolean isEmail(String email) {
      return Validator.isEmail(email);
   }

   public static void validEmail(String emails) {
      StrUtil.split(emails, ",").forEach(email -> {
         if (!isEmail(email)) {
            throw new CommonException("邮件地址：{}格式错误", email);
         }
      });
   }
}
