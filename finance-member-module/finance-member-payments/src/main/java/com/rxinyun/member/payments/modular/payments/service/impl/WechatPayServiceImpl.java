package com.rxinyun.member.payments.modular.payments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.common.enums.PlatformEnum;
import com.rxinyun.common.event.OrderEvent;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.user.entity.MemberBalance;
import com.rxinyun.member.auth.modular.user.entity.Recharge;
import com.rxinyun.member.auth.modular.user.param.RechargeEditParam;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import com.rxinyun.member.auth.modular.user.service.RechargeService;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.mapper.PaymentsRecordMapper;
import com.rxinyun.member.payments.modular.payments.service.WechatPayService;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig.Builder;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.h5.model.H5Info;
import com.wechat.pay.java.service.payments.h5.model.SceneInfo;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
public class WechatPayServiceImpl implements WechatPayService {
   private static final Logger log = LoggerFactory.getLogger(WechatPayServiceImpl.class);
   private static final String WECHAT_PAY_APP_ID = "WECHAT_PAY_APP_ID";
   private static final String WECHAT_PAY_APP_PRIVATE_KEY = "WECHAT_PAY_APP_PRIVATE_KEY";
   private static final String WECHAT_PAY_SERIAL_NUMBER = "WECHAT_PAY_SERIAL_NUMBER";
   private static final String WECHAT_PAY_API_V3 = "WECHAT_PAY_API_V3";
   private static final String WECHAT_PAY_BIND_APPID = "WECHAT_PAY_BIND_APPID";
   private static final String WECHAT_PAY_NOTIFY_URL = "/member-api/member/pay/wechat/notify";
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";
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

   private Config getConfig() {
      String wechatPayAppId = this.devConfigApi.getValueByKey("WECHAT_PAY_APP_ID");
      if (ObjectUtil.isEmpty(wechatPayAppId)) {
         throw new CommonException("微信支付配置错误：微信支付商户号不能为空", new Object[0]);
      } else {
         String wechatPayPrivateKey = this.devConfigApi.getValueByKey("WECHAT_PAY_APP_PRIVATE_KEY");
         if (ObjectUtil.isEmpty(wechatPayPrivateKey)) {
            throw new CommonException("微信支付配置错误：微信支付私钥不能为空", new Object[0]);
         } else {
            String wechatPaySerialNumber = this.devConfigApi.getValueByKey("WECHAT_PAY_SERIAL_NUMBER");
            if (ObjectUtil.isEmpty(wechatPaySerialNumber)) {
               throw new CommonException("微信支付配置错误：微信支付证书序列号不能为空", new Object[0]);
            } else {
               String wechatPayApiV3 = this.devConfigApi.getValueByKey("WECHAT_PAY_API_V3");
               if (ObjectUtil.isEmpty(wechatPayApiV3)) {
                  throw new CommonException("微信支付配置错误：微信支付apiV3密钥不能为空", new Object[0]);
               } else {
                  log.info(
                     "微信支付配置 - 商户号: {}, 商户证书序列号: {}, 私钥长度: {}, ApiV3密钥长度: {}",
                     new Object[]{wechatPayAppId, wechatPaySerialNumber, wechatPayPrivateKey.length(), wechatPayApiV3.length()}
                  );
                  log.info("私钥前50字符: {}", wechatPayPrivateKey.substring(0, Math.min(50, wechatPayPrivateKey.length())));
                  log.info("私钥后50字符: {}", wechatPayPrivateKey.substring(Math.max(0, wechatPayPrivateKey.length() - 50)));
                  log.info("ApiV3密钥: {}", wechatPayApiV3);

                  try {
                     Config config = ((Builder)((Builder)((Builder)new Builder().merchantId(wechatPayAppId)).privateKey(wechatPayPrivateKey))
                           .merchantSerialNumber(wechatPaySerialNumber))
                        .apiV3Key(wechatPayApiV3)
                        .build();
                     log.info("微信支付配置创建成功");
                     return config;
                  } catch (Exception var6) {
                     log.error("创建微信支付配置失败！", var6);
                     throw new CommonException("微信支付配置创建失败: " + var6.getMessage(), new Object[0]);
                  }
               }
            }
         }
      }
   }

