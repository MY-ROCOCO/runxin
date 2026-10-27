package com.rxinyun.common.util;

import cn.hutool.core.util.StrUtil;
import java.util.concurrent.ThreadLocalRandom;

public class CloudServerPortUtil {
   public static final String PORT_MODE_FIXED = "FIXED";
   public static final String PORT_MODE_RANDOM = "RANDOM";

   private CloudServerPortUtil() {
   }

   public static int resolvePort(String portMode, String portRange, int defaultPort) {
      String mode = StrUtil.trimToEmpty(portMode);
      if (!"RANDOM".equalsIgnoreCase(mode) && !"随机".equals(mode)) {
         return defaultPort;
      } else {
         int[] range = parsePortRange(portRange);
         return range == null ? defaultPort : ThreadLocalRandom.current().nextInt(range[0], range[1] + 1);
      }
   }

   private static int[] parsePortRange(String portRange) {
      if (StrUtil.isBlank(portRange)) {
         return null;
      } else {
         String normalized = portRange.trim().replace("，", ",").replace("~", "-").replace("至", "-");
         String[] parts = normalized.contains("-") ? normalized.split("-", 2) : normalized.split(",", 2);
         if (parts.length != 2) {
            return null;
         } else {
            try {
               int min = Integer.parseInt(parts[0].trim());
               int max = Integer.parseInt(parts[1].trim());
               if (min > max) {
                  int tmp = min;
                  min = max;
                  max = tmp;
               }

               return isValidPort(min) && isValidPort(max) ? new int[]{min, max} : null;
            } catch (NumberFormatException var6) {
               return null;
            }
         }
      }
   }

   private static boolean isValidPort(int port) {
      return port > 0 && port <= 65535;
   }
}
