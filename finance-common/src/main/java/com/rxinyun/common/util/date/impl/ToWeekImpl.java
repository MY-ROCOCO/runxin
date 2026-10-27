package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;

public class ToWeekImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      LocalDate today = LocalDate.now();
      LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
      LocalDateTime startOfThisWeek = startOfWeek.atStartOfDay();
      LocalDateTime endOfThisWeek = endOfWeek.atTime(LocalTime.MAX);
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), startOfThisWeek);
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), endOfThisWeek);
      return map;
   }
}
