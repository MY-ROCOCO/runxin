package com.rxinyun.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNoGenerator {
   private static final AtomicInteger sequence = new AtomicInteger(0);
   private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");

   public static synchronized String generateOrderNumber(Integer orderType) {
      String timestamp = sdf.format(new Date());
      int seq = sequence.incrementAndGet();
      if (seq > 99999) {
         sequence.set(1);
      }

      String sequenceStr = String.format("%05d", seq);
      return timestamp + orderType + sequenceStr;
   }
}
