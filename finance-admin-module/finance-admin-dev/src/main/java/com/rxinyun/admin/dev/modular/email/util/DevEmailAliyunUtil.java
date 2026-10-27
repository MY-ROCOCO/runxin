package com.rxinyun.admin.dev.modular.email.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.aliyun.dm20151123.Client;
import com.aliyun.dm20151123.models.BatchSendMailRequest;
import com.aliyun.dm20151123.models.SingleSendMailRequest;
import com.aliyun.teaopenapi.models.Config;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevEmailAliyunUtil {
   private static final Logger log = LoggerFactory.getLogger(DevEmailAliyunUtil.class);
   private static Client client;
   private static final String RUNXIN_EMAIL_ALIYUN_ACCESS_KEY_ID_KEY = "RUNXIN_EMAIL_ALIYUN_ACCESS_KEY_ID";
   private static final String RUNXIN_EMAIL_ALIYUN_ACCESS_KEY_SECRET_KEY = "RUNXIN_EMAIL_ALIYUN_ACCESS_KEY_SECRET";
   private static final String RUNXIN_EMAIL_ALIYUN_REGION_ID_KEY = "RUNXIN_EMAIL_ALIYUN_REGION_ID";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String accessKeyId = devConfigApi.getValueByKey("RUNXIN_EMAIL_ALIYUN_ACCESS_KEY_ID");
      if (ObjectUtil.isEmpty(accessKeyId)) {
         throw new CommonException("阿里云邮件操作客户端未正确配置：accessKeyId为空", new Object[0]);
      } else {
         String accessKeySecret = devConfigApi.getValueByKey("RUNXIN_EMAIL_ALIYUN_ACCESS_KEY_SECRET");
         if (ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("阿里云邮件操作客户端未正确配置：accessKeySecret为空", new Object[0]);
         } else {
            String regionId = devConfigApi.getValueByKey("RUNXIN_EMAIL_ALIYUN_REGION_ID");
            if (ObjectUtil.isEmpty(regionId)) {
               throw new CommonException("阿里云邮件操作客户端未正确配置：regionId为空", new Object[0]);
            } else {
               try {
                  client = new Client(new Config().setRegionId(regionId).setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret));
               } catch (Exception var5) {
                  throw new CommonException(var5.getMessage(), new Object[0]);
               }
            }
         }
      }
   }

   public static String sendTextEmail(String from, String user, String tos, String subject, String content) {
      try {
         initClient();
         SingleSendMailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, false);
         return client.singleSendMail(singleSendMailRequest).getBody().getEnvId();
      } catch (Exception var6) {
         throw new CommonException(var6.getMessage(), new Object[0]);
      }
   }

   public static String sendHtmlEmail(String from, String user, String tos, String subject, String content) {
      try {
         initClient();
         SingleSendMailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, true);
         return client.singleSendMail(singleSendMailRequest).getBody().getEnvId();
      } catch (Exception var6) {
         throw new CommonException(var6.getMessage(), new Object[0]);
      }
   }

   public static String sendEmailWithTemplate(String from, String tagName, String toName, String templateName) {
      try {
         initClient();
         BatchSendMailRequest batchSendMailRequest = createBatchSendRequest(from, tagName, toName, templateName);
         return client.batchSendMail(batchSendMailRequest).getBody().getEnvId();
      } catch (Exception var5) {
         throw new CommonException(var5.getMessage(), new Object[0]);
      }
   }

   private static SingleSendMailRequest createSingleSendRequest(String from, String user, String tos, String subject, String content, boolean isHtml) {
      SingleSendMailRequest request = new SingleSendMailRequest();
      request.setAccountName(from);
      request.setFromAlias(user);
      request.setAddressType(1);
      request.setReplyToAddress(true);
      request.setToAddress(tos);
      request.setSubject(subject);
      if (isHtml) {
         request.setHtmlBody(content);
      } else {
         request.setTextBody(content);
      }

      request.setClickTrace("0");
      return request;
   }

   private static BatchSendMailRequest createBatchSendRequest(String from, String tagName, String toName, String templateName) {
      BatchSendMailRequest request = new BatchSendMailRequest();
      request.setAccountName(from);
      request.setReceiversName(toName);
      request.setTemplateName(templateName);
      request.setAddressType(1);
      request.setTagName(tagName);
      request.setClickTrace("0");
      return request;
   }
}
