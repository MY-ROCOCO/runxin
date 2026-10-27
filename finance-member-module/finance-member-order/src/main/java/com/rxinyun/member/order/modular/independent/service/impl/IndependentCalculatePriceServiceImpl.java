package com.rxinyun.member.order.modular.independent.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentPriceCalculatorHelper;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentRenewCalculatorHelper;
import com.rxinyun.member.order.modular.independent.helper.MemberIndependentUpgradeCalculatorHelper;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentCalculatePriceParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.member.order.modular.independent.service.IndependentCalculatePriceService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IndependentCalculatePriceServiceImpl implements IndependentCalculatePriceService {
   private static final Logger log = LoggerFactory.getLogger(IndependentCalculatePriceServiceImpl.class);
   @Resource
   private MemberIndependentPriceCalculatorHelper independentPriceCalculatorHelper;
   @Resource
   private MemberIndependentUpgradeCalculatorHelper independentUpgradeCalculatorHelper;
   @Resource
   private MemberIndependentRenewCalculatorHelper independentRenewCalculatorHelper;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;
   @Resource
   private MemberIndependentApi memberIndependentApi;

   @Override
   public CommonResult<IndependentCalculatePriceResponse> calculatePrice(MemberIndependentCalculatePriceParam calculatePriceParam) {
      log.info("独立服务器订单计价:{}", JSONUtil.toJsonStr(calculatePriceParam));
      IndependentCalculatePriceResponse response = this.independentPriceCalculatorHelper.calculatePrice(calculatePriceParam);
      if (StrUtil.isNotBlank(calculatePriceParam.getCouponCode())) {
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
               OrderTypeEnum.NEW_PURCHASE.getCode(),
               calculatePriceParam.getLineId(),
               calculatePriceParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> upgradeIp(MemberIndependentIpUpgradeOrderParam upgradeOrderParam) {
      log.info("独立服务器订单IP升级计价:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      IndependentCalculatePriceResponse response = this.independentUpgradeCalculatorHelper.upgradeIpCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> upgradeBandwidth(MemberIndependentBandwidthUpgradeOrderParam upgradeOrderParam) {
      log.info("独立服务器订单带宽升级计价:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      IndependentCalculatePriceResponse response = this.independentUpgradeCalculatorHelper.upgradeBandwidthCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> upgradeDefense(MemberIndependentDefenseUpgradeOrderParam upgradeOrderParam) {
      log.info("独立服务器订单防御升级计价:{}", JSONUtil.toJsonStr(upgradeOrderParam));
      IndependentCalculatePriceResponse response = this.independentUpgradeCalculatorHelper.upgradeDefenseCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> renew(MemberIndependentRenewOrderParam renewOrderParam) {
      log.info("独立服务器订单续费计价:{}", JSONUtil.toJsonStr(renewOrderParam));
      IndependentCalculatePriceResponse response = this.independentRenewCalculatorHelper.renewCalculatePrice(renewOrderParam);
      if (StrUtil.isNotBlank(renewOrderParam.getCouponCode())) {
         String lineId = this.independentRenewCalculatorHelper.resolveCouponLineId(response, renewOrderParam.getCouponCode());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.INDEPENDENT_SERVER.getCode(),
               OrderTypeEnum.RENEW.getCode(),
               lineId,
               renewOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }
}
