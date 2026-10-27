package com.rxinyun.common.util.date.impl;

import com.rxinyun.common.util.date.DateConversion;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import java.time.LocalDateTime;
import java.util.HashMap;

public class AllTimeImpl implements DateConversion {
   @Override
   public HashMap<String, LocalDateTime> startToEndTime() {
      HashMap<String, LocalDateTime> map = new HashMap<>();
      map.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), LocalDateTime.of(0, 1, 1, 0, 0, 0));
      map.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), LocalDateTime.of(9999, 12, 31, 23, 59, 59));
      return map;
   }
}
