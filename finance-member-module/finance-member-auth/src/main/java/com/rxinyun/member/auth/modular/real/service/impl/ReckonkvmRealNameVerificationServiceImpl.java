package com.rxinyun.member.auth.modular.real.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.enums.CertificationStatusEnum;
import com.rxinyun.common.enums.UserTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.real.service.MemberCompanyRealInfoService;
import com.rxinyun.member.auth.modular.real.service.MemberPersonalRealInfoService;
import com.rxinyun.member.auth.modular.real.service.ReckonkvmRealNameVerificationService;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReckonkvmRealNameVerificationServiceImpl implements ReckonkvmRealNameVerificationService {
   private static final Logger log = LoggerFactory.getLogger(ReckonkvmRealNameVerificationServiceImpl.class);
   private static final String RECKONKVM_REAL_KEY = "RECKONKVM_REAL_KEY";
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";
   private static final String RETURN_PATH = "/console";
   private static final String GATEWAY_BASE_URL = "https://api.reckonkvm.com/open/real-auth";
   private static final String PATH_INITIALIZE = "/initialize";
   private static final String PATH_CERTIFY = "/certify";
   private static final String PATH_QUERY = "/query";
   private static final String HEADER_API_KEY = "X-Api-Key";
   private static final int HTTP_TIMEOUT = 15000;
   private static final int SUCCESS_CODE = 200;
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private MemberPersonalRealInfoService memberPersonalRealInfoService;
   @Resource
   private MemberCompanyRealInfoService memberCompanyRealInfoService;
   @Resource
   private ClientUserService clientUserService;

   @Override
   public String initializeFaceVerification(String certName, String certNo) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("certName", certName);
      body.put("certNo", certNo);
      body.put("returnUrl", this.buildReturnUrl());
      JSONObject data = this.requestReckonkvm("/initialize", body);
      String certifyId = data.getString("certifyId");
      if (StrUtil.isBlank(certifyId)) {
         throw new CommonException("Reckonkvm实名认证初始化失败：认证ID为空", new Object[0]);
      } else {
         log.info("Reckonkvm实名认证初始化成功，certName={}，certifyId={}", certName, certifyId);
         return certifyId;
      }
   }

   @Override
   public CommonResult certifyFace(String certifyId) {
      try {
         JSONObject data = this.requestReckonkvm("/certify", this.certifyIdBody(certifyId));
         String certifyUrl = data.getString("certifyUrl");
         return StrUtil.isBlank(certifyUrl) ? CommonResult.error("Reckonkvm实名认证跳转地址为空") : CommonResult.data(certifyUrl);
      } catch (CommonException var4) {
         log.error("Reckonkvm实名认证生成跳转地址失败，certifyId={}", certifyId, var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult certifyQuery(String certifyId) {
      try {
         JSONObject data = this.requestReckonkvm("/query", this.certifyIdBody(certifyId));
         if (!Boolean.TRUE.equals(data.getBoolean("passed"))) {
            return CommonResult.error("认证不通过");
         } else {
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            if (UserTypeEnum.PERSONAL.getKey().equals(clientLoginUser.getUserType())) {
               this.memberPersonalRealInfoService.updateByStatus(clientLoginUser.getId(), CertificationStatusEnum.APPROVED.getKey());
            } else {
               if (!UserTypeEnum.ENTERPRISE.getKey().equals(clientLoginUser.getUserType())) {
                  throw new CommonException("不支持的用户类型：{}", new Object[]{clientLoginUser.getUserType()});
               }

               this.memberCompanyRealInfoService.updateByStatus(clientLoginUser.getId(), CertificationStatusEnum.APPROVED.getKey());
            }

            this.clientUserService.updateByRealStatus(clientLoginUser.getId());
            return CommonResult.ok("认证成功");
         }
      } catch (CommonException var4) {
         log.error("Reckonkvm实名认证查询失败，certifyId={}", certifyId, var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   private JSONObject requestReckonkvm(String path, Map<String, Object> body) {
      String url = "https://api.reckonkvm.com/open/real-auth" + path;
      String apiKey = this.requiredConfig("RECKONKVM_REAL_KEY", "Reckonkvm实名认证Key未配置");

      try {
         HttpResponse httpResponse = ((HttpRequest)((HttpRequest)HttpRequest.post(url).header("X-Api-Key", apiKey)).header("Content-Type", "application/json"))
            .body(JSON.toJSONString(body))
            .timeout(15000)
            .execute();

         JSONObject var9;
         try {
            String responseBody = httpResponse.body();
            log.info("Reckonkvm实名认证响应：path={}，body={}", path, responseBody);
            JSONObject response = JSON.parseObject(responseBody);
            if (response == null || response.getIntValue("code") != 200) {
               String message = response == null ? "响应为空" : response.getString("msg");
               throw new CommonException("Reckonkvm实名认证请求失败：{}", new Object[]{StrUtil.blankToDefault(message, "未知错误")});
            }

            JSONObject data = response.getJSONObject("data");
            if (ObjectUtil.isEmpty(data)) {
               throw new CommonException("Reckonkvm实名认证请求失败：data为空", new Object[0]);
            }

            var9 = data;
         } catch (Throwable var11) {
            if (httpResponse != null) {
               try {
                  httpResponse.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (httpResponse != null) {
            httpResponse.close();
         }

         return var9;
      } catch (CommonException var12) {
         throw var12;
      } catch (Exception var13) {
         log.error("Reckonkvm实名认证请求异常，path={}", path, var13);
         throw new CommonException("Reckonkvm实名认证请求异常", new Object[0]);
      }
   }

   private Map<String, Object> certifyIdBody(String certifyId) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("certifyId", certifyId);
      return body;
   }

   private String buildReturnUrl() {
      String homeUrl = this.requiredConfig("RUNXIN_SYS_HOME_URL", "网站基础信息配置错误：网站网址为空");
      return StrUtil.removeSuffix(homeUrl, "/") + "/console";
   }

   private String requiredConfig(String key, String message) {
      String value = this.devConfigApi.getValueByKey(key);
      if (StrUtil.isBlank(value)) {
         throw new CommonException(message, new Object[0]);
      } else {
         return value.trim();
      }
   }
}
