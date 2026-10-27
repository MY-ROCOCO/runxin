package com.rxinyun.common.util;

import com.rxinyun.common.exception.CommonException;
import java.util.concurrent.atomic.AtomicInteger;

public class MacAddressGenerator {
   private static final String PUBLIC_PREFIX = "00:15:5D";
   private static final String PRIVATE_PREFIX = "00:0C:29";
   private static final String NAT_PREFIX = "00:50:56";
   private static final int MAC_SUFFIX_MASK = 16777215;
   private static final AtomicInteger SEQUENCE = new AtomicInteger();

   public static String generateMacAddress(String type) {
      StringBuilder macAddress = new StringBuilder();
      if (type.equals("PUBLIC")) {
         macAddress.append("00:15:5D");
      } else if (type.equals("PRIVATE")) {
         macAddress.append("00:0C:29");
      } else {
         if (!type.equals("NAT")) {
            throw new CommonException("该MAC地址类型不存在: " + type);
         }

         macAddress.append("00:50:56");
      }

      int suffix = generateTimeBasedSuffix();
      macAddress.append(":").append(String.format("%02X", suffix >> 16 & 0xFF));
      macAddress.append(":").append(String.format("%02X", suffix >> 8 & 0xFF));
      macAddress.append(":").append(String.format("%02X", suffix & 0xFF));
      return macAddress.toString();
   }

   private static int generateTimeBasedSuffix() {
      long epochSecond = System.currentTimeMillis() / 1000L;
      int sequence = SEQUENCE.getAndIncrement();
      return (int)(epochSecond + sequence & 16777215L);
   }

   public static void main(String[] args) {
      String macAddress = generateMacAddress("PUBLIC");
      System.out.println("Generated MAC Address: " + macAddress);
   }
}
