package com.rxinyun.admin.dev.modular.email.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ses.v20201002.SesClient;
import com.tencentcloudapi.ses.v20201002.models.Attachment;
import com.tencentcloudapi.ses.v20201002.models.BatchSendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.SendEmailRequest;
import com.tencentcloudapi.ses.v20201002.models.Simple;
import com.tencentcloudapi.ses.v20201002.models.Template;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevEmailTencentUtil {
   private static final Logger log = LoggerFactory.getLogger(DevEmailTencentUtil.class);
   private static SesClient client;
   private static final String RUNXIN_EMAIL_TENCENT_SECRET_ID_KEY = "RUNXIN_EMAIL_TENCENT_SECRET_ID";
   private static final String RUNXIN_EMAIL_TENCENT_SECRET_KEY_KEY = "RUNXIN_EMAIL_TENCENT_SECRET_KEY";
   private static final String RUNXIN_EMAIL_TENCENT_REGION_ID_KEY = "RUNXIN_EMAIL_TENCENT_REGION_ID";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String secretId = devConfigApi.getValueByKey("RUNXIN_EMAIL_TENCENT_SECRET_ID");
      if (ObjectUtil.isEmpty(secretId)) {
         throw new CommonException("腾讯云邮件操作客户端未正确配置：secretId为空", new Object[0]);
      } else {
         String secretKey = devConfigApi.getValueByKey("RUNXIN_EMAIL_TENCENT_SECRET_KEY");
         if (ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("腾讯云邮件操作客户端未正确配置：secretKey为空", new Object[0]);
         } else {
            String regionId = devConfigApi.getValueByKey("RUNXIN_EMAIL_TENCENT_REGION_ID");
            if (ObjectUtil.isEmpty(regionId)) {
               throw new CommonException("腾讯云邮件操作客户端未正确配置：regionId为空", new Object[0]);
            } else {
               client = new SesClient(new Credential(secretId, secretKey), regionId);
            }
         }
      }
   }

   public static String sendTextEmail(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {
      try {
         initClient();
         SendEmailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, false, attachmentList);
         return client.SendEmail(singleSendMailRequest).getMessageId();
      } catch (TencentCloudSDKException var7) {
         throw new CommonException(var7.getMessage(), new Object[0]);
      }
   }

   public static String sendHtmlEmail(String from, String user, String tos, String subject, String content, List<JSONObject> attachmentList) {
      try {
         initClient();
         SendEmailRequest singleSendMailRequest = createSingleSendRequest(from, user, tos, subject, content, true, attachmentList);
         return client.SendEmail(singleSendMailRequest).getMessageId();
      } catch (TencentCloudSDKException var7) {
         throw new CommonException(var7.getMessage(), new Object[0]);
      }
   }

   public static String sendEmailWithTemplate(
      String from, String user, String toId, String templateId, String templateParam, String subject, List<JSONObject> attachmentList
   ) {
      try {
         initClient();
         BatchSendEmailRequest batchSendEmailRequest = createBatchSendRequest(from, user, toId, templateId, templateParam, subject, attachmentList);
         return client.BatchSendEmail(batchSendEmailRequest).getTaskId().toString();
      } catch (TencentCloudSDKException var8) {
         throw new CommonException(var8.getMessage(), new Object[0]);
      }
   }

   private static SendEmailRequest createSingleSendRequest(
      String from, String user, String tos, String subject, String content, boolean isHtml, List<JSONObject> attachmentList
   ) {
      SendEmailRequest sendEmailRequest = new SendEmailRequest();
      sendEmailRequest.setFromEmailAddress(ObjectUtil.isNotEmpty(user) ? user + " <" + from + ">" : from);
      sendEmailRequest.setDestination(StrUtil.splitToArray(tos, ","));
      sendEmailRequest.setSubject(subject);
      Simple simple = new Simple();
      if (isHtml) {
         simple.setHtml(Base64.encode(content));
      } else {
         simple.setText(Base64.encode(content));
      }

      sendEmailRequest.setSimple(simple);
      if (ObjectUtil.isNotEmpty(attachmentList)) {
         Attachment[] attachments = (Attachment[])attachmentList.stream().map(jsonObject -> {
            Attachment attachment = new Attachment();
            BeanUtil.copyProperties(jsonObject, attachment, new String[0]);
            return attachment;
         }).toArray();
         sendEmailRequest.setAttachments(attachments);
      }

      return sendEmailRequest;
   }

   private static BatchSendEmailRequest createBatchSendRequest(
      String from, String user, String toId, String templateId, String templateParam, String subject, List<JSONObject> attachmentList
   ) {
      BatchSendEmailRequest batchSendEmailRequest = new BatchSendEmailRequest();
      batchSendEmailRequest.setFromEmailAddress(ObjectUtil.isNotEmpty(user) ? user + " <" + from + ">" : from);
      batchSendEmailRequest.setReceiverId(Convert.toLong(toId));
      Template template = new Template();
      template.setTemplateID(Convert.toLong(templateId));
      template.setTemplateData(templateParam);
      batchSendEmailRequest.setTemplate(template);
      batchSendEmailRequest.setSubject(subject);
      batchSendEmailRequest.setTaskType(1L);
      if (ObjectUtil.isNotEmpty(attachmentList)) {
         Attachment[] attachments = (Attachment[])attachmentList.stream().map(jsonObject -> {
            Attachment attachment = new Attachment();
            BeanUtil.copyProperties(jsonObject, attachment, new String[0]);
            return attachment;
         }).toArray();
         batchSendEmailRequest.setAttachments(attachments);
      }

      return batchSendEmailRequest;
   }
}
