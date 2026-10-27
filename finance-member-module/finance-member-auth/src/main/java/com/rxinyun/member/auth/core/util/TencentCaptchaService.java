package com.rxinyun.member.auth.core.util;

import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.tencentcloudapi.captcha.v20190722.CaptchaClient;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultRequest;
import com.tencentcloudapi.captcha.v20190722.models.DescribeCaptchaResultResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TencentCaptchaService {
   private static final Logger log = LoggerFactory.getLogger(TencentCaptchaService.class);
   @Autowired
   private DevConfigApi devConfigApi;

   public boolean verifyTicket(String ticket, String randstr) {
      if (ticket != null && randstr != null) {
         try {
            String secretId = this.devConfigApi.getValueByKey("TENCENT_CAPTCHA_SECRET_ID");
            String secretKey = this.devConfigApi.getValueByKey("TENCENT_CAPTCHA_SECRET_KEY");
            String captchaAppIdStr = this.devConfigApi.getValueByKey("TENCENT_CAPTCHA_APP_ID");
            String appSecretKey = this.devConfigApi.getValueByKey("TENCENT_CAPTCHA_APP_SECRET_KEY");
            if (secretId != null && secretKey != null && captchaAppIdStr != null && appSecretKey != null) {
               Long captchaAppId = Long.valueOf(captchaAppIdStr);
               Credential cred = new Credential(secretId, secretKey);
               HttpProfile httpProfile = new HttpProfile();
               httpProfile.setEndpoint("captcha.tencentcloudapi.com");
               ClientProfile profile = new ClientProfile();
               profile.setHttpProfile(httpProfile);
               CaptchaClient client = new CaptchaClient(cred, "", profile);
               DescribeCaptchaResultRequest req = new DescribeCaptchaResultRequest();
               req.setCaptchaType(9L);
               req.setTicket(ticket);
               HttpServletRequest request = CommonServletUtil.getRequest();
               String ip = CommonIpAddressUtil.getIp(request);
               req.setUserIp(ip);
               req.setRandstr(randstr);
               req.setCaptchaAppId(captchaAppId);
               req.setAppSecretKey(appSecretKey);
               DescribeCaptchaResultResponse resp = client.DescribeCaptchaResult(req);
               log.info("腾讯云验证码验证结果：{}", resp);
               if (resp != null && "OK".equals(resp.getCaptchaMsg())) {
                  log.info("验证码验证成功");
                  return true;
               } else {
                  log.error("腾讯云验证码验证失败");
                  return false;
               }
            } else {
               log.error("腾讯云验证码配置缺失");
               return false;
            }
         } catch (Exception var16) {
            log.error("腾讯云验证码验证失败");
            return false;
         }
      } else {
         log.error("参数不能为空");
         return false;
      }
   }
}