   @Override
   public CommonResult pay(String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      log.info("微信支付:支付平台：{},产品名称：{}, 会员编号：{},订单号：{},金额：{}", new Object[]{platform, subject, memberNo, orderNo, totalAmount});
      PlatformEnum platformEnum = PlatformEnum.fromCode(platform);
      if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
         switch (platformEnum) {
            case OA:
               log.info("公众号支付");
               return null;
            case PC:
               log.info("PC支付");
               String wechatPayUrl = this.wechatNativePay(subject, memberNo, orderNo, totalAmount);
               return CommonResult.data(wechatPayUrl);
            case H5:
               log.info("移动端支付");
               String wechatH5PayUrl = this.wechatH5Pay(subject, memberNo, orderNo, totalAmount);
               return CommonResult.data(wechatH5PayUrl);
            case MP:
               log.info("小程序支付");
               return null;
            case APP:
               log.info("APP支付");
               return null;
            default:
               log.error("支付类型错误");
               return CommonResult.error("支付类型错误");
         }
      } else {
         return CommonResult.error("支付金额不合法");
      }
   }

   public String wechatNativePay(String subject, Long memberNo, String outTradeNo, BigDecimal totalAmount) {
      Config config = this.getConfig();
      NativePayService service = new com.wechat.pay.java.service.payments.nativepay.NativePayService.Builder().config(config).build();
      String wechatPayAppId = this.devConfigApi.getValueByKey("WECHAT_PAY_APP_ID");
      String wechatPayBindAppId = this.devConfigApi.getValueByKey("WECHAT_PAY_BIND_APPID");
      if (!ObjectUtil.isEmpty(wechatPayAppId) && !ObjectUtil.isEmpty(wechatPayBindAppId)) {
         String homeUrl = this.devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL");
         if (ObjectUtil.isEmpty(homeUrl)) {
            throw new CommonException("网站基础信息配置错误：网站网址为空", new Object[0]);
         } else {
            PrepayRequest request = new PrepayRequest();
            Amount amount = new Amount();
            amount.setTotal(totalAmount.multiply(new BigDecimal(100)).intValue());
            request.setAmount(amount);
            request.setAppid(wechatPayBindAppId);
            request.setMchid(wechatPayAppId);
            request.setDescription(subject);
            request.setNotifyUrl(homeUrl + "/member-api/member/pay/wechat/notify");
            request.setOutTradeNo(outTradeNo);
            Map<String, String> map = new HashMap<>();
            map.put("subject", subject);
            map.put("member_no", String.valueOf(memberNo));
            request.setAttach(JSON.toJSONString(map));
            PrepayResponse response = service.prepay(request);
            return response.getCodeUrl();
         }
      } else {
         throw new CommonException("微信支付配置错误：微信支付商户号或绑定公众号/小程序不能为空", new Object[0]);
      }
   }

   public String wechatH5Pay(String subject, Long memberNo, String outTradeNo, BigDecimal totalAmount) {
      Config config = this.getConfig();
      H5Service service = new com.wechat.pay.java.service.payments.h5.H5Service.Builder().config(config).build();
      String wechatPayAppId = this.devConfigApi.getValueByKey("WECHAT_PAY_APP_ID");
      String wechatPayBindAppId = this.devConfigApi.getValueByKey("WECHAT_PAY_BIND_APPID");
      if (!ObjectUtil.isEmpty(wechatPayAppId) && !ObjectUtil.isEmpty(wechatPayBindAppId)) {
         com.wechat.pay.java.service.payments.h5.model.PrepayRequest request = new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();
         com.wechat.pay.java.service.payments.h5.model.Amount amount = new com.wechat.pay.java.service.payments.h5.model.Amount();
         amount.setTotal(totalAmount.multiply(new BigDecimal(100)).intValue());
         request.setAmount(amount);
         request.setAppid(wechatPayBindAppId);
         request.setMchid(wechatPayAppId);
         request.setDescription(subject);
         String notifyHomeUrl = this.devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL");
         if (ObjectUtil.isEmpty(notifyHomeUrl)) {
            throw new CommonException("网站基础信息配置错误：网站网址为空", new Object[0]);
         }
         request.setNotifyUrl(notifyHomeUrl + "/member-api/member/pay/wechat/notify");
         request.setOutTradeNo(outTradeNo);
         SceneInfo sceneInfo = new SceneInfo();
         H5Info h5Info = new H5Info();
         h5Info.setType("Wap");
         h5Info.setAppName("润信云");
         sceneInfo.setH5Info(h5Info);
         HttpServletRequest httpRequest = CommonServletUtil.getRequest();
         sceneInfo.setPayerClientIp(CommonIpAddressUtil.getIp(httpRequest));
         request.setSceneInfo(sceneInfo);
         Map<String, String> map = new HashMap<>();
         map.put("subject", subject);
         map.put("member_no", String.valueOf(memberNo));
         request.setAttach(JSON.toJSONString(map));
         log.info("wechatH5Pay request:{}", request);
         com.wechat.pay.java.service.payments.h5.model.PrepayResponse response = service.prepay(request);
         return response.getH5Url();
      } else {
         throw new CommonException("微信支付配置错误：微信支付商户号或绑定公众号/小程序不能为空", new Object[0]);
      }
   }

   @Override
   public CommonResult notify(HttpServletRequest request) throws IOException {
      log.info("------收到微信支付通知------");
      new HashMap();
      String signature = request.getHeader("Wechatpay-Signature");
      String nonce = request.getHeader("Wechatpay-Nonce");
      String timestamp = request.getHeader("Wechatpay-Timestamp");
      String serial = request.getHeader("Wechatpay-Serial");
      String signType = request.getHeader("Wechatpay-Signature-Type");
      RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
         .serialNumber(serial)
         .nonce(nonce)
         .signature(signature)
         .timestamp(timestamp)
         .signType(signType)
         .body(getRequestBody(request))
         .build();
      Config config = this.getConfig();
      NotificationParser parser = new NotificationParser(new NotificationConfig[]{(NotificationConfig)config});
      log.info("验签参数 - serial_number(微信平台证书): {}, timestamp: {}, nonce: {}, signType: {}", new Object[]{serial, timestamp, nonce, signType});
      log.info("验签：{}", requestParam);

      Transaction transaction;
      try {
         transaction = (Transaction)parser.parse(requestParam, Transaction.class);
         log.info("验签成功！支付回调结果：{}", transaction.toString());
      } catch (ValidationException var18) {
         log.error("验签失败！异常信息: {}", var18.getMessage(), var18);
         return CommonResult.error("验签失败");
      }

      log.info("验签成功！支付回调结果：{}", transaction.toString());
      log.info("会员编号: {}", transaction.getAttach());
      JSONObject jsonObject = JSONObject.parseObject(transaction.getAttach());
      String subject = jsonObject.getString("subject");
      String memberNo = jsonObject.getString("member_no");
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
      OffsetDateTime dateTime = OffsetDateTime.parse(transaction.getSuccessTime(), formatter);
      Date date = Date.from(dateTime.toInstant());
      if ("用户充值".equals(subject)) {
         this.rechargeSuccess(transaction.getOutTradeNo(), date, transaction.getTransactionId());
         this.savePaymentRecord(
            Long.valueOf(memberNo),
            subject,
            transaction.getOutTradeNo(),
            transaction.getTransactionId(),
            new BigDecimal(transaction.getAmount().getPayerTotal() / 100),
            1
         );
      } else {
         this.savePaymentRecord(
            Long.valueOf(memberNo),
            subject,
            transaction.getOutTradeNo(),
            transaction.getTransactionId(),
            new BigDecimal(transaction.getAmount().getPayerTotal() / 100),
            1
         );
         this.publishOrderEvent(transaction.getOutTradeNo(), date, transaction.getTransactionId(), 1);
      }

      return CommonResult.ok();
   }

   public static String getRequestBody(HttpServletRequest request) throws IOException {
      try {
         byte[] bytes = request.getInputStream().readAllBytes();
         return new String(bytes, StandardCharsets.UTF_8);
      } catch (IOException var2) {
         throw new IOException("读取返回支付接口数据流出现异常！", var2);
      }
   }

   @Transactional
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
         log.error("订单支付事件发布异常，订单号:{}", outTradeNo);
      }
   }
}
