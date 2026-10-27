package com.rxinyun.member.auth.modular.real.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.DatadigitalFincloudGeneralsaasFaceCertifyInitializeModel;
import com.alipay.api.domain.DatadigitalFincloudGeneralsaasFaceCertifyQueryModel;
import com.alipay.api.domain.DatadigitalFincloudGeneralsaasFaceCertifyVerifyModel;
import com.alipay.api.domain.OpenCertifyIdentifyInfo;
import com.alipay.api.domain.OpenCertifyMerchantConfigs;
import com.alipay.api.request.DatadigitalFincloudGeneralsaasFaceCertifyInitializeRequest;
import com.alipay.api.request.DatadigitalFincloudGeneralsaasFaceCertifyQueryRequest;
import com.alipay.api.request.DatadigitalFincloudGeneralsaasFaceCertifyVerifyRequest;
import com.alipay.api.response.DatadigitalFincloudGeneralsaasFaceCertifyInitializeResponse;
import com.alipay.api.response.DatadigitalFincloudGeneralsaasFaceCertifyQueryResponse;
import com.alipay.api.response.DatadigitalFincloudGeneralsaasFaceCertifyVerifyResponse;
import com.rxinyun.common.enums.CertificationStatusEnum;
import com.rxinyun.common.enums.UserTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.real.service.AlipayFaceVerificationService;
import com.rxinyun.member.auth.modular.real.service.MemberCompanyRealInfoService;
import com.rxinyun.member.auth.modular.real.service.MemberPersonalRealInfoService;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlipayFaceVerificationServiceImpl implements AlipayFaceVerificationService {
   private static final Logger log = LoggerFactory.getLogger(AlipayFaceVerificationServiceImpl.class);
   @Resource
   private MemberPersonalRealInfoService memberPersonalRealInfoService;
   @Resource
   private ClientUserService clientUserService;
   @Resource
   private MemberCompanyRealInfoService memberCompanyRealInfoService;
   @Autowired
   private DevConfigApi devConfigApi;
   private static final String FORMAT = "json";
   private static final String CHART_TYPE = "utf-8";
   private static final String SIGN_TYPE = "RSA2";
   private static final String ALIPAY_REAL_APP_ID = "ALIPAY_REAL_APP_ID";
   private static final String ALIPAY_REAL_APP_PRIVATE_KEY = "ALIPAY_REAL_APP_PRIVATE_KEY";
   private static final String ALIPAY_REAL_PUBLIC_KEY = "ALIPAY_REAL_PUBLIC_KEY";
   private static final String ALIPAY_REAL_GATEWAY = "https://openapi.alipay.com/gateway.do";
   private static final String ALIPAY_REAL_RETURN_PATH = "/console";
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";

   public AlipayClient getAlipayClient() {
      String aliPayAppId = this.devConfigApi.getValueByKey("ALIPAY_REAL_APP_ID");
      if (ObjectUtil.isEmpty(aliPayAppId)) {
         throw new CommonException("支付宝实名配置错误：支付宝AppID为空", new Object[0]);
      } else {
         String aliPayAppPrivateKey = this.devConfigApi.getValueByKey("ALIPAY_REAL_APP_PRIVATE_KEY");
         if (ObjectUtil.isEmpty(aliPayAppPrivateKey)) {
            throw new CommonException("支付宝实名配置错误：支付宝AppPrivateKey为空", new Object[0]);
         } else {
            String aliPayPublicKey = this.devConfigApi.getValueByKey("ALIPAY_REAL_PUBLIC_KEY");
            if (ObjectUtil.isEmpty(aliPayPublicKey)) {
               throw new CommonException("支付宝实名配置错误：支付宝aliPayPublicKey为空", new Object[0]);
            } else {
               return new DefaultAlipayClient(
                  "https://openapi.alipay.com/gateway.do", aliPayAppId, aliPayAppPrivateKey, "json", "utf-8", aliPayPublicKey, "RSA2"
               );
            }
         }
      }
   }

   @Override
   public String initializeFaceVerification(String certName, String certNo) {
      try {
         AlipayClient alipayClient = this.getAlipayClient();
         DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
         String homeUrl = devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL");
         if (ObjectUtil.isEmpty(homeUrl)) {
            throw new CommonException("网站基础信息配置错误：网站网址为空", new Object[0]);
         } else {
            String aliPayReturnUrl = homeUrl + "/console";
            DatadigitalFincloudGeneralsaasFaceCertifyInitializeRequest request = new DatadigitalFincloudGeneralsaasFaceCertifyInitializeRequest();
            DatadigitalFincloudGeneralsaasFaceCertifyInitializeModel model = new DatadigitalFincloudGeneralsaasFaceCertifyInitializeModel();
            model.setOuterOrderNo("RXYSM" + System.currentTimeMillis());
            model.setBizCode("FUTURE_TECH_BIZ_FACE_SDK");
            OpenCertifyIdentifyInfo identityParam = new OpenCertifyIdentifyInfo();
            identityParam.setCertType("IDENTITY_CARD");
            identityParam.setCertName(certName);
            identityParam.setCertNo(certNo);
            identityParam.setIdentityType("CERT_INFO");
            model.setIdentityParam(identityParam);
            OpenCertifyMerchantConfigs merchantConfig = new OpenCertifyMerchantConfigs();
            merchantConfig.setFaceReserveStrategy("reserve");
            merchantConfig.setReturnUrl(aliPayReturnUrl);
            model.setMerchantConfig(merchantConfig);
            request.setBizModel(model);
            log.info("人脸认证初始化请求参数:{}", JSON.toJSONString(request));
            DatadigitalFincloudGeneralsaasFaceCertifyInitializeResponse response = (DatadigitalFincloudGeneralsaasFaceCertifyInitializeResponse)alipayClient.execute(
               request
            );
            log.info("response:{}, code: {}", response.getBody(), response.getCode());
            if ("10000".equals(response.getCode()) && "Success".equals(response.getMsg())) {
               return response.getCertifyId();
            } else {
               throw new CommonException(response.getSubMsg(), new Object[0]);
            }
         }
      } catch (AlipayApiException var13) {
         log.error("支付宝人脸核身初始化异常", var13);
         throw new CommonException("支付宝人脸核身初始化异常", new Object[0]);
      }
   }

   @Override
   public CommonResult certifyFace(String certifyId) {
      try {
         AlipayClient alipayClient = this.getAlipayClient();
         DatadigitalFincloudGeneralsaasFaceCertifyVerifyRequest request = new DatadigitalFincloudGeneralsaasFaceCertifyVerifyRequest();
         DatadigitalFincloudGeneralsaasFaceCertifyVerifyModel model = new DatadigitalFincloudGeneralsaasFaceCertifyVerifyModel();
         model.setCertifyId(certifyId);
         request.setBizModel(model);
         DatadigitalFincloudGeneralsaasFaceCertifyVerifyResponse response = (DatadigitalFincloudGeneralsaasFaceCertifyVerifyResponse)alipayClient.execute(
            request
         );
         log.info("response:{}", response.getBody());
         if ("10000".equals(response.getCode()) && "Success".equals(response.getMsg())) {
            String certifyUrl = response.getCertifyUrl();
            return CommonResult.data(certifyUrl);
         } else {
            return CommonResult.error(response.getSubMsg());
         }
      } catch (AlipayApiException var7) {
         log.error("支付宝人脸核身初始化异常", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult certifyQuery(String certifyId) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();

      try {
         AlipayClient alipayClient = this.getAlipayClient();
         DatadigitalFincloudGeneralsaasFaceCertifyQueryRequest request = new DatadigitalFincloudGeneralsaasFaceCertifyQueryRequest();
         DatadigitalFincloudGeneralsaasFaceCertifyQueryModel model = new DatadigitalFincloudGeneralsaasFaceCertifyQueryModel();
         model.setCertifyId(certifyId);
         request.setBizModel(model);
         DatadigitalFincloudGeneralsaasFaceCertifyQueryResponse response = (DatadigitalFincloudGeneralsaasFaceCertifyQueryResponse)alipayClient.execute(request);
         log.info("response:{}", response.getBody());
         if ("10000".equals(response.getCode()) && "Success".equals(response.getMsg())) {
            if (response.getPassed().equals("T")) {
               if (clientLoginUser.getUserType().equals(UserTypeEnum.PERSONAL.getKey())) {
                  this.memberPersonalRealInfoService.updateByStatus(clientLoginUser.getId(), CertificationStatusEnum.APPROVED.getKey());
               } else if (clientLoginUser.getUserType().equals(UserTypeEnum.ENTERPRISE.getKey())) {
                  this.memberCompanyRealInfoService.updateByStatus(clientLoginUser.getId(), CertificationStatusEnum.APPROVED.getKey());
               }

               this.clientUserService.updateByRealStatus(clientLoginUser.getId());
               return CommonResult.ok("认证成功");
            } else {
               return CommonResult.error("认证不通过");
            }
         } else {
            return CommonResult.error(response.getSubMsg());
         }
      } catch (AlipayApiException var7) {
         log.error("支付宝人脸核身初始化异常", var7);
         return CommonResult.error(var7.getMessage());
      }
   }
}
