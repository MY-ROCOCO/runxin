package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;

public class ToMonthImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      LocalDate today = LocalDate.now();
      LocalDate firstDayOfMonth = today.withDayOfMonth(1);
      LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
      LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
      LocalDateTime endOfMonth = lastDayOfMonth.atTime(LocalTime.MAX);
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), startOfMonth);
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), endOfMonth);
      return map;
   }
}
