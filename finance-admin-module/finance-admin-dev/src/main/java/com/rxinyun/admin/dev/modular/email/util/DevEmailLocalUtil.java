package com.rxinyun.admin.dev.modular.email.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevEmailLocalUtil {
   private static final Logger log = LoggerFactory.getLogger(DevEmailLocalUtil.class);
   private static MailAccount mailAccount;
   private static final String EMAIL_SMTP_HOST_KEY = "EMAIL_SMTP_HOST";
   private static final String EMAIL_SEND_EMAIL_KEY = "EMAIL_SEND_EMAIL";
   private static final String EMAIL_SMTP_USER_KEY = "EMAIL_SMTP_USER";
   private static final String EMAIL_SEND_NAME_KEY = "EMAIL_SEND_NAME";
   private static final String EMAIL_SMTP_PASSWORD_KEY = "EMAIL_SMTP_PASSWORD";
   private static final String EMAIL_SMTP_PORT_KEY = "EMAIL_SMTP_PORT";
   private static final String EMAIL_SMTP_SSL_ENABLE_KEY = "EMAIL_SMTP_SSL_ENABLE";
   private static final String EMAIL_SMTP_STARTTLS_ENABLE_KEY = "EMAIL_SMTP_STARTTLS_ENABLE";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String from = devConfigApi.getValueByKey("EMAIL_SEND_EMAIL");
      if (StrUtil.isEmpty(from)) {
         throw new CommonException("未配置发件人邮箱，请配置 EMAIL_SEND_EMAIL", new Object[0]);
      } else {
         String pass = devConfigApi.getValueByKey("EMAIL_SMTP_PASSWORD");
         if (StrUtil.isEmpty(pass)) {
            throw new CommonException("未配置 SMTP 密码，请配置 EMAIL_SMTP_PASSWORD", new Object[0]);
         } else {
            String smtpUser = devConfigApi.getValueByKey("EMAIL_SMTP_USER");
            String sendName = devConfigApi.getValueByKey("EMAIL_SEND_NAME");
            String host = devConfigApi.getValueByKey("EMAIL_SMTP_HOST");
            String portStr = devConfigApi.getValueByKey("EMAIL_SMTP_PORT");
            String sslStr = devConfigApi.getValueByKey("EMAIL_SMTP_SSL_ENABLE");
            String tlsStr = devConfigApi.getValueByKey("EMAIL_SMTP_STARTTLS_ENABLE");
            mailAccount = new MailAccount();
            if (StrUtil.isNotEmpty(sendName) && !sendName.trim().equals(from)) {
               mailAccount.setFrom("\"" + sendName.replace("\"", "\\\"") + "\" <" + from + ">");
            } else {
               mailAccount.setFrom(from);
            }

            mailAccount.setUser(StrUtil.blankToDefault(StrUtil.trim(smtpUser), from));
            mailAccount.setPass(pass);
            if (StrUtil.isNotEmpty(host)) {
               mailAccount.setHost(host);
            }

            if (StrUtil.isNotEmpty(portStr)) {
               mailAccount.setPort(Convert.toInt(portStr, 465));
            }

            if (StrUtil.isNotEmpty(sslStr) || StrUtil.isNotEmpty(tlsStr)) {
               boolean sslEnable = "1".equals(StrUtil.trim(sslStr));
               boolean starttlsEnable = "1".equals(StrUtil.trim(tlsStr));
               if (sslEnable && starttlsEnable) {
                  throw new CommonException("SMTP的SSL与TLS不能同时开启，端口465请启用SSL，端口587请启用TLS", new Object[0]);
               }

               mailAccount.setSslEnable(sslEnable);
               mailAccount.setStarttlsEnable(starttlsEnable);
            }
         }
      }
   }

   public static MailAccount getClient() {
      initClient();
      return mailAccount;
   }

   public static String sendTextEmail(String tos, String subject, String content, List<File> files) {
      try {
         initClient();
         return MailUtil.send(mailAccount, tos, subject, content, false, (File[])ArrayUtil.toArray(emptyIfNull(files), File.class));
      } catch (Exception var5) {
         throw new CommonException(var5.getMessage(), new Object[0]);
      }
   }

   public static String sendHtmlEmail(String tos, String subject, String content, Map<String, InputStream> imageMap, List<File> files) {
      try {
         initClient();
         return MailUtil.send(mailAccount, tos, subject, content, imageMap, true, (File[])ArrayUtil.toArray(emptyIfNull(files), File.class));
      } catch (Exception var6) {
         throw new CommonException(var6.getMessage(), new Object[0]);
      }
   }

   private static List<File> emptyIfNull(List<File> files) {
      return (List<File>)ObjectUtil.defaultIfNull(files, Collections.emptyList());
   }
}
