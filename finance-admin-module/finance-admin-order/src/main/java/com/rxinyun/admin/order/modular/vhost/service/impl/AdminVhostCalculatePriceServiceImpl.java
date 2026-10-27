package com.rxinyun.admin.order.modular.vhost.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostCalculateParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostRenewParam;
import com.rxinyun.admin.order.modular.vhost.param.AdminVhostUpgradeParam;
import com.rxinyun.admin.order.modular.vhost.response.AdminVhostPriceResponse;
import com.rxinyun.admin.order.modular.vhost.service.AdminVhostCalculatePriceService;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.mapper.VhostLineMapper;
import com.rxinyun.admin.production.modular.vhost.region.entity.VhostRegion;
import com.rxinyun.admin.production.modular.vhost.region.mapper.VhostRegionMapper;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersion;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersionLine;
import com.rxinyun.admin.production.modular.vhost.version.mapper.VhostVersionLineMapper;
import com.rxinyun.admin.production.modular.vhost.version.mapper.VhostVersionMapper;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.DateUtils;
import com.rxinyun.common.vhost.VhostOrderOptionSupport;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class AdminVhostCalculatePriceServiceImpl implements AdminVhostCalculatePriceService {
   @Resource
   private VhostVersionMapper vhostVersionMapper;
   @Resource
   private VhostLineMapper vhostLineMapper;
   @Resource
   private VhostRegionMapper vhostRegionMapper;
   @Resource
   private VhostVersionLineMapper vhostVersionLineMapper;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;
   @Resource
   private MemberApi memberApi;
   @Resource
   private MemberVhostApi memberVhostApi;

   @Override
   public AdminVhostPriceResponse calculateNewPurchase(AdminVhostCalculateParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      VhostVersion version = (VhostVersion)this.vhostVersionMapper.selectById(param.getVersionId());
      if (version != null && Integer.valueOf(1).equals(version.getStatus())) {
         VhostLine line = (VhostLine)this.vhostLineMapper.selectById(param.getLineId());
         if (line != null && Integer.valueOf(1).equals(line.getStatus())) {
            Long bindingCount = this.vhostVersionLineMapper
               .selectCount(
                  (Wrapper<VhostVersionLine>)((LambdaQueryWrapper<VhostVersionLine>)new LambdaQueryWrapper<VhostVersionLine>().eq(VhostVersionLine::getVersionId, version.getId()))
                     .eq(VhostVersionLine::getLineId, line.getId())
               );
            if (bindingCount != null && bindingCount != 0L) {
               VhostOrderOptionSupport.requireTrafficSize(param.getTrafficSize(), version.getTrafficMinSize(), version.getTrafficMaxSize());
               int freeSize = version.getTrafficFreeSize() == null ? 0 : version.getTrafficFreeSize();
               int billableTraffic = Math.max(0, param.getTrafficSize() - freeSize);
               BigDecimal monthAmount = version.getMonthPrice()
                  .multiply(BigDecimal.valueOf((long)param.getMonths().intValue()))
                  .multiply(BigDecimal.valueOf((long)param.getNumber().intValue()))
                  .setScale(2, RoundingMode.HALF_UP);
               BigDecimal trafficAmount = version.getTrafficPrice()
                  .multiply(BigDecimal.valueOf((long)billableTraffic))
                  .multiply(BigDecimal.valueOf((long)param.getNumber().intValue()))
                  .setScale(2, RoundingMode.HALF_UP);
               BigDecimal totalPrice = monthAmount.add(trafficAmount).setScale(2, RoundingMode.HALF_UP);
               String phpVersion = VhostOrderOptionSupport.requirePhpVersion(param.getPhpVersion(), version.getPhpVersions());
               AdminVhostPriceResponse response = new AdminVhostPriceResponse();
               response.setOrderType(OrderTypeEnum.NEW_PURCHASE.getCode());
               response.setVersionId(version.getId());
               response.setVersionName(version.getName());
               response.setLineId(line.getId());
               response.setLineName(line.getName());
               response.setRegionId(line.getRegionId());
               VhostRegion region = (VhostRegion)this.vhostRegionMapper.selectById(line.getRegionId());
               response.setRegionName(region == null ? null : region.getName());
               response.setMonths(param.getMonths());
               response.setNumber(param.getNumber());
               response.setTrafficSize(param.getTrafficSize());
               response.setTrafficFreeSize(freeSize);
               response.setBillableTraffic(billableTraffic);
               response.setMonthPrice(version.getMonthPrice());
               response.setRenewPrice(version.getMonthPrice());
               response.setTrafficPrice(version.getTrafficPrice());
               response.setMonthAmount(monthAmount);
               response.setTrafficAmount(trafficAmount);
               response.setPhpVersions(version.getPhpVersions());
               response.setPhpVersion(phpVersion);
               response.setMysqlVersions(version.getMysqlVersions());
               response.setDiscountTemplateId(version.getDiscountTemplateId());
               this.applyDiscount(response, totalPrice, version.getDiscountTemplateId(), member);
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
   public AdminVhostPriceResponse calculateRenew(AdminVhostRenewParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseMemberVhost host = this.requireHost(param.getHostId(), param.getMemberId());
      BigDecimal renewPrice = host.getRenewPrice() != null ? host.getRenewPrice() : host.getMonthPrice();
      if (renewPrice != null && renewPrice.compareTo(BigDecimal.ZERO) > 0) {
         VhostVersion version = (VhostVersion)this.vhostVersionMapper.selectById(host.getVersionId());
         BigDecimal monthAmount = renewPrice.multiply(BigDecimal.valueOf((long)param.getMonths().intValue())).setScale(2, RoundingMode.HALF_UP);
         AdminVhostPriceResponse response = this.fromHost(host);
         response.setOrderType(OrderTypeEnum.RENEW.getCode());
         response.setMonths(param.getMonths());
         response.setNumber(1);
         response.setRenewPrice(renewPrice);
         response.setMonthAmount(monthAmount);
         response.setTrafficAmount(BigDecimal.ZERO);
         response.setDiscountTemplateId(version == null ? null : version.getDiscountTemplateId());
         this.applyDiscount(response, monthAmount, response.getDiscountTemplateId(), member);
         return response;
      } else {
         throw new CommonException("续费价格不存在", new Object[0]);
      }
   }

   @Override
   public AdminVhostPriceResponse calculateUpgradeTraffic(AdminVhostUpgradeParam param) {
      BaseMember member = this.requireMember(param.getMemberId());
      BaseMemberVhost host = this.requireHost(param.getHostId(), param.getMemberId());
      if (host.getEndTime() == null) {
         throw new CommonException("虚拟主机到期时间不存在", new Object[0]);
      } else {
         int remainingDays = DateUtils.betweenDay(new Date(), host.getEndTime(), false);
         if (remainingDays <= 0) {
            throw new CommonException("虚拟主机已到期，请先续费", new Object[0]);
         } else {
            VhostVersion version = (VhostVersion)this.vhostVersionMapper.selectById(host.getVersionId());
            if (version != null && version.getTrafficPrice() != null && version.getTrafficPrice().compareTo(BigDecimal.ZERO) > 0) {
               int currentTraffic = host.getTrafficSize() == null ? 0 : host.getTrafficSize();
               int nextTraffic = currentTraffic + param.getTrafficSize();
               BigDecimal trafficDayPrice = version.getTrafficPrice().divide(BigDecimal.valueOf(30L), 6, RoundingMode.HALF_UP);
               BigDecimal totalPrice = trafficDayPrice.multiply(BigDecimal.valueOf((long)param.getTrafficSize().intValue()))
                  .multiply(BigDecimal.valueOf((long)remainingDays))
                  .setScale(2, RoundingMode.HALF_UP);
               int freeSize = host.getTrafficFreeSize() == null ? 0 : host.getTrafficFreeSize();
               AdminVhostPriceResponse response = this.fromHost(host);
               response.setOrderType(OrderTypeEnum.UPGRADE.getCode());
               response.setMonths(null);
               response.setNumber(1);
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
               this.applyDiscount(response, totalPrice, version.getDiscountTemplateId(), member);
               return response;
            } else {
               throw new CommonException("流量单价不存在", new Object[0]);
            }
         }
      }
   }

   private BaseMember requireMember(Long memberId) {
      BaseMember member = this.memberApi.detail(memberId);
      if (member == null) {
         throw new CommonException("会员不存在", new Object[0]);
      } else {
         return member;
      }
   }

   private BaseMemberVhost requireHost(String hostId, Long memberId) {
      BaseMemberVhost host = this.memberVhostApi.selectByIdAndMemberNo(hostId, String.valueOf(memberId));
      if (host == null) {
         throw new CommonException("虚拟主机不存在", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         throw new CommonException("虚拟主机已删除", new Object[0]);
      } else {
         return host;
      }
   }

   private AdminVhostPriceResponse fromHost(BaseMemberVhost host) {
      AdminVhostPriceResponse response = new AdminVhostPriceResponse();
      response.setHostId(host.getId());
      response.setOriginalOrderNo(host.getOrderNo());
      response.setVersionId(host.getVersionId());
      response.setVersionName(host.getVersionName());
      response.setRegionId(host.getRegionId());
      response.setRegionName(host.getRegionName());
      response.setLineId(host.getLineId());
      response.setLineName(host.getLineName());
      response.setTrafficSize(host.getTrafficSize());
      response.setTrafficFreeSize(host.getTrafficFreeSize());
      response.setBillableTraffic(host.getBillableTraffic());
      response.setMonthPrice(host.getMonthPrice());
      response.setRenewPrice(host.getRenewPrice());
      response.setPhpVersions(host.getPhpVersions());
      response.setPhpVersion(host.getPhpVersion());
      response.setDomains(host.getBindDomains());
      response.setMysqlVersions(host.getMysqlVersions());
      return response;
   }

   private void applyDiscount(AdminVhostPriceResponse response, BigDecimal totalPrice, String discountTemplateId, BaseMember member) {
      BigDecimal rate = BigDecimal.ONE;
      if (StrUtil.isNotBlank(discountTemplateId) && member != null && StrUtil.isNotBlank(member.getGroupId())) {
         BigDecimal queried = this.memberDiscountApi.queryDiscount(member.getGroupId(), discountTemplateId);
         if (queried != null && queried.compareTo(BigDecimal.ZERO) > 0) {
            rate = queried;
         }
      }

      BigDecimal payPrice = totalPrice.multiply(rate).setScale(2, RoundingMode.HALF_UP);
      response.setTotalPrice(totalPrice);
      response.setDiscountRate(rate);
      response.setDiscountPrice(totalPrice.subtract(payPrice));
      response.setPayPrice(payPrice);
   }
}
