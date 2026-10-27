package com.rxinyun.admin.dev.modular.sms.provider;

import com.rxinyun.admin.dev.modular.sms.param.DevSmsSendParam;
import com.rxinyun.admin.dev.modular.sms.service.DevSmsService;
import com.rxinyun.dev.api.DevSmsApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DevSmsApiProvider implements DevSmsApi {
   @Resource
   private DevSmsService devSmsService;

   public void sendSms(String phoneNumbers, String templateParam) {
      DevSmsSendParam devSmsSendParam = new DevSmsSendParam();
      devSmsSendParam.setPhoneNumbers(phoneNumbers);
      devSmsSendParam.setTemplateParam(templateParam);
      this.devSmsService.sendSms(devSmsSendParam);
   }
}
