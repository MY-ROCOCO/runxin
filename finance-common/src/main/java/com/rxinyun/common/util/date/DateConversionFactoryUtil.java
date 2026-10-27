package com.rxinyun.common.util.date;

import cn.hutool.core.util.ObjectUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.date.enums.DateUtilParamName;
import com.rxinyun.common.util.date.impl.AllTimeImpl;
import com.rxinyun.common.util.date.impl.LastDayImpl;
import com.rxinyun.common.util.date.impl.LastMonthImpl;
import com.rxinyun.common.util.date.impl.LastWeekImpl;
import com.rxinyun.common.util.date.impl.ToDayImpl;
import com.rxinyun.common.util.date.impl.ToMonthImpl;
import com.rxinyun.common.util.date.impl.ToWeekImpl;
import java.time.LocalDateTime;
import java.util.HashMap;

public class DateConversionFactoryUtil {
   public static HashMap<String, LocalDateTime> conversion(String date) {
      if (ObjectUtil.isEmpty(date) || date == null) {
         date = DateUtilParamName.ALLTIME.getValue();
      }

      HashMap<String, DateConversion> dateMap = new HashMap<>();
      dateMap.put(DateUtilParamName.LAST_DAY.getValue(), new LastDayImpl());
      dateMap.put(DateUtilParamName.TO_DAY.getValue(), new ToDayImpl());
      dateMap.put(DateUtilParamName.TO_WEEK.getValue(), new ToWeekImpl());
      dateMap.put(DateUtilParamName.LAST_WEEK.getValue(), new LastWeekImpl());
      dateMap.put(DateUtilParamName.TO_MONTH.getValue(), new ToMonthImpl());
      dateMap.put(DateUtilParamName.LAST_MONTH.getValue(), new LastMonthImpl());
      dateMap.put(DateUtilParamName.ALLTIME.getValue(), new AllTimeImpl());
      DateConversion dateConversion = dateMap.get(date);
      if (dateConversion == null) {
         throw new CommonException("该时间方案未开通");
      } else {
         return dateConversion.startToEndTime();
      }
   }
}
