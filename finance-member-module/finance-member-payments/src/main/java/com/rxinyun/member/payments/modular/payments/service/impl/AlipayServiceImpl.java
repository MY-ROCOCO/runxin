package com.rxinyun.member.payments.modular.payments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.rxinyun.common.enums.PlatformEnum;
import com.rxinyun.common.enums.business.AlipayPayModeEnum;
import com.rxinyun.common.event.OrderEvent;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.user.entity.MemberBalance;
import com.rxinyun.member.auth.modular.user.entity.Recharge;
import com.rxinyun.member.auth.modular.user.param.RechargeEditParam;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import com.rxinyun.member.auth.modular.user.service.RechargeService;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.mapper.PaymentsRecordMapper;
import com.rxinyun.member.payments.modular.payments.param.AlipayFaceQrPayResponse;
import com.rxinyun.member.payments.modular.payments.service.AlipayService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlipayServiceImpl implements AlipayService {
   private static final Logger log = LoggerFactory.getLogger(AlipayServiceImpl.class);
   private static final String ALIPAY_PAY_APP_ID = "ALIPAY_PAY_APP_ID";
   private static final String ALIPAY_PAY_APP_PRIVATE_KEY = "ALIPAY_PAY_APP_PRIVATE_KEY";
   private static final String ALIPAY_PAY_PUBLIC_KEY = "ALIPAY_PAY_PUBLIC_KEY";
   private static final String ALIPAY_PC_PAY_MODE = "ALIPAY_PC_PAY_MODE";
   private static final String ALIPAY_H5_PAY_MODE = "ALIPAY_H5_PAY_MODE";
   private static final String ALIPAY_PAY_NOTIFY_URL = "/member-api/member/pay/alipay/notify";
   private static final String ALIPAY_PAY_RETURN_URL = "/console";
   private static final String ALIPAY_PAY_GATEWAY = "https://openapi.alipay.com/gateway.do";
   private static final String PAYMENT_ORDER_EXPIRATION_TIME = "PAYMENT_ORDER_EXPIRATION_TIME";
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";
   private static final String FACE_TO_FACE_PAYMENT = "FACE_QR";
   private static final String FACE_TO_FACE_PRODUCT_CODE = "FACE_TO_FACE_PAYMENT";
   private static final String DEFAULT_PAYMENT_ORDER_EXPIRATION_MINUTES = "30";
   private static final int ALIPAY_PAY_TYPE = 2;
   private static final String FORMAT = "json";
   private static final String CHART_TYPE = "utf-8";
   private static final String SIGN_TYPE = "RSA2";
   @Autowired
   private ApplicationContext applicationContext;
   @Autowired
   private PaymentsRecordMapper paymentsRecordMapper;
   @Autowired
   private RechargeService rechargeService;
   @Resource
   private MemberBalanceService memberBalanceService;
   @Autowired
   private DevConfigApi devConfigApi;

   private AlipayClient getAlipayClient() {
      String aliPayAppId = this.devConfigApi.getValueByKey("ALIPAY_PAY_APP_ID");
      if (ObjectUtil.isEmpty(aliPayAppId)) {
         throw new CommonException("支付宝支付配置错误：支付宝AppID为空", new Object[0]);
      } else {
         String aliPayAppPrivateKey = this.devConfigApi.getValueByKey("ALIPAY_PAY_APP_PRIVATE_KEY");
         if (ObjectUtil.isEmpty(aliPayAppPrivateKey)) {
            throw new CommonException("支付宝支付配置错误：支付宝AppPrivateKey为空", new Object[0]);
         } else {
            String aliPayPublicKey = this.devConfigApi.getValueByKey("ALIPAY_PAY_PUBLIC_KEY");
            if (ObjectUtil.isEmpty(aliPayPublicKey)) {
               throw new CommonException("支付宝支付配置错误：支付宝aliPayPublicKey为空", new Object[0]);
            } else {
               return new DefaultAlipayClient(
                  "https://openapi.alipay.com/gateway.do", aliPayAppId, aliPayAppPrivateKey, "json", "utf-8", aliPayPublicKey, "RSA2"
               );
            }
         }
      }
   }

   @Override
   public CommonResult pay(String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      PlatformEnum platformEnum = PlatformEnum.fromCode(platform);
      if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
         switch (platformEnum) {
            case OA:
               log.info("公众号支付");
               return CommonResult.error("支付宝暂不支持公众号支付");
            case PC:
            case H5:
               return this.routePay(platformEnum, subject, memberNo, orderNo, totalAmount);
            case MP:
               log.info("小程序支付");
               return CommonResult.error("支付宝暂不支持小程序支付");
            case APP:
               log.info("APP支付");
               return CommonResult.error("支付宝暂不支持APP支付");
            default:
               log.error("支付类型错误");
               return CommonResult.error("支付类型错误");
         }
      } else {
         return CommonResult.error("支付金额不合法");
      }
   }

   private CommonResult routePay(PlatformEnum platformEnum, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      AlipayPayModeEnum payMode = this.resolvePayMode(platformEnum);
      log.info("支付宝支付入口:{}，实际支付方式:{}", platformEnum.getCode(), payMode.getCode());
      switch (payMode) {
         case PC:
            String aliPayPcUrl = this.alipayPcPay(subject, memberNo, orderNo, totalAmount);
            return StrUtil.isBlank(aliPayPcUrl) ? CommonResult.error("支付宝电脑网站支付下单失败") : CommonResult.data(aliPayPcUrl);
         case H5:
            String aliPayH5Url = this.alipayH5Pay(subject, memberNo, orderNo, totalAmount);
            return StrUtil.isBlank(aliPayH5Url) ? CommonResult.error("支付宝手机网站支付下单失败") : CommonResult.data(aliPayH5Url);
         case FACE_QR:
            AlipayFaceQrPayResponse faceQrPayResponse = this.alipayFaceQrPay(subject, memberNo, orderNo, totalAmount);
            return faceQrPayResponse == null ? CommonResult.error("支付宝当面付预下单失败") : CommonResult.data(faceQrPayResponse);
         default:
            return CommonResult.error("支付宝支付方式错误");
      }
   }

   private AlipayPayModeEnum resolvePayMode(PlatformEnum platformEnum) {
      String configKey;
      String defaultMode;
      switch (platformEnum) {
         case PC:
            configKey = "ALIPAY_PC_PAY_MODE";
            defaultMode = AlipayPayModeEnum.PC.getCode();
            break;
         case H5:
            configKey = "ALIPAY_H5_PAY_MODE";
            defaultMode = AlipayPayModeEnum.H5.getCode();
            break;
         default:
            throw new CommonException("支付宝支付平台不支持：" + platformEnum.getCode(), new Object[0]);
      }

      String configValue = StrUtil.blankToDefault(this.devConfigApi.getValueByKey(configKey), defaultMode).trim().toUpperCase();

      AlipayPayModeEnum payMode;
      try {
         payMode = AlipayPayModeEnum.fromCode(configValue);
      } catch (IllegalArgumentException var7) {
         throw new CommonException("支付宝支付配置错误：" + configKey + "=" + configValue, new Object[0]);
      }

      if (PlatformEnum.PC.equals(platformEnum) && payMode != AlipayPayModeEnum.PC && payMode != AlipayPayModeEnum.FACE_QR) {
         throw new CommonException("支付宝支付配置错误：" + configKey + " 仅支持 PC 或 FACE_QR", new Object[0]);
      } else if (PlatformEnum.H5.equals(platformEnum) && payMode != AlipayPayModeEnum.H5 && payMode != AlipayPayModeEnum.FACE_QR) {
         throw new CommonException("支付宝支付配置错误：" + configKey + " 仅支持 H5 或 FACE_QR", new Object[0]);
      } else {
         return payMode;
      }
   }

   private String alipayPcPay(String subject, Long memberNo, String outTradeNo, BigDecimal totalAmount) {
      AlipayClient alipayClient = this.getAlipayClient();
      String homeUrl = this.getHomeUrl();
      AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
      request.setNotifyUrl(homeUrl + "/member-api/member/pay/alipay/notify");
      request.setReturnUrl(homeUrl + "/console");
      AlipayTradePagePayModel model = new AlipayTradePagePayModel();
      model.setSubject(subject);
      model.setBody(this.buildPaymentBody(subject, memberNo));
      model.setOutTradeNo(outTradeNo);
      model.setTotalAmount(String.valueOf(totalAmount));
      model.setProductCode("FAST_INSTANT_TRADE_PAY");
      request.setBizModel(model);
      log.info("=========支付宝支付参数========: {}", JSON.toJSONString(request));

      try {
         AlipayTradePagePayResponse response = (AlipayTradePagePayResponse)alipayClient.pageExecute(request, "POST");
         if (response.isSuccess()) {
            log.info("调用成功，返回的重定向URL为：{}", response.getBody());
            return response.getBody();
         }

         log.error("调用失败，错误信息：{}", response.getSubMsg());
      } catch (AlipayApiException var10) {
         log.error("支付宝API调用异常：", var10);
      }

      return null;
   }

   private String alipayH5Pay(String subject, Long memberNo, String outTradeNo, BigDecimal totalAmount) {
      AlipayClient alipayClient = this.getAlipayClient();
      String homeUrl = this.getHomeUrl();
      AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
      request.setNotifyUrl(homeUrl + "/member-api/member/pay/alipay/notify");
      request.setReturnUrl(homeUrl + "/console");
      AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
      model.setOutTradeNo(outTradeNo);
      model.setTotalAmount(String.valueOf(totalAmount));
      model.setBody(this.buildPaymentBody(subject, memberNo));
      model.setSubject(subject);
      model.setProductCode("QUICK_WAP_WAY");
      request.setBizModel(model);
      log.info("=========支付宝支付参数========: {}", JSON.toJSONString(request));

      try {
         AlipayTradeWapPayResponse response = (AlipayTradeWapPayResponse)alipayClient.pageExecute(request, "POST");
         if (response.isSuccess()) {
            log.info("调用成功，返回的重定向URL为：{}", response.getBody());
            return response.getBody();
         }

         log.error("调用失败，错误信息：{}", response.getSubMsg());
      } catch (AlipayApiException var10) {
         log.error("支付宝API调用异常：", var10);
      }

      return null;
   }

   private AlipayFaceQrPayResponse alipayFaceQrPay(String subject, Long memberNo, String outTradeNo, BigDecimal totalAmount) {
      AlipayClient alipayClient = this.getAlipayClient();
      String homeUrl = this.getHomeUrl();
      String timeoutExpress = this.getTimeoutExpress();
      AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
      request.setNotifyUrl(homeUrl + "/member-api/member/pay/alipay/notify");
      AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
      model.setOutTradeNo(outTradeNo);
      model.setTotalAmount(String.valueOf(totalAmount));
      model.setBody(this.buildPaymentBody(subject, memberNo));
      model.setSubject(subject);
      model.setProductCode("FACE_TO_FACE_PAYMENT");
      if (StrUtil.isNotBlank(timeoutExpress)) {
         model.setTimeoutExpress(timeoutExpress);
      }

      request.setBizModel(model);
      log.info("=========支付宝当面付预下单参数========: {}", JSON.toJSONString(request));

      try {
         AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse)alipayClient.execute(request);
         if (response.isSuccess() && StrUtil.isNotBlank(response.getQrCode())) {
            AlipayFaceQrPayResponse payResponse = new AlipayFaceQrPayResponse();
            payResponse.setOrderNo(outTradeNo);
            payResponse.setOutTradeNo(outTradeNo);
            payResponse.setPayType(2);
            payResponse.setPayMode("FACE_QR");
            payResponse.setQrCode(response.getQrCode());
            payResponse.setExpireTime(timeoutExpress);
            return payResponse;
         }

         log.error("支付宝当面付预下单失败，错误信息：{}", response.getSubMsg());
      } catch (AlipayApiException var12) {
         log.error("支付宝当面付预下单异常：", var12);
      }

      return null;
   }

   private String buildPaymentBody(String subject, Long memberNo) {
      return subject + "，会员编号：" + memberNo;
   }

   private String getHomeUrl() {
      String homeUrl = this.devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL");
      if (ObjectUtil.isEmpty(homeUrl)) {
         throw new CommonException("网站基础信息配置错误：网站网址为空", new Object[0]);
      } else {
         return homeUrl;
      }
   }

   private String getTimeoutExpress() {
      String paymentOrderExpirationTime = StrUtil.blankToDefault(this.devConfigApi.getValueByKey("PAYMENT_ORDER_EXPIRATION_TIME"), "30").trim();

      try {
         int expirationMinutes = Integer.parseInt(paymentOrderExpirationTime);
         return expirationMinutes <= 0 ? null : expirationMinutes + "m";
      } catch (NumberFormatException var3) {
         log.warn("支付订单过期时间配置非法，使用默认值: {}", paymentOrderExpirationTime);
         return "30m";
      }
   }

   @Override
   public String notify(HttpServletRequest request) {
      Map<String, String> params = this.extractParameters(request);
      log.info("=========支付宝回调参数========:{}", JSON.toJSONString(params));
      if ("TRADE_SUCCESS".equals(params.get("trade_status"))) {
         log.info("=========支付宝异步回调========");
         this.processSuccessfulPayment(params);
      }

      return "success";
   }

   private Map<String, String> extractParameters(HttpServletRequest request) {
      Map<String, String> params = new HashMap<>();
      Map<String, String[]> requestParams = request.getParameterMap();
      requestParams.forEach((key, value) -> params.put(key, value[0]));
      return params;
   }

   private void processSuccessfulPayment(Map<String, String> params) {
      try {
         String orderNo = params.get("out_trade_no");
         String payTime = params.get("gmt_payment");
         String alipayPayNo = params.get("trade_no");
         String sign = params.get("sign");
         String content = AlipaySignature.getSignCheckContentV1(params);
         String aliPayPublicKey = this.devConfigApi.getValueByKey("ALIPAY_PAY_PUBLIC_KEY");
         if (ObjectUtil.isEmpty(aliPayPublicKey)) {
            throw new CommonException("支付宝支付配置错误：支付宝aliPayPublicKey为空", new Object[0]);
         }

         boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayPublicKey, "UTF-8");
         log.info("签名校验结果: {}", checkSignature);
         if (checkSignature) {
            log.info("交易名称: {}", params.get("subject"));
            log.info("交易备注: {}", params.get("body"));
            log.info("交易状态: {}", params.get("trade_status"));
            log.info("支付宝交易凭证号: {}", params.get("trade_no"));
            log.info("商户订单号: {}", params.get("out_trade_no"));
            log.info("交易金额: {}", params.get("total_amount"));
            log.info("买家在支付宝唯一id: {}", params.get("buyer_id"));
            log.info("买家付款时间: {}", params.get("gmt_payment"));
            log.info("买家付款金额: {}", params.get("buyer_pay_amount"));
            log.info("交易类型: {}", params.get("businessParams"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String memberNo = this.extractMemberNo(params.get("body"));
            log.info("会员编号: {}", memberNo);
            if ("用户充值".equals(params.get("subject"))) {
               this.rechargeSuccess(orderNo, sdf.parse(payTime), alipayPayNo);
               this.savePaymentRecord(Long.valueOf(memberNo), params.get("subject"), orderNo, alipayPayNo, new BigDecimal(params.get("total_amount")), 2);
            } else {
               this.savePaymentRecord(Long.valueOf(memberNo), params.get("subject"), orderNo, alipayPayNo, new BigDecimal(params.get("total_amount")), 2);
               this.publishOrderEvent(orderNo, sdf.parse(payTime), alipayPayNo, 2);
            }
         }
      } catch (Exception var11) {
         log.error("支付宝回调参数解析异常：", var11);
      }
   }

   private String extractMemberNo(String input) {
      String[] parts = input.split("，");

      for (String part : parts) {
         if (part.trim().startsWith("会员编号：")) {
            return part.trim().substring("会员编号：".length()).trim();
         }
      }

      return null;
   }

   @Transactional
   @Override
   public void rechargeSuccess(String outTradeNo, Date payTime, String alipayPayNo) {
      Recharge recharge = this.rechargeService.queryEntity(outTradeNo);
      if (recharge == null) {
         log.error("充值单不存在，订单号:{}", outTradeNo);
      } else if (!Integer.valueOf(1).equals(recharge.getStatus())) {
         log.warn("充值单非未完成状态，跳过到账 rechargeId={} status={}", outTradeNo, recharge.getStatus());
      } else {
         RechargeEditParam rechargeEditParam = (RechargeEditParam)BeanUtil.copyProperties(recharge, RechargeEditParam.class, new String[0]);
         rechargeEditParam.setStatus(2);
         rechargeEditParam.setRechargeTime(payTime);
         rechargeEditParam.setTransactionId(alipayPayNo);
         this.rechargeService.edit(rechargeEditParam);
         MemberBalance memberBalance = this.memberBalanceService.selectByMemberNo(recharge.getMemberNo());
         if (memberBalance != null) {
            memberBalance.setBalance(memberBalance.getBalance().add(recharge.getAmount()));
            this.memberBalanceService.edit(memberBalance);
         }
      }
   }

   private void savePaymentRecord(Long memberNo, String subject, String orderNo, String outTradeNo, BigDecimal totalAmount, Integer payType) {
      PaymentsRecord record = new PaymentsRecord();
      record.setMemberNo(memberNo);
      record.setOrderNo(orderNo);
      record.setTradeNo(outTradeNo);
      record.setBusinessType(subject);
      record.setRemark(subject);
      record.setPayTime(new Date());
      record.setPayAmount(totalAmount);
      record.setPayType(payType);
      MemberBalance mb = this.memberBalanceService.selectByMemberNo(memberNo);
      record.setMemberBalanceAfter(mb != null ? mb.getBalance() : null);
      this.paymentsRecordMapper.insert(record);
   }

   private void publishOrderEvent(String outTradeNo, Date payTime, String alipayPayNo, Integer payType) {
      try {
         log.info("订单支付事件发布中，订单号:{}", outTradeNo);
         OrderEvent event = new OrderEvent(this, outTradeNo, payTime, alipayPayNo, payType);
         this.applicationContext.publishEvent(event);
         log.info("订单支付事件发布成功，订单号:{}", outTradeNo);
      } catch (Exception var6) {
         log.error("订单支付事件发布异常，订单号:{}", outTradeNo, var6);
      }
   }
}
