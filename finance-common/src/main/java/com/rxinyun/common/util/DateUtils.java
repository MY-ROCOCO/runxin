package com.rxinyun.common.util;

import com.rxinyun.common.exception.CommonException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
   public static Integer betweenDay(Date start, Date end, boolean isAbs) {
      if (start != null && end != null) {
         long diffInMillies = end.getTime() - start.getTime();
         long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
         if (isAbs) {
            diffInDays = Math.abs(diffInDays);
         }

         if (diffInDays <= 2147483647L && diffInDays >= -2147483648L) {
            return (int)diffInDays;
         } else {
            throw new CommonException("日期差值超出Integer范围");
         }
      } else {
         throw new CommonException("日期参数不能为空");
      }
   }
}
