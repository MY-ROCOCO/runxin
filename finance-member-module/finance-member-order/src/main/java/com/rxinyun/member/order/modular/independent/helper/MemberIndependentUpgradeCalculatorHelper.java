package com.rxinyun.member.order.modular.independent.helper;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.DateUtils;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentIpUpgradeOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberIndependentUpgradeCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentUpgradeCalculatorHelper.class);
   @Resource
   private MemberIndependentApi memberIndependentApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private IndependentLineApi independentLineApi;

   public IndependentCalculatePriceResponse upgradeIpCalculatePrice(MemberIndependentIpUpgradeOrderParam upgradeOrderParam) {
      IndependentCalculatePriceResponse calculatePriceResponse = new IndependentCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(upgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         calculatePriceResponse.setBillingMode(1);
         calculatePriceResponse.setUnit("d");
         calculatePriceResponse.setDuration(betweenDays);
         calculatePriceResponse.setNumber(1);
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServer.getMachineId());
         log.info("查询到的机型为: {}", JSONUtil.toJsonStr(machine));
         if (machine == null) {
            throw new CommonException("该机型不存在", new Object[0]);
         } else {
            BaseIndependentLine line = this.independentLineApi.selectByLineId(memberServer.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("线路不存在", new Object[0]);
            } else {
               calculatePriceResponse.setMachineId(machine.getId());
               calculatePriceResponse.setMachineName(machine.getName());
               calculatePriceResponse.setMachineMonthPrice(machine.getMachineMonthPrice());
               if (upgradeOrderParam.getIpSize() < machine.getIpFreeSize()) {
                  throw new CommonException("不能低于赠送IP数量", new Object[0]);
               } else {
                  calculatePriceResponse.setIpSize(upgradeOrderParam.getIpSize());
                  calculatePriceResponse.setIpFreeSize(machine.getIpFreeSize());
                  calculatePriceResponse.setIpMonthPrice(machine.getIpMonthPrice());
                  calculatePriceResponse.setBandwidth(memberServer.getBandwidth());
                  calculatePriceResponse.setBandwidthFreeSize(machine.getBandwidthFreeSize());
                  calculatePriceResponse.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
                  calculatePriceResponse.setDefense(memberServer.getDefense());
                  calculatePriceResponse.setDefenseFreeSize(machine.getDefenseFreeSize());
                  calculatePriceResponse.setDefenseMonthPrice(machine.getDefenseMonthPrice());
                  int currentIp = memberServer.getIpSize() == null ? 0 : memberServer.getIpSize();
                  int ipDiff = upgradeOrderParam.getIpSize() - currentIp;
                  if (ipDiff <= 0) {
                     throw new CommonException("新 IP 数量必须大于当前数量", new Object[0]);
                  } else {
                     BigDecimal ipDayPrice = machine.getIpDayPrice().multiply(BigDecimal.valueOf((long)ipDiff));
                     BigDecimal ipMonthPrice = machine.getIpMonthPrice().multiply(BigDecimal.valueOf((long)ipDiff));
                     BigDecimal ipPrice = ipDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                     log.info("IP差价: {}", ipDayPrice);
                     calculatePriceResponse.setUnitPrice(ipDayPrice);
                     calculatePriceResponse.setTotalPrice(ipPrice);
                     BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                     log.info("payPrice: {}", payPrice);
                     if (StrUtil.isNotBlank(line.getDiscountTemplateId())) {
                        payPrice = this.applyLineDiscount(calculatePriceResponse, clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                        log.info("折扣后实付金额: {}", payPrice);
                     }

                     calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                     calculatePriceResponse.setPayPrice(payPrice);
                     calculatePriceResponse.setRenewPrice(this.calculateMonthlyRenewPrice(calculatePriceResponse));
                     calculatePriceResponse.setTips("IP升级差价");
                     return calculatePriceResponse;
                  }
               }
            }
         }
      }
   }

   public IndependentCalculatePriceResponse upgradeBandwidthCalculatePrice(MemberIndependentBandwidthUpgradeOrderParam bandwidthUpgradeOrderParam) {
      IndependentCalculatePriceResponse calculatePriceResponse = new IndependentCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(bandwidthUpgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServer.getMachineId());
         log.info("查询到的机型为: {}", JSONUtil.toJsonStr(machine));
         if (machine == null) {
            throw new CommonException("该机型不存在", new Object[0]);
         } else {
            BaseIndependentLine line = this.independentLineApi.selectByLineId(memberServer.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("线路不存在", new Object[0]);
            } else {
               log.info("获取机型成功, ID: {}, 机型名称: {}", machine.getId(), machine.getName());
               int currentBwCheck = memberServer.getBandwidth() == null ? 0 : memberServer.getBandwidth();
               if (bandwidthUpgradeOrderParam.getBandwidth() <= currentBwCheck) {
                  throw new CommonException("新带宽大小必须大于原带宽大小", new Object[0]);
               } else {
                  calculatePriceResponse.setBillingMode(1);
                  calculatePriceResponse.setUnit("d");
                  calculatePriceResponse.setDuration(betweenDays);
                  calculatePriceResponse.setNumber(1);
                  calculatePriceResponse.setMachineId(machine.getId());
                  calculatePriceResponse.setMachineName(machine.getName());
                  calculatePriceResponse.setMachineMonthPrice(machine.getMachineMonthPrice());
                  calculatePriceResponse.setIpSize(memberServer.getIpSize());
                  calculatePriceResponse.setIpFreeSize(machine.getIpFreeSize());
                  calculatePriceResponse.setIpMonthPrice(machine.getIpMonthPrice());
                  if (bandwidthUpgradeOrderParam.getBandwidth() < machine.getBandwidthFreeSize()) {
                     throw new CommonException("不能低于赠送带宽", new Object[0]);
                  } else {
                     calculatePriceResponse.setBandwidth(bandwidthUpgradeOrderParam.getBandwidth());
                     calculatePriceResponse.setBandwidthFreeSize(machine.getBandwidthFreeSize());
                     calculatePriceResponse.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
                     calculatePriceResponse.setDefense(memberServer.getDefense());
                     calculatePriceResponse.setDefenseFreeSize(machine.getDefenseFreeSize());
                     calculatePriceResponse.setDefenseMonthPrice(machine.getDefenseMonthPrice());
                     int bandwidthDifference = bandwidthUpgradeOrderParam.getBandwidth() - currentBwCheck;
                     BigDecimal bandwidthDayPrice = BigDecimal.ZERO;
                     BigDecimal bandwidthMonthPrice = BigDecimal.ZERO;
                     BigDecimal bandwidthPrice = BigDecimal.ZERO;
                     if (calculatePriceResponse.getBillingMode().equals(1)) {
                        bandwidthDayPrice = machine.getBandwidthDayPrice().multiply(BigDecimal.valueOf((long)bandwidthDifference));
                        bandwidthMonthPrice = machine.getBandwidthMonthPrice().multiply(BigDecimal.valueOf((long)bandwidthDifference));
                        bandwidthPrice = bandwidthDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                        calculatePriceResponse.setUnitPrice(bandwidthDayPrice);
                        calculatePriceResponse.setTotalPrice(bandwidthPrice);
                        BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                        log.info("payPrice: {}", payPrice);
                        if (StrUtil.isNotBlank(line.getDiscountTemplateId())) {
                           payPrice = this.applyLineDiscount(calculatePriceResponse, clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                           log.info("折扣后实付金额: {}", payPrice);
                        }

                        calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                        calculatePriceResponse.setPayPrice(payPrice);
                        calculatePriceResponse.setRenewPrice(this.calculateMonthlyRenewPrice(calculatePriceResponse));
                        calculatePriceResponse.setTips("带宽升级差价");
                        return calculatePriceResponse;
                     } else {
                        throw new CommonException("计费方式不存在", new Object[0]);
                     }
                  }
               }
            }
         }
      }
   }

   public IndependentCalculatePriceResponse upgradeDefenseCalculatePrice(MemberIndependentDefenseUpgradeOrderParam defenseUpgradeOrderParam) {
      IndependentCalculatePriceResponse calculatePriceResponse = new IndependentCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(defenseUpgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         int currentDefense = memberServer.getDefense() == null ? 0 : memberServer.getDefense();
         if (defenseUpgradeOrderParam.getDefense() <= currentDefense) {
            throw new CommonException("新防御大小必须大于原防御大小", new Object[0]);
         } else {
            BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServer.getMachineId());
            log.info("查询到的机型为: {}", JSONUtil.toJsonStr(machine));
            if (machine == null) {
               throw new CommonException("该机型不存在", new Object[0]);
            } else {
               BaseIndependentLine line = this.independentLineApi.selectByLineId(memberServer.getLineId());
               log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
               if (line == null) {
                  throw new CommonException("线路不存在", new Object[0]);
               } else {
                  calculatePriceResponse.setBillingMode(1);
                  calculatePriceResponse.setUnit("d");
                  calculatePriceResponse.setDuration(betweenDays);
                  calculatePriceResponse.setNumber(1);
                  if (defenseUpgradeOrderParam.getDefense() < machine.getDefenseFreeSize()) {
                     throw new CommonException("不能低于赠送防御值", new Object[0]);
                  } else {
                     calculatePriceResponse.setDefense(defenseUpgradeOrderParam.getDefense());
                     calculatePriceResponse.setDefenseFreeSize(machine.getDefenseFreeSize());
                     calculatePriceResponse.setDefenseMonthPrice(machine.getDefenseMonthPrice());
                     calculatePriceResponse.setIpSize(memberServer.getIpSize());
                     calculatePriceResponse.setIpFreeSize(machine.getIpFreeSize());
                     calculatePriceResponse.setIpMonthPrice(machine.getIpMonthPrice());
                     calculatePriceResponse.setMachineId(machine.getId());
                     calculatePriceResponse.setMachineName(machine.getName());
                     calculatePriceResponse.setMachineMonthPrice(machine.getMachineMonthPrice());
                     calculatePriceResponse.setBandwidth(memberServer.getBandwidth());
                     calculatePriceResponse.setBandwidthFreeSize(machine.getBandwidthFreeSize());
                     calculatePriceResponse.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
                     int defenseDifference = defenseUpgradeOrderParam.getDefense() - currentDefense;
                     BigDecimal defenseDayPrice = BigDecimal.ZERO;
                     BigDecimal defenseMonthPrice = BigDecimal.ZERO;
                     BigDecimal defensePrice = BigDecimal.ZERO;
                     if (calculatePriceResponse.getBillingMode().equals(1)) {
                        defenseDayPrice = machine.getDefenseDayPrice().multiply(BigDecimal.valueOf((long)defenseDifference));
                        defenseMonthPrice = machine.getDefenseMonthPrice().multiply(BigDecimal.valueOf((long)defenseDifference));
                        defensePrice = defenseDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                        calculatePriceResponse.setUnitPrice(defenseDayPrice);
                        calculatePriceResponse.setTotalPrice(defensePrice);
                        BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                        if (StrUtil.isNotBlank(line.getDiscountTemplateId())) {
                           payPrice = this.applyLineDiscount(calculatePriceResponse, clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                        }

                        calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                        calculatePriceResponse.setPayPrice(payPrice);
                        calculatePriceResponse.setRenewPrice(this.calculateMonthlyRenewPrice(calculatePriceResponse));
                        calculatePriceResponse.setTips("防御升级差价");
                        return calculatePriceResponse;
                     } else {
                        throw new CommonException("计费方式不存在", new Object[0]);
                     }
                  }
               }
            }
         }
      }
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

   private BigDecimal applyLineDiscount(IndependentCalculatePriceResponse response, String groupId, String discountTemplateId) {
      BigDecimal discount = this.memberDiscountApi.queryDiscount(groupId, discountTemplateId);
      if (discount == null) {
         log.warn("未查询到用户组 {} 对应的独立服务器线路折扣，按原价计算", groupId);
         discount = BigDecimal.ONE;
      }

      BigDecimal payPrice = response.getTotalPrice().multiply(discount);
      response.setDiscountPrice(response.getTotalPrice().subtract(payPrice));
      return payPrice;
   }
}
