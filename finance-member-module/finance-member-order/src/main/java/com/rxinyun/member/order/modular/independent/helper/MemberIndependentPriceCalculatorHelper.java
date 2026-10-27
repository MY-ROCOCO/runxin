package com.rxinyun.member.order.modular.independent.helper;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentCalculatePriceParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberIndependentPriceCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentPriceCalculatorHelper.class);
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;

   public IndependentCalculatePriceResponse calculatePrice(MemberIndependentCalculatePriceParam calculatePriceParam) {
      IndependentCalculatePriceResponse calculatePriceResponse = new IndependentCalculatePriceResponse();
      calculatePriceResponse.setBillingMode(calculatePriceParam.getBillingMode());
      calculatePriceResponse.setUnit(calculatePriceParam.getUnit());
      calculatePriceResponse.setDefense(calculatePriceParam.getDefense());
      calculatePriceResponse.setDuration(calculatePriceParam.getDuration());
      calculatePriceResponse.setNumber(calculatePriceParam.getNumber());
      BaseAdminMachine machine = this.adminMachineApi.findMachineById(calculatePriceParam.getMachineId());
      log.info("查询到的机型为: {}", JSONUtil.toJsonStr(machine));
      if (machine == null) {
         throw new CommonException("该机型不存在", new Object[0]);
      } else {
         log.info("获取机型成功, ID: {}, 机型名称: {}", machine.getId(), machine.getName());
         BaseIndependentLine line = this.independentLineApi.selectByLineId(calculatePriceParam.getLineId());
         log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
         if (line == null) {
            throw new CommonException("线路不存在", new Object[0]);
         } else {
            calculatePriceResponse.setMachineId(machine.getId());
            calculatePriceResponse.setMachineName(machine.getName());
            calculatePriceResponse.setMachineMonthPrice(machine.getMachineMonthPrice());
            BigDecimal coreTotalPrice = calculateMachinePrice(calculatePriceResponse);
            if (calculatePriceParam.getBandwidth() < machine.getBandwidthFreeSize()) {
               throw new CommonException("不能低于赠送带宽", new Object[0]);
            } else if (calculatePriceParam.getBandwidth() < machine.getBandwidthMinSize()) {
               throw new CommonException("带宽不能低于最小限制: " + machine.getBandwidthMinSize(), new Object[0]);
            } else if (calculatePriceParam.getBandwidth() > machine.getBandwidthMaxSize()) {
               throw new CommonException("带宽不能高于最大限制: " + machine.getBandwidthMaxSize(), new Object[0]);
            } else {
               calculatePriceResponse.setBandwidth(calculatePriceParam.getBandwidth());
               calculatePriceResponse.setBandwidthFreeSize(machine.getBandwidthFreeSize());
               calculatePriceResponse.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
               BigDecimal bandwidthTotalPrice = this.calculateBandwidthPrice(calculatePriceResponse);
               if (calculatePriceParam.getDefense() < machine.getDefenseFreeSize()) {
                  throw new CommonException("不能低于赠送防御值", new Object[0]);
               } else {
                  calculatePriceResponse.setDefense(calculatePriceParam.getDefense());
                  calculatePriceResponse.setDefenseFreeSize(machine.getDefenseFreeSize());
                  calculatePriceResponse.setDefenseMonthPrice(machine.getDefenseMonthPrice());
                  BigDecimal defenseTotalPrice = this.calculateDefensePrice(calculatePriceResponse);
                  if (calculatePriceParam.getIpSize() < machine.getIpFreeSize()) {
                     throw new CommonException("不能低于赠送IP数量", new Object[0]);
                  } else {
                     calculatePriceResponse.setIpSize(calculatePriceParam.getIpSize());
                     calculatePriceResponse.setIpFreeSize(machine.getIpFreeSize());
                     calculatePriceResponse.setIpMonthPrice(machine.getIpMonthPrice());
                     BigDecimal ipTotalPrice = this.calculateIpPrice(calculatePriceResponse);
                     calculatePriceResponse.setUnitPrice(coreTotalPrice.add(bandwidthTotalPrice).add(defenseTotalPrice).add(ipTotalPrice));
                     calculatePriceResponse.setTotalPrice(
                        calculatePriceResponse.getUnitPrice().multiply(BigDecimal.valueOf((long)calculatePriceParam.getNumber().intValue()))
                     );
                     calculatePriceResponse.setRenewPrice(this.calculateMonthlyRenewPrice(calculatePriceResponse));
                     boolean isLogin = StpClientUtil.isLogin();
                     log.info("当前是否登录: {}", isLogin);
                     if (isLogin && StrUtil.isNotBlank(line.getDiscountTemplateId())) {
                        StpUtil.isLogin();
                        SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
                        log.info("当前登录用户: {}", clientLoginUser);
                        this.applyLineDiscount(calculatePriceResponse, clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                     }

                     if (calculatePriceResponse.getPayPrice() == null) {
                        calculatePriceResponse.setPayPrice(calculatePriceResponse.getTotalPrice());
                     }

                     return calculatePriceResponse;
                  }
               }
            }
         }
      }
   }

   public static BigDecimal calculateMachinePrice(IndependentCalculatePriceResponse calculatePriceResponse) {
      if (calculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyCorePrice(calculatePriceResponse.getMachineMonthPrice(), calculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + calculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateMonthlyCorePrice(BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateBandwidthPrice(IndependentCalculatePriceResponse calculatePriceResponse) {
      Integer bandwidth = calculatePriceResponse.getBandwidth() - calculatePriceResponse.getBandwidthFreeSize();
      if (calculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyBandwidthPrice(bandwidth, calculatePriceResponse.getBandwidthMonthPrice(), calculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + calculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateMonthlyBandwidthPrice(Integer bandwidth, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)bandwidth.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateDefensePrice(IndependentCalculatePriceResponse calculatePriceResponse) {
      Integer defense = calculatePriceResponse.getDefense() - calculatePriceResponse.getDefenseFreeSize();
      if (calculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyDefensePrice(defense, calculatePriceResponse.getDefenseMonthPrice(), calculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + calculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateMonthlyDefensePrice(Integer defense, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)defense.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateIpPrice(IndependentCalculatePriceResponse calculatePriceResponse) {
      Integer ipSize = calculatePriceResponse.getIpSize() - calculatePriceResponse.getIpFreeSize();
      if (calculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyIpPrice(ipSize, calculatePriceResponse.getIpMonthPrice(), calculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + calculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateMonthlyIpPrice(Integer ipSize, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)ipSize.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateMonthlyRenewPrice(IndependentCalculatePriceResponse response) {
      BigDecimal machine = response.getMachineMonthPrice() == null ? BigDecimal.ZERO : response.getMachineMonthPrice();
      int bandwidthPaid = Math.max(
         0, (response.getBandwidth() == null ? 0 : response.getBandwidth()) - (response.getBandwidthFreeSize() == null ? 0 : response.getBandwidthFreeSize())
      );
      BigDecimal bandwidth = (response.getBandwidthMonthPrice() == null ? BigDecimal.ZERO : response.getBandwidthMonthPrice())
         .multiply(BigDecimal.valueOf((long)bandwidthPaid));
      int defensePaid = Math.max(
         0, (response.getDefense() == null ? 0 : response.getDefense()) - (response.getDefenseFreeSize() == null ? 0 : response.getDefenseFreeSize())
      );
      BigDecimal defense = (response.getDefenseMonthPrice() == null ? BigDecimal.ZERO : response.getDefenseMonthPrice())
         .multiply(BigDecimal.valueOf((long)defensePaid));
      int ipPaid = Math.max(0, (response.getIpSize() == null ? 0 : response.getIpSize()) - (response.getIpFreeSize() == null ? 0 : response.getIpFreeSize()));
      BigDecimal ip = (response.getIpMonthPrice() == null ? BigDecimal.ZERO : response.getIpMonthPrice()).multiply(BigDecimal.valueOf((long)ipPaid));
      return machine.add(bandwidth).add(defense).add(ip);
   }

   private void applyLineDiscount(IndependentCalculatePriceResponse response, String groupId, String discountTemplateId) {
      BigDecimal discount = this.memberDiscountApi.queryDiscount(groupId, discountTemplateId);
      if (discount == null) {
         log.warn("未查询到用户组 {} 对应的独立服务器线路折扣，按原价计算", groupId);
         discount = BigDecimal.ONE;
      }

      BigDecimal payPrice = response.getTotalPrice().multiply(discount);
      response.setDiscountPrice(response.getTotalPrice().subtract(payPrice));
      response.setPayPrice(payPrice);
   }
}
