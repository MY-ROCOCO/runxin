package com.rxinyun.member.payments.modular.payments.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.event.OrderEvent;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.mapper.PaymentsRecordMapper;
import com.rxinyun.member.payments.modular.payments.service.AlipayService;
import com.rxinyun.member.payments.modular.payments.service.YipayService;
import com.rxinyun.member.payments.modular.payments.util.YipaySignUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

@Service
public class YipayServiceImpl implements YipayService {
   private static final Logger log = LoggerFactory.getLogger(YipayServiceImpl.class);
   private static final String ALIPAY_PAY_ROUTE = "ALIPAY_PAY_ROUTE";
   private static final String WECHAT_PAY_ROUTE = "WECHAT_PAY_ROUTE";
   private static final String PAY_ROUTE_YIPAY = "YIPAY";
   private static final String YIPAY_GATEWAY_URL = "YIPAY_GATEWAY_URL";
   private static final String YIPAY_PID = "YIPAY_PID";
   private static final String YIPAY_KEY = "YIPAY_KEY";
   private static final String YIPAY_CHANNEL_ID = "YIPAY_CHANNEL_ID";
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";
   private static final String YIPAY_NOTIFY_URL = "/member-api/member/pay/yipay/notify";
   private static final String YIPAY_RETURN_URL = "/console";
   private static final String SIGN_TYPE = "MD5";
   private static final int SUBJECT_MAX_LENGTH = 100;
   @Autowired
   private DevConfigApi devConfigApi;
   @Autowired
   private PaymentsRecordMapper paymentsRecordMapper;
   @Autowired
   private AlipayService alipayService;
   @Resource
   private BalanceApi balanceApi;
   @Autowired
   private ApplicationContext applicationContext;

   @Override
   public boolean isRouteEnabled(Integer payType) {
      if (payType == null) {
         return false;
      } else {
         String routeConfigKey;
         if (payType == PayTypeEnum.ALIPAY.getCode()) {
            routeConfigKey = "ALIPAY_PAY_ROUTE";
         } else {
            if (payType != PayTypeEnum.WECHAT.getCode()) {
               return false;
            }

            routeConfigKey = "WECHAT_PAY_ROUTE";
         }

         return "YIPAY".equalsIgnoreCase(StrUtil.trim(this.devConfigApi.getValueByKey(routeConfigKey)));
      }
   }

   @Override
   public CommonResult pay(Integer payType, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      if (totalAmount != null && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
         String gatewayUrl = this.requiredConfig("YIPAY_GATEWAY_URL", "易支付网关地址未配置");
         String pid = this.requiredConfig("YIPAY_PID", "易支付商户ID未配置");
         String key = this.requiredConfig("YIPAY_KEY", "易支付商户密钥未配置");
         String homeUrl = this.requiredConfig("RUNXIN_SYS_HOME_URL", "网站网址未配置");
         Map<String, String> params = new LinkedHashMap<>();
         params.put("pid", pid);
         params.put("type", this.resolveYipayType(payType));
         params.put("out_trade_no", orderNo);
         params.put("notify_url", homeUrl + "/member-api/member/pay/yipay/notify");
         params.put("return_url", homeUrl + "/console");
         params.put("name", this.normalizeSubject(subject));
         params.put("money", totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString());
         String channelId = this.devConfigApi.getValueByKey("YIPAY_CHANNEL_ID");
         if (StrUtil.isNotBlank(channelId)) {
            params.put("cid", channelId.trim());
         }

         params.put("param", this.buildParam(subject, memberNo, payType));
         params.put("sign", YipaySignUtil.sign(params, key));
         params.put("sign_type", "MD5");
         return CommonResult.data(this.buildSubmitForm(gatewayUrl, params));
      } else {
         return CommonResult.error("支付金额不合法");
      }
   }

   @Transactional
   @Override
   public String notify(HttpServletRequest request) {
      Map<String, String> params = this.extractParameters(request);
      log.info("=========易支付回调参数========:{}", JSON.toJSONString(params));
      String key = this.devConfigApi.getValueByKey("YIPAY_KEY");
      if (StrUtil.isBlank(key)) {
         log.error("易支付回调验签失败：商户密钥未配置");
         return "fail";
      } else if (!YipaySignUtil.verify(params, key)) {
         log.error("易支付回调验签失败，订单号:{}", params.get("out_trade_no"));
         return "fail";
      } else if (!"TRADE_SUCCESS".equals(params.get("trade_status"))) {
         log.warn("易支付回调交易状态非成功，订单号:{}，状态:{}", params.get("out_trade_no"), params.get("trade_status"));
         return "fail";
      } else {
         try {
            this.processSuccessfulPayment(params);
            return "success";
         } catch (Exception var5) {
            log.error("易支付回调处理异常，订单号:{}", params.get("out_trade_no"), var5);
            throw new CommonException("易支付回调处理异常", new Object[0]);
         }
      }
   }

