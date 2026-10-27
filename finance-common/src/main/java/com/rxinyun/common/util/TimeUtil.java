package com.rxinyun.common.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {
   public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
   public static final String DATE_PATTERN = "yyyy-MM-dd";
   public static final String TIME_PATTERN = "HH:mm:ss";
   private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
   private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

   public static LocalDateTime now() {
      return LocalDateTime.now();
   }

   public static LocalDate today() {
      return LocalDate.now();
   }

   public static LocalTime currentTime() {
      return LocalTime.now();
   }

   public static String format(LocalDateTime dateTime) {
      return dateTime == null ? null : dateTime.format(DEFAULT_FORMATTER);
   }

   public static String format(LocalDateTime dateTime, String pattern) {
      return dateTime == null ? null : dateTime.format(DateTimeFormatter.ofPattern(pattern));
   }

   public static String formatDate(LocalDate date) {
      return date == null ? null : date.format(DATE_FORMATTER);
   }

   public static String formatTime(LocalTime time) {
      return time == null ? null : time.format(TIME_FORMATTER);
   }

   public static LocalDateTime parse(String text) {
      return text != null && !text.isEmpty() ? LocalDateTime.parse(text, DEFAULT_FORMATTER) : null;
   }

   public static LocalDateTime parse(String text, String pattern) {
      return text != null && !text.isEmpty() ? LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern)) : null;
   }

   public static LocalDate parseDate(String text) {
      return text != null && !text.isEmpty() ? LocalDate.parse(text, DATE_FORMATTER) : null;
   }

   public static Date toDate(LocalDateTime dateTime) {
      return dateTime == null ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
   }

   public static LocalDateTime toLocalDateTime(Date date) {
      return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
   }

   public static LocalDateTime toLocalDateTime(LocalDate date) {
      return date == null ? null : date.atStartOfDay();
   }

   public static LocalDateTime toLocalDateTime(LocalDate date, LocalTime time) {
      return date == null ? null : date.atTime(time != null ? time : LocalTime.MIN);
   }

   public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
      return dateTime == null ? null : dateTime.plusDays(days);
   }

   public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
      return dateTime == null ? null : dateTime.minusDays(days);
   }

   public static LocalDateTime plusHours(LocalDateTime dateTime, long hours) {
      return dateTime == null ? null : dateTime.plusHours(hours);
   }

   public static LocalDateTime minusHours(LocalDateTime dateTime, long hours) {
      return dateTime == null ? null : dateTime.minusHours(hours);
   }

   public static LocalDateTime plusMinutes(LocalDateTime dateTime, long minutes) {
      return dateTime == null ? null : dateTime.plusMinutes(minutes);
   }

   public static LocalDateTime minusMinutes(LocalDateTime dateTime, long minutes) {
      return dateTime == null ? null : dateTime.minusMinutes(minutes);
   }

   public static boolean isBefore(LocalDateTime dateTime, LocalDateTime other) {
      return dateTime != null && other != null ? dateTime.isBefore(other) : false;
   }

   public static boolean isAfter(LocalDateTime dateTime, LocalDateTime other) {
      return dateTime != null && other != null ? dateTime.isAfter(other) : false;
   }

   public static boolean isEqual(LocalDateTime dateTime1, LocalDateTime dateTime2) {
      return dateTime1 != null && dateTime2 != null ? dateTime1.isEqual(dateTime2) : false;
   }

   public static LocalDateTime getTodayStart() {
      return LocalDate.now().atStartOfDay();
   }

   public static LocalDateTime getTodayEnd() {
      return LocalDate.now().atTime(LocalTime.MAX);
   }

   public static LocalDateTime getYesterdayStart() {
      return LocalDate.now().minusDays(1L).atStartOfDay();
   }

   public static LocalDateTime getYesterdayEnd() {
      return LocalDate.now().minusDays(1L).atTime(LocalTime.MAX);
   }

   public static LocalDateTime getWeekStart() {
      return LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
   }

   public static LocalDateTime getWeekEnd() {
      return LocalDate.now().with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);
   }

   public static LocalDateTime getMonthStart() {
      return LocalDate.now().withDayOfMonth(1).atStartOfDay();
   }

   public static LocalDateTime getMonthEnd() {
      return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);
   }

   public static long daysBetween(LocalDateTime start, LocalDateTime end) {
      return start != null && end != null ? Duration.between(start, end).toDays() : 0L;
   }

   public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
      return start != null && end != null ? Duration.between(start, end).toHours() : 0L;
   }

   public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
      return start != null && end != null ? Duration.between(start, end).toMinutes() : 0L;
   }

   public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
      return start != null && end != null ? Duration.between(start, end).getSeconds() : 0L;
   }

   public static String formatDateTime(Date date) {
      return date == null ? null : format(toLocalDateTime(date));
   }

   public static String formatBetween(Date start, Date end) {
      if (start != null && end != null) {
         long diff = end.getTime() - start.getTime();
         if (diff < 0L) {
            diff = -diff;
         }

         long days = diff / 86400000L;
         long hours = diff % 86400000L / 3600000L;
         long minutes = diff % 3600000L / 60000L;
         long seconds = diff % 60000L / 1000L;
         StringBuilder sb = new StringBuilder();
         if (days > 0L) {
            sb.append(days).append("天");
         }

         if (hours > 0L) {
            sb.append(hours).append("小时");
         }

         if (minutes > 0L) {
            sb.append(minutes).append("分钟");
         }

         if (seconds > 0L || sb.length() == 0) {
            sb.append(seconds).append("秒");
         }

         return sb.toString();
      } else {
         return "";
      }
   }
}
