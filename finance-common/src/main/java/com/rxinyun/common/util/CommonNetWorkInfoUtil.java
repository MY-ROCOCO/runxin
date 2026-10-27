package com.rxinyun.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.system.SystemUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonNetWorkInfoUtil {
   private static final Logger log = LoggerFactory.getLogger(CommonNetWorkInfoUtil.class);
   private static final int SLEEP_SECONDS = 2;

   public static Map<String, String> getNetworkUpRate() {
      Map<String, String> result = new HashMap<>();
      Process pro = null;
      Runtime r = Runtime.getRuntime();
      BufferedReader input = null;

      try {
         boolean isWindows = SystemUtil.getOsInfo().isWindows();
         String command = isWindows ? "netstat -e" : "ifconfig";
         pro = r.exec(command);
         input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
         long[] result1 = readInLine(input, isWindows);
         Thread.sleep(2000L);
         pro.destroy();
         input.close();
         pro = r.exec(command);
         input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
         long[] result2 = readInLine(input, isWindows);
         String upSpeed = FileUtil.readableFileSize(Convert.toLong(NumberUtil.div(NumberUtil.sub((float)result2[0], (float)result1[0]), 2.0F)));
         String downSpeed = FileUtil.readableFileSize(Convert.toLong(NumberUtil.div(NumberUtil.sub((float)result2[1], (float)result1[1]), 2.0F)));
         result.put("UP", upSpeed + (upSpeed.endsWith("B") ? "/S" : "B/S"));
         result.put("DOWN", downSpeed + (downSpeed.endsWith("B") ? "/S" : "B/S"));
      } catch (Exception var18) {
         log.info(">>> 网络测速失败：", var18);
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (IOException var17) {
               log.info(">>> 网络测速失败：", var17);
            }
         }

         Optional.ofNullable(pro).ifPresent(Process::destroy);
      }

      return result;
   }

   private static String formatNumber(double f) {
      return new Formatter().format("%.2f", f).toString();
   }

   private static long[] readInLine(BufferedReader input, boolean isWindows) {
      long[] arr = new long[2];

      try {
         if (isWindows) {
            input.readLine();
            input.readLine();
            input.readLine();
            input.readLine();
            StringTokenizer tokenStat = new StringTokenizer(input.readLine());
            tokenStat.nextToken();
            arr[0] = Long.parseLong(tokenStat.nextToken());
            arr[1] = Long.parseLong(tokenStat.nextToken());
         } else {
            long rx = 0L;
            long tx = 0L;
            String line = null;

            while ((line = input.readLine()) != null) {
               if (line.contains("RX packets")) {
                  rx += Long.parseLong(line.substring(line.indexOf("RX packets") + 11, line.indexOf(" ", line.indexOf("RX packets") + 11)));
               } else if (line.contains("TX packets")) {
                  tx += Long.parseLong(line.substring(line.indexOf("TX packets") + 11, line.indexOf(" ", line.indexOf("TX packets") + 11)));
               }
            }

            arr[0] = rx;
            arr[1] = tx;
         }
      } catch (Exception var9) {
         log.error(">>> 网络测速异常：", var9);
      }

      return arr;
   }
}
