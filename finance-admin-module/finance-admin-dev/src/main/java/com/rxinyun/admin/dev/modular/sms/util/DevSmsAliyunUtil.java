package com.rxinyun.admin.dev.modular.sms.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSmsAliyunUtil {
   private static final Logger log = LoggerFactory.getLogger(DevSmsAliyunUtil.class);
   private static Client client;
   private static final String RUNXIN_SMS_ALIYUN_ACCESS_KEY_ID_KEY = "RUNXIN_SMS_ALIYUN_ACCESS_KEY_ID";
   private static final String RUNXIN_SMS_ALIYUN_ACCESS_KEY_SECRET_KEY = "RUNXIN_SMS_ALIYUN_ACCESS_KEY_SECRET";
   private static final String RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME";
   private static final String RUNXIN_SMS_ALIYUN_END_POINT = "dysmsapi.aliyuncs.com";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      log.info("阿里云短信操作客户端初始化成功:{}", JSON.toJSONString(devConfigApi));
      String accessKeyId = devConfigApi.getValueByKey("RUNXIN_SMS_ALIYUN_ACCESS_KEY_ID");
      if (ObjectUtil.isEmpty(accessKeyId)) {
         throw new CommonException("阿里云短信操作客户端未正确配置：accessKeyId为空", new Object[0]);
      } else {
         String accessKeySecret = devConfigApi.getValueByKey("RUNXIN_SMS_ALIYUN_ACCESS_KEY_SECRET");
         if (ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("阿里云短信操作客户端未正确配置：accessKeySecret为空", new Object[0]);
         } else {
            try {
               client = new Client(new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret).setEndpoint("dysmsapi.aliyuncs.com"));
            } catch (Exception var4) {
               throw new CommonException(var4.getMessage(), new Object[0]);
            }
         }
      }
   }

   public static String sendSms(String phoneNumbers, String signName, String templateCode, String templateParam) {
      try {
         initClient();
         if (ObjectUtil.isEmpty(signName)) {
            signName = getDefaultSignName();
         }

         SendSmsRequest sendSmsRequest = new SendSmsRequest()
            .setPhoneNumbers(phoneNumbers)
            .setSignName(signName)
            .setTemplateCode(templateCode)
            .setTemplateParam(templateParam);
         SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
         SendSmsResponseBody body = sendSmsResponse.getBody();
         String code = body.getCode().toLowerCase();
         if ("ok".equals(code)) {
            return JSONUtil.toJsonStr(body);
         } else {
            throw new CommonException(body.getMessage(), new Object[0]);
         }
      } catch (Exception var8) {
         throw new CommonException(var8.getMessage(), new Object[0]);
      }
   }

   public static String getDefaultSignName() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String signName = devConfigApi.getValueByKey("RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME");
      if (ObjectUtil.isEmpty(signName)) {
         throw new CommonException("阿里云短信操作客户端未正确配置：signName为空", new Object[0]);
      } else {
         return signName;
      }
   }
}
