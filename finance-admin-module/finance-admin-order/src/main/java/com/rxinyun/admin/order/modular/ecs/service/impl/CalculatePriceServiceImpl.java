package com.rxinyun.admin.order.modular.ecs.service.impl;

import com.rxinyun.admin.order.modular.ecs.helper.PriceCalculatorHelper;
import com.rxinyun.admin.order.modular.ecs.helper.RenewCalculatorHelper;
import com.rxinyun.admin.order.modular.ecs.helper.UpgradeCalculatorHelper;
import com.rxinyun.admin.order.modular.ecs.param.BandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.admin.order.modular.ecs.param.CoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.admin.order.modular.ecs.service.CalculatePriceService;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CalculatePriceServiceImpl implements CalculatePriceService {
   private static final Logger log = LoggerFactory.getLogger(CalculatePriceServiceImpl.class);
   @Resource
   private PriceCalculatorHelper priceCalculatorHelper;
   @Resource
   private UpgradeCalculatorHelper upgradeCalculatorHelper;
   @Resource
   private RenewCalculatorHelper renewCalculatorHelper;

   @Override
   public CommonResult<CalculatePriceResponse> calculatePrice(CalculatePriceParam calculatePriceParam) {
      log.info("云服务器订单计价参数calculatePrice：{}", calculatePriceParam);
      return CommonResult.data(this.priceCalculatorHelper.calculatePrice(calculatePriceParam));
   }

   @Override
   public CommonResult<CalculatePriceResponse> upgradeCore(CoreUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeCore：{}", upgradeOrderParam);
      return CommonResult.data(this.upgradeCalculatorHelper.upgradeCoreCalculatePrice(upgradeOrderParam));
   }

   @Override
   public CommonResult<CalculatePriceResponse> upgradeDisk(DiskUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeDisk：{}", upgradeOrderParam);
      return CommonResult.data(this.upgradeCalculatorHelper.upgradeDiskCalculatePrice(upgradeOrderParam));
   }

   @Override
   public CommonResult<CalculatePriceResponse> upgradeBandwidth(BandwidthUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeBandwidth：{}", upgradeOrderParam);
      return CommonResult.data(this.upgradeCalculatorHelper.upgradeBandwidthCalculatePrice(upgradeOrderParam));
   }

   @Override
   public CommonResult<CalculatePriceResponse> upgradeDefense(DefenseUpgradeOrderParam upgradeOrderParam) {
      log.info("云服务器订单计价参数upgradeDefense：{}", upgradeOrderParam);
      return CommonResult.data(this.upgradeCalculatorHelper.upgradeDefenseCalculatePrice(upgradeOrderParam));
   }

   @Override
   public CommonResult<CalculatePriceResponse> renew(RenewOrderParam renewOrderParam) {
      log.info("云服务器订单计价参数renew：{}", renewOrderParam);
      return CommonResult.data(this.renewCalculatorHelper.renewCalculatePrice(renewOrderParam));
   }
}
