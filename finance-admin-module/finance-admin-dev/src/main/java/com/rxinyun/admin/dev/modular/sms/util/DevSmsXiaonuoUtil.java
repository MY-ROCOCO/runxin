package com.rxinyun.admin.dev.modular.sms.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.api.universal.SupplierConfig;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.dromara.sms4j.dingzhong.config.DingZhongConfig;
import org.dromara.sms4j.javase.config.SEInitializer;
import org.dromara.sms4j.provider.config.SmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevSmsXiaonuoUtil {
   private static final Logger log = LoggerFactory.getLogger(DevSmsXiaonuoUtil.class);
   private static SmsBlend smsBlend;
   private static final String RUNXIN_SMS_XIAONUO_ACCESS_KEY_ID_KEY = "RUNXIN_SMS_XIAONUO_ACCESS_KEY_ID";
   private static final String RUNXIN_SMS_XIAONUO_ACCESS_KEY_SECRET_KEY = "RUNXIN_SMS_XIAONUO_ACCESS_KEY_SECRET";
   private static final String RUNXIN_SMS_XIAONUO_REQUEST_URL_KEY = "RUNXIN_SMS_XIAONUO_REQUEST_URL";
   private static final String RUNXIN_SMS_XIAONUO_DEFAULT_SIGN_NAME_KEY = "RUNXIN_SMS_XIAONUO_DEFAULT_SIGN_NAME";

   private static void initClient(String signName) {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String accessKeyId = devConfigApi.getValueByKey("RUNXIN_SMS_XIAONUO_ACCESS_KEY_ID");
      if (ObjectUtil.isEmpty(accessKeyId)) {
         throw new CommonException("小诺短信操作客户端未正确配置：accessKeyId为空", new Object[0]);
      } else {
         String accessKeySecret = devConfigApi.getValueByKey("RUNXIN_SMS_XIAONUO_ACCESS_KEY_SECRET");
         if (ObjectUtil.isEmpty(accessKeySecret)) {
            throw new CommonException("小诺短信操作客户端未正确配置：accessKeySecret为空", new Object[0]);
         } else {
            String requestUrl = devConfigApi.getValueByKey("RUNXIN_SMS_XIAONUO_REQUEST_URL");
            if (ObjectUtil.isEmpty(requestUrl)) {
               throw new CommonException("小诺短信操作客户端未正确配置：requestUrl为空", new Object[0]);
            } else {
               DingZhongConfig dingZhongConfig = new DingZhongConfig();
               dingZhongConfig.setAccessKeyId(accessKeyId);
               dingZhongConfig.setAccessKeySecret(accessKeySecret);
               dingZhongConfig.setRequestUrl(requestUrl);
               dingZhongConfig.setSignature(signName);
               dingZhongConfig.setConfigId("XIAONUO");
               SEInitializer.initializer().fromConfig(new SmsConfig(), CollectionUtil.newArrayList(new SupplierConfig[]{dingZhongConfig}));
               smsBlend = SmsFactory.getSmsBlend(dingZhongConfig.getConfigId());
            }
         }
      }
   }

   public static String sendSms(String phoneNumbers, String signName, String message) {
      try {
         if (ObjectUtil.isEmpty(signName)) {
            signName = getDefaultSignName();
         }

         initClient(signName);
         SmsResponse smsResponse = smsBlend.massTexting(StrUtil.split(phoneNumbers, ","), message);
         if (smsResponse.isSuccess()) {
            return JSONUtil.toJsonStr(smsResponse.getData());
         } else {
            throw new CommonException("短信发送失败", new Object[0]);
         }
      } catch (Exception var4) {
         throw new CommonException(var4.getMessage(), new Object[0]);
      }
   }

   public static String getDefaultSignName() {
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String signName = devConfigApi.getValueByKey("RUNXIN_SMS_XIAONUO_DEFAULT_SIGN_NAME");
      if (ObjectUtil.isEmpty(signName)) {
         throw new CommonException("小诺短信操作客户端未正确配置：signName为空", new Object[0]);
      } else {
         return signName;
      }
   }
}
