package com.rxinyun.admin.dev.modular.sms.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSmsTencentUtil {
   private static final Logger log = LoggerFactory.getLogger(DevSmsTencentUtil.class);
   private static SmsClient client;
   private static final String RUNXIN_SMS_TENCENT_SECRET_ID_KEY = "RUNXIN_SMS_TENCENT_SECRET_ID";
   private static final String RUNXIN_SMS_TENCENT_SECRET_KEY_KEY = "RUNXIN_SMS_TENCENT_SECRET_KEY";
   private static final String RUNXIN_SMS_TENCENT_REGION_ID_KEY = "RUNXIN_SMS_TENCENT_REGION_ID";
   private static final String RUNXIN_SMS_TENCENT_DEFAULT_SDK_APP_ID_KEY = "RUNXIN_SMS_TENCENT_DEFAULT_SDK_APP_ID";
   private static final String RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME";

   private static void initClient() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String secretId = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_SECRET_ID");
      if (ObjectUtil.isEmpty(secretId)) {
         throw new CommonException("腾讯云短信操作客户端未正确配置：secretId为空", new Object[0]);
      } else {
         String secretKey = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_SECRET_KEY");
         if (ObjectUtil.isEmpty(secretKey)) {
            throw new CommonException("腾讯云短信操作客户端未正确配置：secretKey为空", new Object[0]);
         } else {
            String regionId = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_REGION_ID");
            if (ObjectUtil.isEmpty(regionId)) {
               throw new CommonException("腾讯云短信操作客户端未正确配置：regionId为空", new Object[0]);
            } else {
               client = new SmsClient(new Credential(secretId, secretKey), regionId);
            }
         }
      }
   }

   public static String sendSms(String sdkAppId, String phoneNumbers, String signName, String templateCode, String templateParam) {
      try {
         initClient();
         if (ObjectUtil.isEmpty(sdkAppId)) {
            sdkAppId = getDefaultSdkAppId();
         }

         if (ObjectUtil.isEmpty(signName)) {
            signName = getDefaultSignName();
         }

         SendSmsRequest sendSmsRequest = new SendSmsRequest();
         sendSmsRequest.setSmsSdkAppId(sdkAppId);
         sendSmsRequest.setPhoneNumberSet(StrUtil.splitToArray(phoneNumbers, ","));
         sendSmsRequest.setSignName(signName);
         sendSmsRequest.setTemplateId(templateCode);
         sendSmsRequest.setTemplateParamSet(ObjectUtil.isNotEmpty(templateParam) ? StrUtil.splitToArray(templateParam, ",") : null);
         SendSmsResponse sendSmsResponse = client.SendSms(sendSmsRequest);
         SendStatus sendStatus = sendSmsResponse.getSendStatusSet()[0];
         String code = sendStatus.getCode().toLowerCase();
         if ("ok".equals(code)) {
            return JSONUtil.toJsonStr(sendSmsResponse);
         } else {
            throw new CommonException(sendStatus.getMessage(), new Object[0]);
         }
      } catch (Exception var9) {
         throw new CommonException(var9.getMessage(), new Object[0]);
      }
   }

   public static String getDefaultSignName() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String signName = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_DEFAULT_SIGN_NAME");
      if (ObjectUtil.isEmpty(signName)) {
         throw new CommonException("腾讯云短信操作客户端未正确配置：signName为空", new Object[0]);
      } else {
         return signName;
      }
   }

   public static String getDefaultSdkAppId() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String sdkAppId = devConfigApi.getValueByKey("RUNXIN_SMS_TENCENT_DEFAULT_SDK_APP_ID");
      if (ObjectUtil.isEmpty(sdkAppId)) {
         throw new CommonException("腾讯云短信操作客户端未正确配置：sdkAppId为空", new Object[0]);
      } else {
         return sdkAppId;
      }
   }
}
