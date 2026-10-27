package com.rxinyun.admin.dev.modular.telegram.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevTelegramUtil {
   private static final Logger log = LoggerFactory.getLogger(DevTelegramUtil.class);

   private DevTelegramUtil() {
   }

   public static String sendMessage(String botToken, String chatId, String content) {
      return sendMessage(botToken, chatId, content, null);
   }

   public static String sendMessage(String botToken, String chatId, String content, String parseMode) {
      if (StrUtil.isBlank(botToken)) {
         throw new CommonException("未配置Telegram机器人Token，请配置 TELEGRAM_BOT_TOKEN", new Object[0]);
      } else if (StrUtil.isBlank(chatId)) {
         throw new CommonException("Telegram chatId不能为空", new Object[0]);
      } else if (StrUtil.isBlank(content)) {
         throw new CommonException("Telegram消息内容不能为空", new Object[0]);
      } else {
         HttpURLConnection connection = null;

         String var10;
         try {
            URL url = new URL("https://api.telegram.org/bot" + botToken + "/sendMessage");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            JSONObject body = new JSONObject();
            body.set("chat_id", chatId);
            body.set("text", content);
            if (StrUtil.isNotBlank(parseMode)) {
               body.set("parse_mode", parseMode);
            }

            body.set("disable_web_page_preview", true);
            byte[] payload = JSONUtil.toJsonStr(body).getBytes(StandardCharsets.UTF_8);

            try (OutputStream outputStream = connection.getOutputStream()) {
               outputStream.write(payload);
            }

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            if (responseCode < 200 || responseCode >= 300) {
               throw new CommonException("Telegram发送失败：HTTP {} {}", new Object[]{responseCode, responseMessage});
            }

            log.info("Telegram消息发送成功，chatId={}", chatId);
            var10 = "HTTP " + responseCode + " " + responseMessage;
         } catch (Exception var18) {
            if (var18 instanceof CommonException) {
               throw (CommonException)var18;
            }

            throw new CommonException("Telegram发送异常：{}", new Object[]{var18.getMessage()});
         } finally {
            if (connection != null) {
               connection.disconnect();
            }
         }

         return var10;
      }
   }
}
