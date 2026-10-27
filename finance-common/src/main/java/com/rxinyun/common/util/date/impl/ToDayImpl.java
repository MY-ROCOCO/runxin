package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

public class ToDayImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      LocalDateTime todayStart = LocalDate.now().atStartOfDay();
      LocalDateTime todayEnd = TimeUtil.now();
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), todayStart);
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), todayEnd);
      return map;
   }
}
