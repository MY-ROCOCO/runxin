package com.rxinyun.admin.order.modular.independent.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.order.modular.api.helper.IndependentPoolUpgradePricing;
import com.rxinyun.admin.order.modular.api.helper.IndependentResourcePoolPriceCalculator;
import com.rxinyun.admin.order.modular.api.helper.IndependentResourcePoolUpgradeCalculator;
import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.admin.order.modular.independent.param.IndependentBandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentCalculatePriceParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentDefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentIpUpgradeOrderParam;
import com.rxinyun.admin.order.modular.independent.param.IndependentRenewOrderParam;
import com.rxinyun.admin.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.admin.order.modular.independent.response.IndependentRenewItemResponse;
import com.rxinyun.admin.order.modular.independent.service.IndependentCalculatePriceService;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.RenewServerIdSupport;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("adminIndependentCalculatePriceService")
public class IndependentCalculatePriceServiceImpl implements IndependentCalculatePriceService {
   private static final Logger log = LoggerFactory.getLogger(IndependentCalculatePriceServiceImpl.class);
   @Resource
   private IndependentResourcePoolPriceCalculator independentResourcePoolPriceCalculator;
   @Resource
   private IndependentResourcePoolUpgradeCalculator independentResourcePoolUpgradeCalculator;
   @Resource
   private IndependentApi independentApi;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private MemberApi memberApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;
   @Resource
   private AdminMachineApi adminMachineApi;

