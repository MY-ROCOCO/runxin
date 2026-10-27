package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.ecs.helper.MemberPriceCalculatorHelper;
import com.rxinyun.member.order.modular.ecs.helper.MemberRenewCalculatorHelper;
import com.rxinyun.member.order.modular.ecs.helper.MemberUpgradeCalculatorHelper;
import com.rxinyun.member.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberRenewOrderParam;
import com.rxinyun.member.order.modular.ecs.response.MemberCalculatePriceResponse;
import com.rxinyun.member.order.modular.ecs.service.MemberCalculatePriceService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberCalculatePriceServiceImpl implements MemberCalculatePriceService {
   private static final Logger log = LoggerFactory.getLogger(MemberCalculatePriceServiceImpl.class);
   @Resource
   private MemberPriceCalculatorHelper memberPriceCalculatorHelper;
   @Resource
   private MemberUpgradeCalculatorHelper memberUpgradeCalculatorHelper;
   @Resource
   private MemberRenewCalculatorHelper memberRenewCalculatorHelper;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;
   @Resource
   private MemberServerApi memberServerApi;

   @Override
   public CommonResult<MemberCalculatePriceResponse> calculatePrice(CalculatePriceParam calculatePriceParam) {
      log.info("云服务器订单计价参数calculatePrice：{}", calculatePriceParam);
      MemberCalculatePriceResponse response = this.memberPriceCalculatorHelper.calculatePrice(calculatePriceParam);
      if (StrUtil.isNotBlank(calculatePriceParam.getCouponCode())) {
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.CLOUD_SERVER.getCode(),
               OrderTypeEnum.NEW_PURCHASE.getCode(),
               calculatePriceParam.getLineId(),
               calculatePriceParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<MemberCalculatePriceResponse> upgradeCore(MemberCoreUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeCore：{}", upgradeOrderParam);
      MemberCalculatePriceResponse response = this.memberUpgradeCalculatorHelper.upgradeCoreCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberServer memberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.CLOUD_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<MemberCalculatePriceResponse> upgradeDisk(MemberDiskUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeDisk：{}", upgradeOrderParam);
      MemberCalculatePriceResponse response = this.memberUpgradeCalculatorHelper.upgradeDiskCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberServer memberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.CLOUD_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<MemberCalculatePriceResponse> upgradeBandwidth(MemberBandwidthUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeBandwidth：{}", upgradeOrderParam);
      MemberCalculatePriceResponse response = this.memberUpgradeCalculatorHelper.upgradeBandwidthCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberServer memberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.CLOUD_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<MemberCalculatePriceResponse> upgradeDefense(MemberDefenseUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeDefense：{}", upgradeOrderParam);
      MemberCalculatePriceResponse response = this.memberUpgradeCalculatorHelper.upgradeDefenseCalculatePrice(upgradeOrderParam);
      if (StrUtil.isNotBlank(upgradeOrderParam.getCouponCode())) {
         BaseMemberServer memberServer = this.memberServerApi.selectByServerId(upgradeOrderParam.getServerId());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.CLOUD_SERVER.getCode(),
               OrderTypeEnum.UPGRADE.getCode(),
               memberServer == null ? null : memberServer.getLineId(),
               upgradeOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }

   @Override
   public CommonResult<MemberCalculatePriceResponse> renew(MemberRenewOrderParam renewOrderParam) {
      log.info("云服务器订单计价参数renew：{}", renewOrderParam);
      MemberCalculatePriceResponse response = this.memberRenewCalculatorHelper.renewCalculatePrice(renewOrderParam);
      if (StrUtil.isNotBlank(renewOrderParam.getCouponCode())) {
         String lineId = this.memberRenewCalculatorHelper.resolveCouponLineId(response, renewOrderParam.getCouponCode());
         this.orderCouponSupportService
            .applyCoupon(
               response,
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.CLOUD_SERVER.getCode(),
               OrderTypeEnum.RENEW.getCode(),
               lineId,
               renewOrderParam.getCouponCode()
            );
      }

      return CommonResult.data(response);
   }
}
