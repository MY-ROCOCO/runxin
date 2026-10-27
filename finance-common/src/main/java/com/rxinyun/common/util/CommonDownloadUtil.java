package com.rxinyun.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonDownloadUtil {
   private static final Logger log = LoggerFactory.getLogger(CommonDownloadUtil.class);

   public static void download(File file, HttpServletResponse response) {
      download(file.getName(), FileUtil.readBytes(file), response);
   }

   public static void download(String fileName, byte[] fileBytes, HttpServletResponse response) {
      try {
         response.setHeader("Content-Disposition", "attachment;filename=" + URLUtil.encode(fileName));
         response.addHeader("Content-Length", fileBytes.length + "");
         response.setHeader("Access-Control-Allow-Origin", "*");
         response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
         response.setContentType("application/octet-stream;charset=UTF-8");
         IoUtil.write(response.getOutputStream(), true, fileBytes);
      } catch (IOException var4) {
         log.error(">>> 文件下载异常：", var4);
      }
   }
}
