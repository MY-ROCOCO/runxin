package com.rxinyun.admin.dev.modular.email.provider;

import com.rxinyun.admin.dev.modular.email.param.DevEmailSendLocalHtmlParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailSendLocalTxtParam;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.dev.api.DevEmailApi;
import jakarta.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DevEmailApiProvider implements DevEmailApi {
   @Resource
   private DevEmailService devEmailService;

   public void sendTextEmailLocal(String tos, String subject, String content, List<File> files) {
      DevEmailSendLocalTxtParam devEmailSendLocalTxtParam = new DevEmailSendLocalTxtParam();
      devEmailSendLocalTxtParam.setReceiveAccounts(tos);
      devEmailSendLocalTxtParam.setSubject(subject);
      devEmailSendLocalTxtParam.setContent(content);
      devEmailSendLocalTxtParam.setFiles(files);
      this.devEmailService.sendLocal(devEmailSendLocalTxtParam);
   }

   public void sendHtmlEmailLocal(String tos, String subject, String content, Map<String, InputStream> imageMap, List<File> files) {
      DevEmailSendLocalHtmlParam devEmailSendLocalHtmlParam = new DevEmailSendLocalHtmlParam();
      devEmailSendLocalHtmlParam.setReceiveAccounts(tos);
      devEmailSendLocalHtmlParam.setSubject(subject);
      devEmailSendLocalHtmlParam.setContent(content);
      devEmailSendLocalHtmlParam.setImageMap(imageMap);
      devEmailSendLocalHtmlParam.setFiles(files);
      this.devEmailService.sendLocal(devEmailSendLocalHtmlParam);
   }
}
