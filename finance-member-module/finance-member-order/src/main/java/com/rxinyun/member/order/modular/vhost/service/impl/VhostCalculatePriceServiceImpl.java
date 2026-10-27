package com.rxinyun.member.order.modular.vhost.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.DateUtils;
import com.rxinyun.common.vhost.VhostOrderOptionSupport;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.vhost.param.VhostCalculatePriceParam;
import com.rxinyun.member.order.modular.vhost.param.VhostRenewParam;
import com.rxinyun.member.order.modular.vhost.param.VhostUpgradeTrafficParam;
import com.rxinyun.member.order.modular.vhost.response.VhostCalculatePriceResponse;
import com.rxinyun.member.order.modular.vhost.service.VhostCalculatePriceService;
import com.rxinyun.member.production.modular.vhost.line.entity.MemberVhostLine;
import com.rxinyun.member.production.modular.vhost.line.mapper.MemberVhostLineMapper;
import com.rxinyun.member.production.modular.vhost.region.entity.MemberVhostRegion;
import com.rxinyun.member.production.modular.vhost.region.mapper.MemberVhostRegionMapper;
import com.rxinyun.member.production.modular.vhost.version.entity.MemberVhostVersion;
import com.rxinyun.member.production.modular.vhost.version.entity.MemberVhostVersionLine;
import com.rxinyun.member.production.modular.vhost.version.mapper.MemberVhostVersionLineMapper;
import com.rxinyun.member.production.modular.vhost.version.mapper.MemberVhostVersionMapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class VhostCalculatePriceServiceImpl implements VhostCalculatePriceService {
   @Resource
   private MemberVhostVersionMapper memberVhostVersionMapper;
   @Resource
   private MemberVhostLineMapper memberVhostLineMapper;
   @Resource
   private MemberVhostRegionMapper memberVhostRegionMapper;
   @Resource
   private MemberVhostVersionLineMapper memberVhostVersionLineMapper;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;
   @Resource
   private MemberVhostApi memberVhostApi;

   @Override
   public VhostCalculatePriceResponse calculateNewPurchase(VhostCalculatePriceParam param) {
      MemberVhostVersion version = (MemberVhostVersion)this.memberVhostVersionMapper.selectById(param.getVersionId());
      if (version != null && Integer.valueOf(1).equals(version.getStatus())) {
         MemberVhostLine line = (MemberVhostLine)this.memberVhostLineMapper.selectById(param.getLineId());
         if (line != null && Integer.valueOf(1).equals(line.getStatus())) {
            Long bindingCount = this.memberVhostVersionLineMapper
               .selectCount(
                  (Wrapper<MemberVhostVersionLine>)((LambdaQueryWrapper<MemberVhostVersionLine>)new LambdaQueryWrapper<MemberVhostVersionLine>().eq(MemberVhostVersionLine::getVersionId, version.getId()))
                     .eq(MemberVhostVersionLine::getLineId, line.getId())
               );
            if (bindingCount != null && bindingCount != 0L) {
               VhostOrderOptionSupport.requireTrafficSize(param.getTrafficSize(), version.getTrafficMinSize(), version.getTrafficMaxSize());
               int freeSize = version.getTrafficFreeSize() == null ? 0 : version.getTrafficFreeSize();
               int billableTraffic = Math.max(0, param.getTrafficSize() - freeSize);
               BigDecimal monthAmount = version.getMonthPrice()
                  .multiply(BigDecimal.valueOf((long)param.getMonths().intValue()))
                  .setScale(2, RoundingMode.HALF_UP);
               BigDecimal trafficAmount = version.getTrafficPrice().multiply(BigDecimal.valueOf((long)billableTraffic)).setScale(2, RoundingMode.HALF_UP);
               monthAmount = monthAmount.multiply(BigDecimal.valueOf((long)param.getNumber().intValue())).setScale(2, RoundingMode.HALF_UP);
               trafficAmount = trafficAmount.multiply(BigDecimal.valueOf((long)param.getNumber().intValue())).setScale(2, RoundingMode.HALF_UP);
               BigDecimal totalPrice = monthAmount.add(trafficAmount).setScale(2, RoundingMode.HALF_UP);
               String phpVersion = VhostOrderOptionSupport.requirePhpVersion(param.getPhpVersion(), version.getPhpVersions());
               VhostCalculatePriceResponse response = new VhostCalculatePriceResponse();
               response.setVersionId(version.getId());
               response.setVersionName(version.getName());
               response.setLineId(line.getId());
               response.setLineName(line.getName());
               response.setRegionId(line.getRegionId());
               MemberVhostRegion region = (MemberVhostRegion)this.memberVhostRegionMapper.selectById(line.getRegionId());
               response.setRegionName(region == null ? null : region.getName());
               response.setMonths(param.getMonths());
               response.setNumber(param.getNumber());
               response.setTrafficSize(param.getTrafficSize());
               response.setTrafficFreeSize(freeSize);
               response.setBillableTraffic(billableTraffic);
               response.setMonthPrice(version.getMonthPrice());
               response.setTrafficPrice(version.getTrafficPrice());
               response.setMonthAmount(monthAmount);
               response.setTrafficAmount(trafficAmount);
               response.setPhpVersions(version.getPhpVersions());
               response.setPhpVersion(phpVersion);
               response.setMysqlVersions(version.getMysqlVersions());
               response.setDiscountTemplateId(version.getDiscountTemplateId());
               response.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
               this.applyDiscount(response, totalPrice, version.getDiscountTemplateId());
               this.applyCoupon(response, param.getCouponCode(), line.getId(), OrderTypeEnum.NEW_PURCHASE.getCode());
               return response;
            } else {
               throw new CommonException("版本未绑定该线路", new Object[0]);
            }
         } else {
            throw new CommonException("线路不存在或已停用", new Object[0]);
         }
      } else {
         throw new CommonException("版本不存在或已停用", new Object[0]);
      }
   }

   @Override
   public VhostCalculatePriceResponse calculateRenew(VhostRenewParam param) {
      BaseMemberVhost host = this.requireOwnHost(param.getHostId());
      BigDecimal renewPrice = host.getRenewPrice() != null ? host.getRenewPrice() : host.getMonthPrice();
      if (renewPrice != null && renewPrice.compareTo(BigDecimal.ZERO) > 0) {
         MemberVhostVersion version = (MemberVhostVersion)this.memberVhostVersionMapper.selectById(host.getVersionId());
         BigDecimal monthAmount = renewPrice.multiply(BigDecimal.valueOf((long)param.getMonths().intValue())).setScale(2, RoundingMode.HALF_UP);
         VhostCalculatePriceResponse response = this.fromHost(host);
         response.setOrderType(OrderTypeEnum.RENEW.getCode());
         response.setMonths(param.getMonths());
         response.setRenewPrice(renewPrice);
         response.setMonthAmount(monthAmount);
         response.setTrafficAmount(BigDecimal.ZERO);
         response.setDiscountTemplateId(version == null ? null : version.getDiscountTemplateId());
         this.applyDiscount(response, monthAmount, response.getDiscountTemplateId());
         this.applyCoupon(response, param.getCouponCode(), host.getLineId(), OrderTypeEnum.RENEW.getCode());
         return response;
      } else {
         throw new CommonException("续费价格不存在", new Object[0]);
      }
   }

   @Override
   public VhostCalculatePriceResponse calculateUpgradeTraffic(VhostUpgradeTrafficParam param) {
      BaseMemberVhost host = this.requireOwnHost(param.getHostId());
      if (host.getEndTime() == null) {
         throw new CommonException("虚拟主机到期时间不存在", new Object[0]);
      } else {
         int remainingDays = DateUtils.betweenDay(new Date(), host.getEndTime(), false);
         if (remainingDays <= 0) {
            throw new CommonException("虚拟主机已到期，请先续费", new Object[0]);
         } else {
            MemberVhostVersion version = (MemberVhostVersion)this.memberVhostVersionMapper.selectById(host.getVersionId());
            if (version != null && version.getTrafficPrice() != null && version.getTrafficPrice().compareTo(BigDecimal.ZERO) > 0) {
               int currentTraffic = host.getTrafficSize() == null ? 0 : host.getTrafficSize();
               int nextTraffic = currentTraffic + param.getTrafficSize();
               BigDecimal trafficDayPrice = version.getTrafficPrice().divide(BigDecimal.valueOf(30L), 6, RoundingMode.HALF_UP);
               BigDecimal totalPrice = trafficDayPrice.multiply(BigDecimal.valueOf((long)param.getTrafficSize().intValue()))
                  .multiply(BigDecimal.valueOf((long)remainingDays))
                  .setScale(2, RoundingMode.HALF_UP);
               int freeSize = host.getTrafficFreeSize() == null ? 0 : host.getTrafficFreeSize();
               VhostCalculatePriceResponse response = this.fromHost(host);
               response.setOrderType(OrderTypeEnum.UPGRADE.getCode());
               response.setMonths(null);
               response.setRemainingDays(remainingDays);
               response.setTrafficSize(nextTraffic);
               response.setUpgradeTraffic(param.getTrafficSize());
               response.setTrafficFreeSize(freeSize);
               response.setBillableTraffic(Math.max(0, nextTraffic - freeSize));
               response.setTrafficPrice(version.getTrafficPrice());
               response.setPhpVersions(version.getPhpVersions());
               response.setMysqlVersions(version.getMysqlVersions());
               response.setTrafficDayPrice(trafficDayPrice);
               response.setMonthAmount(BigDecimal.ZERO);
               response.setTrafficAmount(totalPrice);
               response.setDiscountTemplateId(version.getDiscountTemplateId());
               this.applyDiscount(response, totalPrice, version.getDiscountTemplateId());
               this.applyCoupon(response, param.getCouponCode(), host.getLineId(), OrderTypeEnum.UPGRADE.getCode());
               return response;
            } else {
               throw new CommonException("流量单价不存在", new Object[0]);
            }
         }
      }
   }

   private BaseMemberVhost requireOwnHost(String hostId) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberVhost host = this.memberVhostApi.selectByIdAndMemberNo(hostId, String.valueOf(loginUser.getId()));
      if (host == null) {
         throw new CommonException("虚拟主机不存在", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         throw new CommonException("虚拟主机已删除", new Object[0]);
      } else {
         return host;
      }
   }

   private VhostCalculatePriceResponse fromHost(BaseMemberVhost host) {
      VhostCalculatePriceResponse response = new VhostCalculatePriceResponse();
      response.setHostId(host.getId());
      response.setVersionId(host.getVersionId());
      response.setVersionName(host.getVersionName());
      response.setRegionId(host.getRegionId());
      response.setRegionName(host.getRegionName());
      response.setLineId(host.getLineId());
      response.setLineName(host.getLineName());
      response.setMonths(host.getMonths());
      response.setTrafficSize(host.getTrafficSize());
      response.setTrafficFreeSize(host.getTrafficFreeSize());
      response.setBillableTraffic(host.getBillableTraffic());
      response.setMonthPrice(host.getMonthPrice());
      response.setRenewPrice(host.getRenewPrice());
      response.setTrafficPrice(host.getTrafficPrice());
      response.setPhpVersions(host.getPhpVersions());
      response.setPhpVersion(host.getPhpVersion());
      response.setDomains(host.getBindDomains());
      response.setMysqlVersions(host.getMysqlVersions());
      return response;
   }

   private void applyDiscount(VhostCalculatePriceResponse response, BigDecimal totalPrice, String discountTemplateId) {
      BigDecimal rate = BigDecimal.ONE;
      if (StrUtil.isNotBlank(discountTemplateId) && StpClientUtil.isLogin()) {
         SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
         BigDecimal queried = this.memberDiscountApi.queryDiscount(loginUser.getGroupId(), discountTemplateId);
         if (queried != null && queried.compareTo(BigDecimal.ZERO) > 0) {
            rate = queried;
         }
      }

      BigDecimal payPrice = totalPrice.multiply(rate).setScale(2, RoundingMode.HALF_UP);
      response.setTotalPrice(totalPrice);
      response.setDiscountRate(rate);
      response.setDiscountPrice(totalPrice.subtract(payPrice));
      response.setCouponPrice(BigDecimal.ZERO);
      response.setPayPrice(payPrice);
   }

   private void applyCoupon(VhostCalculatePriceResponse response, String couponCode, String lineId, Integer orderType) {
      if (!StrUtil.isBlank(couponCode) && StpClientUtil.isLogin()) {
         CouponCalculateResponse couponResponse = this.orderCouponSupportService
            .calculateCoupon(
               StpClientLoginUserUtil.getClientLoginUser().getId(), ProductTypeEnum.VHOST.getCode(), orderType, lineId, couponCode, response.getPayPrice()
            );
         if (couponResponse != null) {
            response.setCouponPrice(couponResponse.getCouponPrice());
            response.setPayPrice(couponResponse.getPayPrice());
         }
      }
   }
}
