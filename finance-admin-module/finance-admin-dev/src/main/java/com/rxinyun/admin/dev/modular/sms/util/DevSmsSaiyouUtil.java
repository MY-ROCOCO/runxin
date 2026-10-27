package com.rxinyun.admin.dev.modular.sms.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSmsSaiyouUtil {
   private static final Logger log = LoggerFactory.getLogger(DevSmsSaiyouUtil.class);
   private static final String RUNXIN_SMS_SAIYOU_ACCESS_KEY_ID_KEY = "RUNXIN_SMS_SAIYOU_ACCESS_KEY_ID";
   private static final String RUNXIN_SMS_SAIYOU_ACCESS_KEY_SECRET_KEY = "RUNXIN_SMS_SAIYOU_ACCESS_KEY_SECRET";
   private static final String SAIYOU_SMS_SEND_URL = "https://api-v4.mysubmail.com/sms/send.json";

   public static String sendSms(String phoneNumbers, String content, String signName) {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String appid = devConfigApi.getValueByKey("RUNXIN_SMS_SAIYOU_ACCESS_KEY_ID");
      if (ObjectUtil.isEmpty(appid)) {
         throw new CommonException("赛邮短信未正确配置：appid 为空", new Object[0]);
      } else {
         String appkey = devConfigApi.getValueByKey("RUNXIN_SMS_SAIYOU_ACCESS_KEY_SECRET");
         if (ObjectUtil.isEmpty(appkey)) {
            throw new CommonException("赛邮短信未正确配置：appkey 为空", new Object[0]);
         } else {
            String fullContent = "【" + signName + "】" + content;
            List<String> phones = StrUtil.splitTrim(phoneNumbers, ',');
            List<String> results = new ArrayList<>(phones.size());

            for (String to : phones) {
               if (!StrUtil.isBlank(to)) {
                  String body = buildJsonBody(appid, appkey, to.trim(), fullContent);
                  String one = requestPost("https://api-v4.mysubmail.com/sms/send.json", body);
                  results.add(one);
               }
            }

            return results.isEmpty() ? "" : String.join("; ", results);
         }
      }
   }

   private static String buildJsonBody(String appid, String appkey, String to, String content) {
      StringBuilder sb = new StringBuilder(256);
      sb.append("{\"appid\":\"").append(escapeJson(appid)).append("\"");
      sb.append(",\"signature\":\"").append(escapeJson(appkey)).append("\"");
      sb.append(",\"to\":\"").append(escapeJson(to)).append("\"");
      sb.append(",\"content\":\"").append(escapeJson(content)).append("\"}");
      return sb.toString();
   }

   private static String escapeJson(String s) {
      return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
   }

   private static String requestPost(String httpUrl, String jsonBody) {
      StringBuilder sbf = new StringBuilder();

      try {
         URL url = new URL(httpUrl);
         HttpURLConnection conn = (HttpURLConnection)url.openConnection();
         conn.setRequestMethod("POST");
         conn.setDoOutput(true);
         conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
         conn.setConnectTimeout(10000);
         conn.setReadTimeout(10000);

         try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
         }

         int code = conn.getResponseCode();
         InputStream is = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
         if (is == null) {
            return "response code=" + code;
         }

         String line;
         try (
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
         ) {
            while ((line = br.readLine()) != null) {
               if (sbf.length() > 0) {
                  sbf.append("\n");
               }

               sbf.append(line);
            }
         }
      } catch (Exception var17) {
         log.error("请求赛邮 API 失败: {}", var17.getMessage(), var17);
         throw new CommonException("赛邮发送失败: " + var17.getMessage(), new Object[0]);
      }

      return sbf.toString();
   }
}
