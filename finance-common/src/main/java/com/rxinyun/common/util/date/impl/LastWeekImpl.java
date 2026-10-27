package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;

public class LastWeekImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      LocalDate today = LocalDate.now();
      LocalDate startOfWeek = today.minusWeeks(1L).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      LocalDate endOfWeek = today.minusWeeks(1L).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
      LocalDateTime startOfLastWeek = startOfWeek.atStartOfDay();
      LocalDateTime endOfLastWeek = endOfWeek.atTime(LocalTime.MAX);
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), startOfLastWeek);
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), endOfLastWeek);
      return map;
   }
}
