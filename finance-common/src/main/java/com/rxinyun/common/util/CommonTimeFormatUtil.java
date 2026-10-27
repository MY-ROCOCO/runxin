package com.rxinyun.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CommonTimeFormatUtil {
   private static final ZoneId ZONE_GMT8 = ZoneId.of("Asia/Shanghai");
   private static final DateTimeFormatter NORM_DATETIME_GMT8 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final long ONE_MINUTE_SECONDS = 60L;
   private static final int BEFORE_DAWN_HOUR = 6;
   private static final int MORNING_END_HOUR = 12;
   private static final int NOON_END_HOUR = 13;
   private static final int AFTERNOON_END_HOUR = 18;
   private static final int NIGHT_END_HOUR = 24;

   public static String formatNowGmt8() {
      return ZonedDateTime.now(ZONE_GMT8).format(NORM_DATETIME_GMT8);
   }

   public static String formatWxPastTime(Date date) {
      if (DateUtil.between(date, DateUtil.date(), DateUnit.SECOND, false) < 0L) {
         return DateUtil.format(date, "yyyy-MM-dd HH:mm");
      } else if (DateUtil.thisYear() == DateUtil.year(date)) {
         if (DateUtil.isSameDay(date, DateUtil.date())) {
            long betweenMinute = DateUtil.between(date, DateUtil.date(), DateUnit.MINUTE);
            if (betweenMinute < 60L) {
               return betweenMinute < 1L ? "刚刚" : betweenMinute + "分钟前";
            } else {
               return getTodayHour(date) + " " + DateUtil.format(date, "HH:mm");
            }
         } else if (DateUtil.isSameDay(date, DateUtil.yesterday())) {
            return "昨天 " + DateUtil.format(date, "HH:mm");
         } else if (isThisWeek(date)) {
            int dayOfWeek = DateUtil.dayOfWeek(date) - 1;

            return switch (dayOfWeek) {
               case 1 -> "周一";
               case 2 -> "周二";
               case 3 -> "周三";
               case 4 -> "周四";
               case 5 -> "周五";
               case 6 -> "周六";
               default -> "周日";
            } + " " + DateUtil.format(date, "HH:mm");
         } else {
            return DateUtil.format(date, "MM-dd HH:mm");
         }
      } else {
         return DateUtil.format(date, "yyyy-MM-dd HH:mm");
      }
   }

   public static String formatSeconds(long secondsParam) {
      long days = secondsParam / 86400L;
      long hours = secondsParam % 86400L / 3600L;
      long minutes = secondsParam % 3600L / 60L;
      long seconds = secondsParam % 60L;
      String result;
      if (days > 0L) {
         result = days + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒";
      } else if (hours > 0L) {
         result = hours + "小时" + minutes + "分钟" + seconds + "秒";
      } else if (minutes > 0L) {
         result = minutes + "分钟" + seconds + "秒";
      } else {
         result = seconds + "秒";
      }

      return result;
   }

   private static boolean isThisWeek(Date date) {
      DateTime beginOfWeek = DateUtil.beginOfWeek(DateUtil.date());
      long betweenBegin = DateUtil.between(date, beginOfWeek, DateUnit.DAY, false) + 1L;
      return DateUtil.isSameDay(date, beginOfWeek) || betweenBegin < 0L;
   }

   private static String getTodayHour(Date date) {
      String result = "";
      int hour = DateUtil.hour(date, true);
      if (hour >= 0 && hour <= 6) {
         result = "凌晨";
      }

      if (hour > 6 && hour < 12) {
         result = "上午";
      }

      if (hour == 12) {
         result = "中午";
      }

      if (hour >= 13 && hour <= 18) {
         result = "下午";
      }

      if (hour > 18 && hour <= 24) {
         result = "晚上";
      }

      return result;
   }
}
