package com.rxinyun.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeIdGenerator {
   private static final long START_TIMESTAMP = 1640995200000L;
   private static final long SEQUENCE_BIT = 12L;
   private static final long MACHINE_BIT = 10L;
   private static final long MAX_SEQUENCE = 4095L;
   private static final long MAX_MACHINE_NUM = 1023L;
   private static final long MACHINE_LEFT = 12L;
   private static final long TIMESTAMP_LEFT = 22L;
   private static long machineId = getMachineNum();
   private static long sequence = 0L;
   private static long lastTimestamp = -1L;
   private static final AtomicLong ATOMIC_SEQUENCE = new AtomicLong(0L);
   private static final Random RANDOM = new Random();

   public static synchronized String generateId() {
      long currTimestamp = getNewTimestamp();
      if (currTimestamp < lastTimestamp) {
         throw new RuntimeException("Clock moved backwards. Refusing to generate id");
      } else {
         if (currTimestamp == lastTimestamp) {
            sequence = sequence + 1L & 4095L;
            if (sequence == 0L) {
               currTimestamp = getNextTimestamp();
            }
         } else {
            sequence = 0L;
         }

         lastTimestamp = currTimestamp;
         return String.valueOf(currTimestamp - 1640995200000L << 22 | machineId << 12 | sequence);
      }
   }

   private static long getNewTimestamp() {
      return System.currentTimeMillis();
   }

   private static long getNextTimestamp() {
      long timestamp = getNewTimestamp();

      while (timestamp <= lastTimestamp) {
         timestamp = getNewTimestamp();
      }

      return timestamp;
   }

   private static long getMachineNum() {
      try {
         InetAddress ip = InetAddress.getLocalHost();
         NetworkInterface network = NetworkInterface.getByInetAddress(ip);
         if (network == null) {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
               network = interfaces.nextElement();
               if (network.getHardwareAddress() != null) {
                  break;
               }
            }
         }

         if (network != null && network.getHardwareAddress() != null) {
            byte[] mac = network.getHardwareAddress();
            long machineId = (mac[0] & 255L) << 40
               | (mac[1] & 255L) << 32
               | (mac[2] & 255L) << 24
               | (mac[3] & 255L) << 16
               | (mac[4] & 255L) << 8
               | mac[5] & 255L;
            return machineId & 1023L;
         }
      } catch (Exception var5) {
      }

      return RANDOM.nextInt(1024);
   }

   static {
      if (machineId > 1023L || machineId < 0L) {
         machineId = RANDOM.nextInt(1024);
      }
   }
}