   private void processSuccessfulPayment(Map<String, String> params) {
      String orderNo = params.get("out_trade_no");
      String tradeNo = StrUtil.blankToDefault(params.get("trade_no"), orderNo);
      if (this.isPaymentRecordExists(tradeNo, orderNo)) {
         log.info("易支付订单已处理过，跳过重复回调，订单号:{}，流水号:{}", orderNo, tradeNo);
      } else {
         JSONObject businessParam = this.parseBusinessParam(params.get("param"));
         String subject = StrUtil.blankToDefault(businessParam.getString("subject"), params.get("name"));
         Long memberNo = businessParam.getLong("member_no");
         Integer payType = businessParam.getInteger("pay_type");
         if (memberNo != null && payType != null) {
            BigDecimal totalAmount = new BigDecimal(params.get("money"));
            Date payTime = new Date();
            if ("用户充值".equals(subject)) {
               this.alipayService.rechargeSuccess(orderNo, payTime, tradeNo);
            } else {
               this.publishOrderEvent(orderNo, payTime, tradeNo, payType);
            }

            this.savePaymentRecord(memberNo, subject, orderNo, tradeNo, totalAmount, payType);
         } else {
            throw new CommonException("易支付回调业务参数缺失", new Object[0]);
         }
      }
   }

   private String requiredConfig(String key, String message) {
      String value = this.devConfigApi.getValueByKey(key);
      if (ObjectUtil.isEmpty(value)) {
         throw new CommonException(message, new Object[0]);
      } else {
         return value.trim();
      }
   }

   private String resolveYipayType(Integer payType) {
      if (payType != null && payType == PayTypeEnum.ALIPAY.getCode()) {
         return "alipay";
      } else if (payType != null && payType == PayTypeEnum.WECHAT.getCode()) {
         return "wxpay";
      } else {
         throw new CommonException("易支付不支持的支付方式：" + payType, new Object[0]);
      }
   }

   private String normalizeSubject(String subject) {
      String value = StrUtil.blankToDefault(subject, "订单支付");
      return value.length() > 100 ? value.substring(0, 100) : value;
   }

   private String buildParam(String subject, Long memberNo, Integer payType) {
      Map<String, Object> param = new LinkedHashMap<>();
      param.put("subject", subject);
      param.put("member_no", memberNo);
      param.put("pay_type", payType);
      return JSON.toJSONString(param);
   }

   private String buildSubmitForm(String gatewayUrl, Map<String, String> params) {
      String action = StrUtil.removeSuffix(gatewayUrl, "/") + "/submit.php";
      StringBuilder form = new StringBuilder();
      form.append("<form id=\"paying\" action=\"").append(HtmlUtils.htmlEscape(action)).append("\" method=\"post\">");
      params.forEach(
         (key, value) -> form.append("<input type=\"hidden\" name=\"")
            .append(HtmlUtils.htmlEscape(key))
            .append("\" value=\"")
            .append(HtmlUtils.htmlEscape(value))
            .append("\"/>")
      );
      form.append("<input type=\"submit\" value=\"正在跳转\"/>");
      form.append("</form>");
      form.append("<script>document.forms['paying'].submit();</script>");
      return form.toString();
   }

   private Map<String, String> extractParameters(HttpServletRequest request) {
      Map<String, String> params = new LinkedHashMap<>();
      request.getParameterMap().forEach((key, value) -> {
         if (value != null && value.length > 0) {
            params.put(key, value[0]);
         }
      });
      return params;
   }

   private JSONObject parseBusinessParam(String param) {
      return StrUtil.isBlank(param) ? new JSONObject() : JSON.parseObject(param);
   }

   private boolean isPaymentRecordExists(String tradeNo, String orderNo) {
      Long count = this.paymentsRecordMapper
         .selectCount(
            (Wrapper<PaymentsRecord>)((LambdaQueryWrapper<PaymentsRecord>)((LambdaQueryWrapper<PaymentsRecord>)new LambdaQueryWrapper<PaymentsRecord>().eq(PaymentsRecord::getTradeNo, tradeNo)).or())
               .eq(PaymentsRecord::getOrderNo, orderNo)
         );
      return count != null && count > 0L;
   }

   private void savePaymentRecord(Long memberNo, String subject, String orderNo, String tradeNo, BigDecimal totalAmount, Integer payType) {
      PaymentsRecord record = new PaymentsRecord();
      record.setMemberNo(memberNo);
      record.setOrderNo(orderNo);
      record.setTradeNo(tradeNo);
      record.setBusinessType(subject);
      record.setRemark(subject);
      record.setPayTime(new Date());
      record.setPayAmount(totalAmount);
      record.setPayType(payType);
      BaseBalance mb = this.balanceApi.selectByMemberNo(memberNo);
      record.setMemberBalanceAfter(mb != null ? mb.getBalance() : null);
      this.paymentsRecordMapper.insert(record);
   }

   private void publishOrderEvent(String orderNo, Date payTime, String tradeNo, Integer payType) {
      log.info("易支付订单支付事件发布中，订单号:{}", orderNo);
      OrderEvent event = new OrderEvent(this, orderNo, payTime, tradeNo, payType);
      this.applicationContext.publishEvent(event);
      log.info("易支付订单支付事件发布成功，订单号:{}", orderNo);
   }
}
