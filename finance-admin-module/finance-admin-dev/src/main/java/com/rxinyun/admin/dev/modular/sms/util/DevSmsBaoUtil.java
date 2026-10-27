package com.rxinyun.admin.dev.modular.sms.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSmsBaoUtil {
   private static final Logger log = LoggerFactory.getLogger(DevSmsBaoUtil.class);
   private static final String SMSBAO_SUCCESS_CODE = "0";
   private static final String RUNXIN_SMS_SMSBAO_ACCESS_KEY_ID_KEY = "RUNXIN_SMS_SMSBAO_ACCESS_KEY_ID";
   private static final String RUNXIN_SMS_SMSBAO_ACCESS_KEY_SECRET_KEY = "RUNXIN_SMS_SMSBAO_ACCESS_KEY_SECRET";
   private static final String RUNXIN_SMS_SMSBAO_REQUEST_URL = "http://api.smsbao.com/sms";

   public static String sendSms(String phone, String content, String signName) {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      log.info("短信宝短信操作客户端初始化成功:{}", JSON.toJSONString(devConfigApi));
      String accessKeyId = devConfigApi.getValueByKey("RUNXIN_SMS_SMSBAO_ACCESS_KEY_ID");
      if (ObjectUtil.isEmpty(accessKeyId)) {
         throw new CommonException("短信宝短信操作客户端未正确配置：accessKeyId为空", new Object[0]);
      } else {
         String accessKeySecret = devConfigApi.getValueByKey("RUNXIN_SMS_SMSBAO_ACCESS_KEY_SECRET");
         if (ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("短信宝短信操作客户端未正确配置：accessKeySecret为空", new Object[0]);
         } else {
            String httpArg = buildHttpArg(accessKeyId, accessKeySecret, phone, content, signName);
            return request("http://api.smsbao.com/sms", httpArg);
         }
      }
   }

   private static String buildHttpArg(String accessKeyId, String accessKeySecret, String phone, String content, String signName) {
      StringBuffer httpArg = new StringBuffer();
      httpArg.append("u=").append(accessKeyId).append("&");
      httpArg.append("p=").append(md5(accessKeySecret)).append("&");
      httpArg.append("m=").append(phone).append("&");
      httpArg.append("c=").append(encodeUrlString("【" + signName + "】" + content, "UTF-8"));
      return httpArg.toString();
   }

   private static String request(String httpUrl, String httpArg) {
      StringBuilder sbf = new StringBuilder();

      try {
         URL url = new URL(httpUrl + "?" + httpArg);
         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
         connection.setRequestMethod("GET");
         connection.setConnectTimeout(10000);
         connection.setReadTimeout(10000);
         connection.connect();
         int statusCode = connection.getResponseCode();
         InputStream stream = statusCode >= 200 && statusCode < 300 ? connection.getInputStream() : connection.getErrorStream();
         if (stream == null) {
            throw new CommonException("短信宝接口请求失败，HTTP状态码：" + statusCode, new Object[0]);
         } else {
            InputStream is = stream;

            String strRead;
            try (
               InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
               BufferedReader br = new BufferedReader(isr);
            ) {
               while ((strRead = br.readLine()) != null) {
                  if (sbf.length() > 0) {
                     sbf.append("\n");
                  }

                  sbf.append(strRead);
               }
            } catch (Throwable var17) {
               if (stream != null) {
                  try {
                     is.close();
                  } catch (Throwable var12) {
                     var17.addSuppressed(var12);
                  }
               }

               throw var17;
            }

            if (stream != null) {
               stream.close();
            }

            String result = sbf.toString();
            if (!"0".equals(result)) {
               throw new CommonException("短信宝发送失败，返回码：" + result, new Object[0]);
            } else {
               return result;
            }
         }
      } catch (CommonException var18) {
         throw var18;
      } catch (Exception var19) {
         log.error("请求短信宝API失败: {}", var19.getMessage(), var19);
         throw new CommonException("请求短信宝API失败：" + var19.getMessage(), new Object[0]);
      }
   }

   private static String md5(String plainText) {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         md.update(plainText.getBytes());
         byte[] b = md.digest();
         StringBuilder buf = new StringBuilder();

         for (byte value : b) {
            int i = value;
            if (value < 0) {
               i = value + 256;
            }

            if (i < 16) {
               buf.append("0");
            }

            buf.append(Integer.toHexString(i));
         }

         return buf.toString();
      } catch (NoSuchAlgorithmException var9) {
         log.error("MD5加密失败: {}", var9.getMessage(), var9);
         return null;
      }
   }

   private static String encodeUrlString(String str, String charset) {
      if (str == null) {
         return null;
      } else {
         try {
            return URLEncoder.encode(str, charset);
         } catch (Exception var3) {
            log.error("URL编码失败: {}", var3.getMessage(), var3);
            return null;
         }
      }
   }
}
