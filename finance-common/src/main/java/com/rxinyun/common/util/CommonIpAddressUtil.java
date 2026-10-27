package com.rxinyun.common.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.rxinyun.common.exception.CommonException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonIpAddressUtil {
   private static final Logger log = LoggerFactory.getLogger(CommonIpAddressUtil.class);
   private static final String LOCAL_REMOTE_HOST = "0:0:0:0:0:0:0:1";
   private static final String IP2REGION_DB_PATH = "/ip2region.xdb";
   private static final Searcher searcher = initSearcher();

   private CommonIpAddressUtil() {
   }

   private static Searcher initSearcher() {
      try {
         File dbFile = FileUtil.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + "/ip2region.xdb");
         if (!FileUtil.exist(dbFile)) {
            try (InputStream inputStream = CommonIpAddressUtil.class.getResourceAsStream("/ip2region.xdb")) {
               FileUtil.writeFromStream(inputStream, dbFile);
            }
         }

         byte[] cBuff = Searcher.loadContentFromFile(dbFile.getPath());
         return Searcher.newWithBuffer(cBuff);
      } catch (Exception var6) {
         log.error(">>> CommonIpAddressUtil初始化异常：", var6);
         throw new CommonException("CommonIpAddressUtil初始化异常");
      }
   }

   public static String getIp(HttpServletRequest request) {
      if (ObjectUtil.isEmpty(request)) {
         return "127.0.0.1";
      } else {
         try {
            String remoteHost = JakartaServletUtil.getClientIP(request, new String[0]);
            return "0:0:0:0:0:0:0:1".equals(remoteHost) ? "127.0.0.1" : remoteHost;
         } catch (Exception var2) {
            log.error(">>> 获取客户端ip异常：", var2);
            return "127.0.0.1";
         }
      }
   }

   public static String getCityInfo(String ip) {
      try {
         String region = searcher.search(ip.trim());
         return region.replace("0|", "").replace("|0", "");
      } catch (Exception var2) {
         return "未知";
      }
   }
}
