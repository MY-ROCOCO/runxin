package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

public class LastDayImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      LocalDate yesterday = LocalDate.now().minusDays(1L);
      LocalDateTime yesterdayStart = yesterday.atStartOfDay();
      LocalDateTime yesterdayEnd = yesterday.atTime(LocalTime.MAX);
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), yesterdayStart);
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), yesterdayEnd);
      return map;
   }
}
