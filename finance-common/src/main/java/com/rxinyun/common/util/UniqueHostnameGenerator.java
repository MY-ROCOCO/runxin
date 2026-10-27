package com.rxinyun.common.util;

import java.util.HashSet;
import java.util.Random;

public class UniqueHostnameGenerator {
   private static final HashSet<String> usedHostnames = new HashSet<>();
   private static final String PREFIX = "ecs_";
   private static final Random random = new Random();

   public static String generateUniqueHostname(int length) {
      String hostname = null;

      while (hostname == null || usedHostnames.contains(hostname)) {
         hostname = generateRandomString(length);
      }

      usedHostnames.add(hostname);
      return hostname;
   }

   private static String generateRandomString(int length) {
      StringBuilder sb = new StringBuilder(length);

      for (int i = 0; i < length; i++) {
         int randomInt = random.nextInt(36);
         if (randomInt < 10) {
            sb.append(randomInt);
         } else {
            sb.append((char)(97 + randomInt - 10));
         }
      }

      return sb.toString();
   }

   public static void main(String[] args) {
      String newHostname = generateUniqueHostname(10);
      System.out.println("New unique hostname: " + newHostname);
   }
}
