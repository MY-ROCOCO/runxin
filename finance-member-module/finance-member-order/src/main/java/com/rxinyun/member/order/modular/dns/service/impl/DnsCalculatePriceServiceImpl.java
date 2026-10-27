package com.rxinyun.member.order.modular.dns.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.dns.param.DnsCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.param.DnsRenewCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.param.DnsUpgradeQuotaCalculatePriceParam;
import com.rxinyun.member.order.modular.dns.response.DnsCalculatePriceResponse;
import com.rxinyun.member.order.modular.dns.service.DnsCalculatePriceService;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsPackageVO;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class DnsCalculatePriceServiceImpl implements DnsCalculatePriceService {
   private static final long ONE_DAY_MILLIS = 86400000L;
   @Resource
   private DnsPackageInstanceApi dnsPackageInstanceApi;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;

   @Override
   public DnsCalculatePriceResponse calculateNewPurchase(DnsCalculatePriceParam param) {
      DnsPackageVO dnsPackage = this.dnsPackageInstanceApi.getActivePackage(param.getPackageId());
      BigDecimal totalPrice = dnsPackage.getSingleDomainPrice()
         .multiply(BigDecimal.valueOf((long)param.getDomainQuota().intValue()))
         .multiply(BigDecimal.valueOf((long)param.getPeriodMonths().intValue()))
         .setScale(2, RoundingMode.HALF_UP);
      DnsCalculatePriceResponse response = new DnsCalculatePriceResponse();
      response.setPackageId(dnsPackage.getId());
      response.setPackageName(dnsPackage.getName());
      response.setLineGroupId(dnsPackage.getLineGroupId());
      response.setSingleDomainPrice(dnsPackage.getSingleDomainPrice());
      response.setMinTtl(dnsPackage.getMinTtl());
      response.setDomainQuota(param.getDomainQuota());
      response.setPeriodMonths(param.getPeriodMonths());
      this.fillPrice(response, totalPrice, OrderTypeEnum.NEW_PURCHASE.getCode(), param.getCouponCode());
      return response;
   }

   @Override
   public DnsCalculatePriceResponse calculateRenew(DnsRenewCalculatePriceParam param) {
      DnsPackageInstanceVO instance = this.queryOwnInstance(param.getInstanceId());
      BigDecimal totalPrice = instance.getSingleDomainPrice()
         .multiply(BigDecimal.valueOf((long)instance.getDomainQuota().intValue()))
         .multiply(BigDecimal.valueOf((long)param.getPeriodMonths().intValue()))
         .setScale(2, RoundingMode.HALF_UP);
      DnsCalculatePriceResponse response = this.fromInstance(instance);
      response.setPeriodMonths(param.getPeriodMonths());
      this.fillPrice(response, totalPrice, OrderTypeEnum.RENEW.getCode(), param.getCouponCode());
      return response;
   }

   @Override
   public DnsCalculatePriceResponse calculateUpgradeQuota(DnsUpgradeQuotaCalculatePriceParam param) {
      DnsPackageInstanceVO instance = this.queryOwnInstance(param.getInstanceId());
      int remainingDays = this.calculateRemainingDays(instance.getNextDueTime());
      BigDecimal totalPrice = instance.getSingleDomainPrice()
         .multiply(BigDecimal.valueOf((long)param.getUpgradeDomainQuota().intValue()))
         .multiply(BigDecimal.valueOf((long)remainingDays))
         .divide(BigDecimal.valueOf(30L), 2, RoundingMode.HALF_UP);
      DnsCalculatePriceResponse response = this.fromInstance(instance);
      response.setDomainQuota(instance.getDomainQuota() + param.getUpgradeDomainQuota());
      response.setUpgradeDomainQuota(param.getUpgradeDomainQuota());
      response.setRemainingDays(remainingDays);
      this.fillPrice(response, totalPrice, OrderTypeEnum.UPGRADE.getCode(), param.getCouponCode());
      return response;
   }

   private DnsPackageInstanceVO queryOwnInstance(String instanceId) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      return this.dnsPackageInstanceApi.getMemberInstance(instanceId, String.valueOf(loginUser.getId()));
   }

   private DnsCalculatePriceResponse fromInstance(DnsPackageInstanceVO instance) {
      DnsCalculatePriceResponse response = new DnsCalculatePriceResponse();
      response.setInstanceId(instance.getId());
      response.setPackageId(instance.getPackageId());
      response.setPackageName(instance.getPackageName());
      response.setLineGroupId(instance.getLineGroupId());
      response.setSingleDomainPrice(instance.getSingleDomainPrice());
      response.setMinTtl(instance.getMinTtl());
      response.setDomainQuota(instance.getDomainQuota());
      response.setPeriodMonths(instance.getPeriodMonths());
      response.setNextDueTime(instance.getNextDueTime());
      return response;
   }

   private void fillPrice(DnsCalculatePriceResponse response, BigDecimal totalPrice, Integer orderType, String couponCode) {
      response.setTotalPrice(totalPrice);
      response.setCouponPrice(BigDecimal.ZERO);
      response.setPayPrice(totalPrice);
      if (!StrUtil.isBlank(couponCode)) {
         CouponCalculateResponse couponResponse = this.orderCouponSupportService
            .calculateCoupon(
               StpClientLoginUserUtil.getClientLoginUser().getId(), ProductTypeEnum.DNS.getCode(), orderType, null, couponCode, response.getPayPrice()
            );
         if (couponResponse != null) {
            response.setCouponPrice(couponResponse.getCouponPrice());
            response.setPayPrice(couponResponse.getPayPrice());
         }
      }
   }

   private int calculateRemainingDays(Date nextDueTime) {
      if (nextDueTime != null && nextDueTime.after(new Date())) {
         long diff = nextDueTime.getTime() - System.currentTimeMillis();
         return (int)Math.max(1L, (diff + 86400000L - 1L) / 86400000L);
      } else {
         throw new CommonException("DNS套餐实例已到期，无法升级域名数量", new Object[0]);
      }
   }
}