   @Override
   public CommonResult<IndependentCalculatePriceResponse> calculatePrice(IndependentCalculatePriceParam calculatePriceParam) {
      log.info("后台独立服务器订单计价参数calculatePrice：{}", JSONUtil.toJsonStr(calculatePriceParam));
      IndependentOrderServerConfig config = new IndependentOrderServerConfig();
      config.setRegionId(calculatePriceParam.getRegionId());
      config.setLineId(calculatePriceParam.getLineId());
      this.independentResourcePoolPriceCalculator
         .fillNewPurchasePricing(
            config,
            calculatePriceParam.getBillingMode(),
            calculatePriceParam.getUnit(),
            calculatePriceParam.getMachineId(),
            calculatePriceParam.getLineId(),
            calculatePriceParam.getBandwidth(),
            calculatePriceParam.getDefense(),
            calculatePriceParam.getIpSize(),
            calculatePriceParam.getDuration(),
            calculatePriceParam.getNumber()
         );
      IndependentCalculatePriceResponse response = this.convertFromConfig(config);
      response.setBillingMode(calculatePriceParam.getBillingMode());
      this.applyLineDiscount(response, calculatePriceParam.getMemberNo(), calculatePriceParam.getLineId());
      response.setCouponPrice(BigDecimal.ZERO);
      return CommonResult.data(response);
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> upgradeIp(IndependentIpUpgradeOrderParam upgradeOrderParam) {
      log.info("后台独立服务器订单IP升级计价参数upgradeIp：{}", JSONUtil.toJsonStr(upgradeOrderParam));
      BaseIndependent server = this.requireServer(upgradeOrderParam.getServerId());
      Long memberNo = this.requireMemberNo(server);
      IndependentPoolUpgradePricing pricing = this.independentResourcePoolUpgradeCalculator.computeIpUpgrade(server, upgradeOrderParam.getIpSize(), memberNo);
      return CommonResult.data(this.convertFromUpgradePricing(pricing, "IP升级差价"));
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> upgradeBandwidth(IndependentBandwidthUpgradeOrderParam upgradeOrderParam) {
      log.info("后台独立服务器订单带宽升级计价参数upgradeBandwidth：{}", JSONUtil.toJsonStr(upgradeOrderParam));
      BaseIndependent server = this.requireServer(upgradeOrderParam.getServerId());
      Long memberNo = this.requireMemberNo(server);
      IndependentPoolUpgradePricing pricing = this.independentResourcePoolUpgradeCalculator
         .computeBandwidthUpgrade(server, upgradeOrderParam.getBandwidth(), memberNo);
      return CommonResult.data(this.convertFromUpgradePricing(pricing, "带宽升级差价"));
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> upgradeDefense(IndependentDefenseUpgradeOrderParam upgradeOrderParam) {
      log.info("后台独立服务器订单防御升级计价参数upgradeDefense：{}", JSONUtil.toJsonStr(upgradeOrderParam));
      BaseIndependent server = this.requireServer(upgradeOrderParam.getServerId());
      Long memberNo = this.requireMemberNo(server);
      IndependentPoolUpgradePricing pricing = this.independentResourcePoolUpgradeCalculator
         .computeDefenseUpgrade(server, upgradeOrderParam.getDefense(), memberNo);
      return CommonResult.data(this.convertFromUpgradePricing(pricing, "防御升级差价"));
   }

   @Override
   public CommonResult<IndependentCalculatePriceResponse> renew(IndependentRenewOrderParam renewOrderParam) {
      log.info("后台独立服务器订单续费计价参数renew：{}", JSONUtil.toJsonStr(renewOrderParam));
      List<String> serverIds = RenewServerIdSupport.resolve(renewOrderParam.getServerId(), renewOrderParam.getServerIds());
      Integer duration = renewOrderParam.getDuration();
      if (duration != null && duration > 0) {
         List<IndependentRenewItemResponse> items = new ArrayList<>();
         IndependentCalculatePriceResponse first = null;
         BigDecimal totalPrice = BigDecimal.ZERO;
         BigDecimal discountPrice = BigDecimal.ZERO;
         BigDecimal payPrice = BigDecimal.ZERO;

         for (String serverId : serverIds) {
            IndependentCalculatePriceResponse one = this.calculateRenewOne(serverId, duration);
            if (first == null) {
               first = one;
            }

            items.add(this.toRenewItem(serverId, one));
            totalPrice = totalPrice.add(this.nz(one.getTotalPrice()));
            discountPrice = discountPrice.add(this.nz(one.getDiscountPrice()));
            payPrice = payPrice.add(this.nz(one.getPayPrice()));
         }

         first.setNumber(serverIds.size());
         first.setTotalPrice(totalPrice);
         first.setDiscountPrice(discountPrice);
         first.setPayPrice(payPrice);
         first.setCouponPrice(BigDecimal.ZERO);
         first.setItems(items);
         if (serverIds.size() == 1) {
            first.setTips("服务器续费 " + duration + " 个月");
         } else {
            first.setTips("批量续费 " + serverIds.size() + " 台 × " + duration + " 个月");
         }

         return CommonResult.data(first);
      } else {
         throw new CommonException("续费时长必须大于0", new Object[0]);
      }
   }

   private IndependentCalculatePriceResponse calculateRenewOne(String serverId, Integer duration) {
      BaseIndependent server = this.requireServer(serverId);
      if (server.getRenewPrice() == null) {
         throw new CommonException("服务器续费价格不存在", new Object[0]);
      } else {
         BaseAdminMachine machine = this.requireMachine(server);
         IndependentCalculatePriceResponse response = new IndependentCalculatePriceResponse();
         response.setBillingMode(1);
         response.setMachineId(server.getMachineId());
         response.setMachineName(server.getMachineName());
         response.setMachineMonthPrice(machine.getMachineMonthPrice());
         response.setBandwidth(server.getBandwidth());
         response.setBandwidthFreeSize(machine.getBandwidthFreeSize());
         response.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
         response.setDefense(server.getDefense());
         response.setDefenseFreeSize(machine.getDefenseFreeSize());
         response.setDefenseMonthPrice(machine.getDefenseMonthPrice());
         response.setIpSize(server.getIpSize());
         response.setIpFreeSize(machine.getIpFreeSize());
         response.setIpMonthPrice(machine.getIpMonthPrice());
         BigDecimal monthlyRenewPrice = server.getRenewPrice();
         response.setUnit("m");
         response.setDuration(duration);
         response.setNumber(1);
         response.setUnitPrice(monthlyRenewPrice);
         response.setTotalPrice(monthlyRenewPrice.multiply(BigDecimal.valueOf((long)duration.intValue())));
         if (StrUtil.isNotBlank(server.getLineId())) {
            this.applyLineDiscount(response, server.getMemberNo(), server.getLineId());
         } else {
            response.setPayPrice(response.getTotalPrice());
         }

         response.setCouponPrice(BigDecimal.ZERO);
         response.setRenewPrice(monthlyRenewPrice);
         response.setTips("服务器续费 " + duration + " 个月");
         return response;
      }
   }

   private IndependentRenewItemResponse toRenewItem(String serverId, IndependentCalculatePriceResponse one) {
      BaseIndependent server = this.requireServer(serverId);
      IndependentRenewItemResponse item = new IndependentRenewItemResponse();
      item.setServerId(serverId);
      item.setServerName(server.getServerName());
      item.setLineId(server.getLineId());
      item.setEndTime(server.getEndTime());
      item.setRenewPrice(one.getRenewPrice());
      item.setTotalPrice(one.getTotalPrice());
      item.setPayPrice(one.getPayPrice());
      return item;
   }

   private BigDecimal nz(BigDecimal value) {
      return value == null ? BigDecimal.ZERO : value;
   }

   private BaseIndependent requireServer(String serverId) {
      BaseIndependent server = this.independentApi.getById(serverId);
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         return server;
      }
   }

   private Long requireMemberNo(BaseIndependent server) {
      if (server.getMemberNo() == null) {
         throw new CommonException("原服务器订单会员不存在", new Object[0]);
      } else {
         return server.getMemberNo();
      }
   }

   private BaseAdminMachine requireMachine(BaseIndependent server) {
      if (StrUtil.isBlank(server.getMachineId())) {
         throw new CommonException("服务器未关联机型", new Object[0]);
      } else {
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(server.getMachineId());
         if (machine == null) {
            throw new CommonException("机型不存在", new Object[0]);
         } else {
            return machine;
         }
      }
   }

   private void applyLineDiscount(IndependentCalculatePriceResponse response, Long memberNo, String lineId) {
      BaseIndependentLine line = this.independentLineApi.selectByLineId(lineId);
      if (line == null) {
         throw new CommonException("线路不存在", new Object[0]);
      } else {
         BigDecimal payPrice = response.getTotalPrice();
         if (StrUtil.isNotBlank(line.getDiscountTemplateId()) && memberNo != null) {
            BaseMember member = this.memberApi.detail(memberNo);
            if (member == null) {
               throw new CommonException("会员不存在", new Object[0]);
            }

            BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
            if (discount == null) {
               log.warn("未查询到会员组折扣，按原价，memberNo={}", memberNo);
               discount = BigDecimal.ONE;
            }

            payPrice = response.getTotalPrice().multiply(discount);
            response.setDiscountPrice(response.getTotalPrice().subtract(payPrice));
         }

         response.setPayPrice(payPrice);
      }
   }

   private IndependentCalculatePriceResponse convertFromConfig(IndependentOrderServerConfig config) {
      IndependentCalculatePriceResponse response = new IndependentCalculatePriceResponse();
      response.setBillingMode(1);
      response.setMachineId(config.getMachineId());
      response.setMachineName(config.getMachineName());
      response.setMachineMonthPrice(config.getMachineMonthPrice());
      response.setBandwidth(config.getBandwidth());
      response.setBandwidthFreeSize(config.getBandwidthFreeSize());
      response.setBandwidthMonthPrice(config.getBandwidthMonthPrice());
      response.setDefense(config.getDefense());
      response.setDefenseFreeSize(config.getDefenseFreeSize());
      response.setDefenseMonthPrice(config.getDefenseMonthPrice());
      response.setIpSize(config.getIpSize());
      response.setIpFreeSize(config.getIpFreeSize());
      response.setIpMonthPrice(config.getIpMonthPrice());
      response.setUnit(config.getUnit());
      response.setDuration(config.getDuration());
      response.setNumber(config.getNumber());
      response.setUnitPrice(config.getUnitPrice());
      response.setTotalPrice(config.getTotalPrice());
      response.setPayPrice(config.getPayPrice());
      response.setRenewPrice(config.getRenewPrice());
      return response;
   }

   private IndependentCalculatePriceResponse convertFromUpgradePricing(IndependentPoolUpgradePricing pricing, String tips) {
      IndependentCalculatePriceResponse response = this.convertFromConfig(pricing.getOrderConfig());
      response.setBillingMode(pricing.getBillingMode());
      response.setUnit(pricing.getUnit());
      response.setDuration(pricing.getDurationDays());
      response.setNumber(1);
      response.setUnitPrice(pricing.getUnitPrice());
      response.setTotalPrice(pricing.getTotalPrice());
      response.setPayPrice(pricing.getPayPrice());
      response.setRenewPrice(pricing.getRenewPrice());
      response.setOriginalOrderNo(pricing.getOriginalOrderNo());
      response.setCouponPrice(BigDecimal.ZERO);
      response.setTips(tips);
      return response;
   }
}
