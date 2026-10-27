package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;

public class LastMonthImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      LocalDate today = LocalDate.now();
      YearMonth lastMonth = YearMonth.from(today).minusMonths(1L);
      LocalDate firstDayOfLastMonth = lastMonth.atDay(1);
      LocalDate lastDayOfLastMonth = lastMonth.atEndOfMonth();
      LocalDateTime startOfLastMonth = firstDayOfLastMonth.atStartOfDay();
      LocalDateTime endOfLastMonth = lastDayOfLastMonth.atTime(LocalTime.MAX);
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), startOfLastMonth);
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), endOfLastMonth);
      return map;
   }
}
