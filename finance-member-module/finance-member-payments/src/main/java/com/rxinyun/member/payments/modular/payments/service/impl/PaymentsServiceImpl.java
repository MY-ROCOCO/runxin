package com.rxinyun.member.payments.modular.payments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.business.PayTypeEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.user.param.RechargeAddParam;
import com.rxinyun.member.auth.modular.user.service.RechargeService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.member.payments.modular.payments.mapper.PaymentsRecordMapper;
import com.rxinyun.member.payments.modular.payments.param.BalanceRechargeParam;
import com.rxinyun.member.payments.modular.payments.param.PaymentsRecordPageParam;
import com.rxinyun.member.payments.modular.payments.service.AlipayService;
import com.rxinyun.member.payments.modular.payments.service.BalancePaymentService;
import com.rxinyun.member.payments.modular.payments.service.PaymentsService;
import com.rxinyun.member.payments.modular.payments.service.UsdtPayService;
import com.rxinyun.member.payments.modular.payments.service.WechatPayService;
import com.rxinyun.member.payments.modular.payments.service.YipayService;
import com.rxinyun.payments.api.pojo.PendingUsdtPaymentDto;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentsServiceImpl extends ServiceImpl<PaymentsRecordMapper, PaymentsRecord> implements PaymentsService {
   private static final Logger log = LoggerFactory.getLogger(PaymentsServiceImpl.class);
   @Autowired
   private BalancePaymentService balancePaymentService;
   @Autowired
   private AlipayService alipayService;
   @Autowired
   private WechatPayService wechatPayService;
   @Autowired
   private UsdtPayService usdtPayService;
   @Autowired
   private YipayService yipayService;
   @Autowired
   private RechargeService rechargeService;

   @Override
   public Page<PaymentsRecord> page(PaymentsRecordPageParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<PaymentsRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().and(q -> q.eq(PaymentsRecord::getMemberNo, clientLoginUser.getId()));
      queryWrapper.lambda().orderByDesc(PaymentsRecord::getCreateTime);
      return (Page<PaymentsRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public CommonResult pay(Integer payType, String platform, String subject, Long memberNo, String orderNo, BigDecimal totalAmount) {
      PayTypeEnum payTypeEnum = PayTypeEnum.fromCode(payType);
      if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
         return CommonResult.error("支付金额不合法");
      } else if (this.yipayService.isRouteEnabled(payType)) {
         log.info("易支付路由支付: payType={}, 产品名称={}, 会员编号={}, 订单号={}, 金额={}", new Object[]{payType, subject, memberNo, orderNo, totalAmount});
         return this.yipayService.pay(payType, subject, memberNo, orderNo, totalAmount);
      } else {
         switch (payTypeEnum) {
            case ALIPAY:
               log.info("支付宝支付");
               return this.alipayService.pay(platform, subject, memberNo, orderNo, totalAmount);
            case WECHAT:
               log.info("微信支付");
               return this.wechatPayService.pay(platform, subject, memberNo, orderNo, totalAmount);
            case INTEGRAL:
               log.info("积分支付");
               return null;
            case BALANCE:
               log.info("余额支付");
               return this.balancePaymentService.pay(subject, memberNo, orderNo, totalAmount, subject + memberNo);
            case USDT:
               log.info("USDT 支付");
               return this.usdtPayService.pay(platform, subject, memberNo, orderNo, totalAmount);
            default:
               log.error("支付类型错误");
               return CommonResult.error("支付类型错误");
         }
      }
   }

   @Override
   public String alipayNotify(HttpServletRequest request) {
      return this.alipayService.notify(request);
   }

   @Override
   public CommonResult wechatPayNotify(HttpServletRequest request) throws IOException, ParseException {
      return this.wechatPayService.notify(request);
   }

   @Override
   public String yipayNotify(HttpServletRequest request) {
      return this.yipayService.notify(request);
   }

   @Override
   public CommonResult recharge(BalanceRechargeParam balanceRechargeParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      RechargeAddParam recharge = (RechargeAddParam)BeanUtil.copyProperties(balanceRechargeParam, RechargeAddParam.class, new String[0]);
      recharge.setStatus(1);
      recharge.setMemberNo(clientLoginUser.getId());
      String rechargeId = this.rechargeService.add(recharge);
      if (null == rechargeId) {
         log.error("充值单创建失败: memberNo={}", clientLoginUser.getId());
         return CommonResult.error("充值单创建失败");
      } else {
         String businessType = "用户充值";
         CommonResult payResult = this.pay(
            balanceRechargeParam.getPayType(),
            balanceRechargeParam.getPlatform(),
            businessType,
            clientLoginUser.getId(),
            rechargeId,
            balanceRechargeParam.getAmount()
         );
         if (payResult != null && 200 == payResult.getCode() && PayTypeEnum.WECHAT.getCode() == balanceRechargeParam.getPayType()) {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("rechargeId", rechargeId);
            data.put("orderNo", rechargeId);
            data.put("payData", payResult.getData());
            return CommonResult.get(payResult.getCode(), payResult.getMsg(), data);
         } else {
            return payResult;
         }
      }
   }

   @Override
   public CommonResult confirmUsdtPayment(String orderNo, Date payTime, String txHash) {
      return this.usdtPayService.confirmUsdtPayment(orderNo, payTime, txHash);
   }

   @Override
   public List<PendingUsdtPaymentDto> getPendingUsdtPayments() {
      return this.usdtPayService.getPendingUsdtPayments();
   }
}
