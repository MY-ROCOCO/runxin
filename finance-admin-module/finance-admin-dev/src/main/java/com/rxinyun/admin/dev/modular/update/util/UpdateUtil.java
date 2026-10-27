package com.rxinyun.admin.dev.modular.update.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateUtil {
   private static final Logger log = LoggerFactory.getLogger(UpdateUtil.class);

   public static String getAppPath() {
      return SystemUtil.getUserInfo().getCurrentDir();
   }

   public static File createDirectoryIfNotExists(String dirPath) {
      File dir = new File(dirPath);
      if (!dir.exists()) {
         dir.mkdirs();
      }

      return dir;
   }

   public static String backupFile(String filePath, String backupDir) {
      try {
         File file = new File(filePath);
         if (!file.exists()) {
            return null;
         } else {
            File backupDirFile = createDirectoryIfNotExists(backupDir);
            String backupFileName = file.getName() + "." + System.currentTimeMillis() + ".bak";
            String backupFilePath = backupDirFile.getAbsolutePath() + File.separator + backupFileName;
            FileUtil.copy(filePath, backupFilePath, true);
            return backupFilePath;
         }
      } catch (Exception var6) {
         log.error("备份文件失败: {}", filePath, var6);
         return null;
      }
   }

   public static boolean restartApplication(String jarPath) {
      try {
         boolean isWindows = SystemUtil.getOsInfo().isWindows();
         String scriptPath = createRestartScript(jarPath, isWindows);
         if (StrUtil.isBlank(scriptPath)) {
            return false;
         } else {
            if (isWindows) {
               Runtime.getRuntime().exec("cmd /c start " + scriptPath);
            } else {
               Runtime.getRuntime().exec("sh " + scriptPath);
            }

            new Thread(() -> {
               try {
                  Thread.sleep(3000L);
                  System.exit(0);
               } catch (InterruptedException var1x) {
                  log.error("延迟退出失败", var1x);
               }
            }).start();
            return true;
         }
      } catch (Exception var3) {
         log.error("重启应用程序失败", var3);
         return false;
      }
   }

   private static String createRestartScript(String jarPath, boolean isWindows) {
      try {
         String scriptExt = isWindows ? ".bat" : ".sh";
         String scriptPath = SystemUtil.getUserInfo().getTempDir() + File.separator + "restart" + System.currentTimeMillis() + scriptExt;
         List<String> lines = new ArrayList<>();
         if (isWindows) {
            lines.add("@echo off");
            lines.add("ping 127.0.0.1 -n 3 > nul");
            lines.add("cd /d " + new File(jarPath).getParent());
            lines.add("java -jar " + jarPath);
            lines.add("del %0");
         } else {
            lines.add("#!/bin/bash");
            lines.add("sleep 3");
            lines.add("cd " + new File(jarPath).getParent());
            lines.add("java -jar " + jarPath + " &");
            lines.add("rm -f $0");
         }

         Path path = Paths.get(scriptPath);
         Files.write(path, lines);
         if (!isWindows) {
            File scriptFile = new File(scriptPath);
            scriptFile.setExecutable(true);
         }

         return scriptPath;
      } catch (IOException var7) {
         log.error("创建重启脚本失败", var7);
         return null;
      }
   }
}
