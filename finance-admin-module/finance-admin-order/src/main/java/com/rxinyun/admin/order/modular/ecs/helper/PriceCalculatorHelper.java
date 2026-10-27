package com.rxinyun.admin.order.modular.ecs.helper;

import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.order.modular.ecs.param.CalculatePriceParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import com.rxinyun.production.api.MemberLineSkuApi;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.core.pojo.BaseMemberLineSku;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PriceCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(PriceCalculatorHelper.class);
   @Resource
   private MemberLineSkuApi memberLineSkuApi;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;
   @Resource
   private MemberApi memberApi;

   public CalculatePriceResponse calculatePrice(CalculatePriceParam calculatePriceParam) {
      CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
      calculatePriceResponse.setBillingMode(calculatePriceParam.getBillingMode());
      calculatePriceResponse.setUnit(calculatePriceParam.getUnit());
      calculatePriceResponse.setDefense(calculatePriceParam.getDefense());
      calculatePriceResponse.setDuration(calculatePriceParam.getDuration());
      calculatePriceResponse.setNumber(calculatePriceParam.getNumber());
      calculatePriceResponse.setIpNum(calculatePriceParam.getIpNum());
      if (calculatePriceParam.getIpNum() != null && calculatePriceParam.getIpNum() >= 1) {
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(calculatePriceParam.getLineId());
         log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else if (line.getStatus() != null && line.getStatus() == 0) {
            throw new CommonException("该线路关闭销售中", new Object[0]);
         } else {
            log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
            BaseMemberLineSku lineSku = this.memberLineSkuApi
               .selectByLineSku(calculatePriceParam.getLineId(), calculatePriceParam.getCpu(), calculatePriceParam.getRam());
            log.info("查询到的实例规格为: {}", JSONUtil.toJsonStr(lineSku));
            if (lineSku == null) {
               throw new CommonException("该规格的实例不存在", new Object[0]);
            } else {
               calculatePriceResponse.setCpu(lineSku.getCpu());
               calculatePriceResponse.setRam(lineSku.getRam());
               calculatePriceResponse.setCoreDayPrice(lineSku.getCoreDayPrice());
               calculatePriceResponse.setCoreMonthPrice(lineSku.getCoreMonthPrice());
               BigDecimal coreTotalPrice = calculateCorePrice(calculatePriceResponse);
               calculatePriceResponse.setSystemDisk(calculatePriceParam.getSystemDisk());
               calculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
               calculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
               calculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
               if (calculatePriceResponse.getSystemDisk() < line.getSystemDiskMinSize()) {
                  throw new CommonException("系统盘不能低于最小限制: " + line.getSystemDiskMinSize(), new Object[0]);
               } else if (calculatePriceResponse.getSystemDisk() > line.getSystemDiskMaxSize()) {
                  throw new CommonException("系统盘不能高于最大限制: " + line.getSystemDiskMaxSize(), new Object[0]);
               } else {
                  BigDecimal systemDiskTotalPrice = this.calculateSystemDiskPrice(calculatePriceResponse);
                  if (calculatePriceParam.getSystemDisk() < line.getSystemDiskFreeSize()) {
                     throw new CommonException("不能低于赠送系统盘", new Object[0]);
                  } else if (calculatePriceParam.getBandwidth() < line.getBandwidthFreeSize()) {
                     throw new CommonException("不能低于赠送带宽", new Object[0]);
                  } else if (calculatePriceParam.getBandwidth() < line.getBandwidthMinSize()) {
                     throw new CommonException("带宽不能低于最小限制: " + line.getBandwidthMinSize(), new Object[0]);
                  } else if (calculatePriceParam.getBandwidth() > line.getBandwidthMaxSize()) {
                     throw new CommonException("带宽不能高于最大限制: " + line.getBandwidthMaxSize(), new Object[0]);
                  } else {
                     calculatePriceResponse.setBandwidth(calculatePriceParam.getBandwidth());
                     calculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                     calculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                     calculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                     BigDecimal bandwidthTotalPrice = this.calculateBandwidthPrice(calculatePriceResponse);
                     if (calculatePriceParam.getIpNum() < line.getIpFreeSize()) {
                        throw new CommonException("不能低于赠送IP数量", new Object[0]);
                     } else if (calculatePriceParam.getIpNum() < line.getIpMinSize()) {
                        throw new CommonException("IP数量不能低于最小限制: " + line.getIpMinSize(), new Object[0]);
                     } else if (calculatePriceParam.getIpNum() > line.getIpMaxSize()) {
                        throw new CommonException("IP数量不能高于最大限制: " + line.getIpMaxSize(), new Object[0]);
                     } else {
                        calculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                        calculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                        BigDecimal ipTotalPrice = this.calculateIpPrice(calculatePriceResponse);
                        Integer dataDiskCount = 0;

                        for (Integer dataDisk : calculatePriceParam.getDataDisks()) {
                           if (dataDisk < line.getDataDiskMinSize()) {
                              throw new CommonException("数据盘不能低于最小限制: " + line.getDataDiskMinSize(), new Object[0]);
                           }

                           if (dataDisk > line.getDataDiskMaxSize()) {
                              throw new CommonException("数据盘不能高于最大限制: " + line.getDataDiskMaxSize(), new Object[0]);
                           }

                           dataDiskCount = dataDiskCount + dataDisk;
                        }

                        if (dataDiskCount < line.getDataDiskFreeSize()) {
                           throw new CommonException("不能低于赠送带宽", new Object[0]);
                        } else {
                           calculatePriceResponse.setDataDisks(calculatePriceParam.getDataDisks());
                           calculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                           calculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                           calculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                           BigDecimal dataDiskTotalPrice = this.calculateDataDiskPrice(calculatePriceResponse);
                           if (calculatePriceParam.getDefense() < line.getDefenseFreeSize()) {
                              throw new CommonException("不能低于赠送防御值", new Object[0]);
                           } else {
                              calculatePriceResponse.setDefense(calculatePriceParam.getDefense());
                              calculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                              calculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                              calculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                              BigDecimal defenseTotalPrice = this.calculateDefensePrice(calculatePriceResponse);
                              calculatePriceResponse.setUnitPrice(
                                 coreTotalPrice.add(systemDiskTotalPrice)
                                    .add(bandwidthTotalPrice)
                                    .add(ipTotalPrice)
                                    .add(dataDiskTotalPrice)
                                    .add(defenseTotalPrice)
                              );
                              calculatePriceResponse.setTotalPrice(
                                 calculatePriceResponse.getUnitPrice().multiply(BigDecimal.valueOf((long)calculatePriceParam.getNumber().intValue()))
                              );
                              calculatePriceResponse.setRenewPrice(this.calculateMonthlyRenewPrice(calculatePriceResponse));
                              BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                              if (line.getDiscountTemplateId() != null && calculatePriceParam.getMemberNo() != null) {
                                 BaseMember member = this.memberApi.detail(calculatePriceParam.getMemberNo());
                                 if (member == null) {
                                    throw new CommonException("会员不存在", new Object[0]);
                                 }

                                 BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
                                 if (discount == null) {
                                    log.warn("未查询到用户组 {} 对应的折扣信息，使用默认折扣 1.0", member.getGroupId());
                                    discount = BigDecimal.ONE;
                                 }

                                 payPrice = calculatePriceResponse.getTotalPrice().multiply(discount);
                                 calculatePriceResponse.setDiscountPrice(calculatePriceResponse.getTotalPrice().subtract(payPrice));
                              }

                              calculatePriceResponse.setPayPrice(payPrice);
                              return calculatePriceResponse;
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         throw new CommonException("IP数量必须大于0", new Object[0]);
      }
   }

   public static BigDecimal calculateCorePrice(CalculatePriceResponse MemberCalculatePriceResponse) {
      if (MemberCalculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyCorePrice(MemberCalculatePriceResponse.getCoreMonthPrice(), MemberCalculatePriceResponse.getDuration());
      } else if (MemberCalculatePriceResponse.getBillingMode().equals(2)) {
         return calculateHourlyCorePrice(MemberCalculatePriceResponse.getCoreDayPrice());
      } else {
         throw new CommonException("不支持的计费方式: " + MemberCalculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateHourlyCorePrice(BigDecimal price) {
      return price.divide(BigDecimal.valueOf(24L), 6, RoundingMode.HALF_UP);
   }

   private static BigDecimal calculateMonthlyCorePrice(BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateSystemDiskPrice(CalculatePriceResponse MemberCalculatePriceResponse) {
      Integer systemDisk = MemberCalculatePriceResponse.getSystemDisk() - MemberCalculatePriceResponse.getSystemDiskFreeSize();
      if (MemberCalculatePriceResponse.getBillingMode().equals(2)) {
         return calculateHourlySystemDiskPrice(systemDisk, MemberCalculatePriceResponse.getSystemDiskDayPrice());
      } else if (MemberCalculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlySystemDiskPrice(systemDisk, MemberCalculatePriceResponse.getSystemDiskMonthPrice(), MemberCalculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + MemberCalculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateHourlySystemDiskPrice(Integer systemDisk, BigDecimal price) {
      BigDecimal coreHourlyPrice = price.divide(BigDecimal.valueOf(24L), 6, RoundingMode.HALF_UP);
      return coreHourlyPrice.multiply(BigDecimal.valueOf((long)systemDisk.intValue()));
   }

   private static BigDecimal calculateMonthlySystemDiskPrice(Integer systemDisk, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)systemDisk.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateDataDiskPrice(CalculatePriceResponse MemberCalculatePriceResponse) {
      Integer dataDiskCount = 0;

      for (Integer dataDisk : MemberCalculatePriceResponse.getDataDisks()) {
         dataDiskCount = dataDiskCount + dataDisk;
      }

      Integer dataDisk = dataDiskCount - MemberCalculatePriceResponse.getDataDisksFreeSize();
      if (MemberCalculatePriceResponse.getBillingMode().equals(2)) {
         return calculateHourlyDataDiskPrice(dataDisk, MemberCalculatePriceResponse.getDataDisksDayPrice());
      } else if (MemberCalculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyDataDiskPrice(dataDisk, MemberCalculatePriceResponse.getDataDisksMonthPrice(), MemberCalculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + MemberCalculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateHourlyDataDiskPrice(Integer dataDisk, BigDecimal price) {
      BigDecimal coreHourlyPrice = price.divide(BigDecimal.valueOf(24L), 6, RoundingMode.HALF_UP);
      return coreHourlyPrice.multiply(BigDecimal.valueOf((long)dataDisk.intValue()));
   }

   private static BigDecimal calculateMonthlyDataDiskPrice(Integer dataDisk, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)dataDisk.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateBandwidthPrice(CalculatePriceResponse MemberCalculatePriceResponse) {
      Integer bandwidth = MemberCalculatePriceResponse.getBandwidth() - MemberCalculatePriceResponse.getBandwidthFreeSize();
      if (MemberCalculatePriceResponse.getBillingMode().equals(2)) {
         return calculateHourlyBandwidthPrice(bandwidth, MemberCalculatePriceResponse.getBandwidthDayPrice());
      } else if (MemberCalculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyBandwidthPrice(bandwidth, MemberCalculatePriceResponse.getBandwidthMonthPrice(), MemberCalculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + MemberCalculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private BigDecimal calculateIpPrice(CalculatePriceResponse response) {
      int ipCount = response.getIpNum() - response.getIpFreeSize();
      if (ipCount <= 0) {
         return BigDecimal.ZERO;
      } else if (response.getIpMonthPrice() == null) {
         return BigDecimal.ZERO;
      } else if (response.getBillingMode().equals(2)) {
         BigDecimal hourlyPrice = response.getIpMonthPrice()
            .divide(BigDecimal.valueOf(30L), 6, RoundingMode.HALF_UP)
            .divide(BigDecimal.valueOf(24L), 6, RoundingMode.HALF_UP);
         return hourlyPrice.multiply(BigDecimal.valueOf((long)ipCount));
      } else if (response.getBillingMode().equals(1)) {
         return response.getIpMonthPrice().multiply(BigDecimal.valueOf((long)ipCount)).multiply(BigDecimal.valueOf((long)response.getDuration().intValue()));
      } else {
         throw new CommonException("不支持的计费方式: " + response.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateHourlyBandwidthPrice(Integer bandwidth, BigDecimal price) {
      BigDecimal bandwidHourlyPrice = price.divide(BigDecimal.valueOf(24L), 6, RoundingMode.HALF_UP);
      return bandwidHourlyPrice.multiply(BigDecimal.valueOf((long)bandwidth.intValue()));
   }

   private static BigDecimal calculateMonthlyBandwidthPrice(Integer bandwidth, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)bandwidth.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateDefensePrice(CalculatePriceResponse MemberCalculatePriceResponse) {
      Integer defense = MemberCalculatePriceResponse.getDefense() - MemberCalculatePriceResponse.getDefenseFreeSize();
      if (MemberCalculatePriceResponse.getBillingMode().equals(2)) {
         return calculateHourlyDefensePrice(defense, MemberCalculatePriceResponse.getDefenseDayPrice());
      } else if (MemberCalculatePriceResponse.getBillingMode().equals(1)) {
         return calculateMonthlyDefensePrice(defense, MemberCalculatePriceResponse.getDefenseMonthPrice(), MemberCalculatePriceResponse.getDuration());
      } else {
         throw new CommonException("不支持的计费方式: " + MemberCalculatePriceResponse.getBillingMode(), new Object[0]);
      }
   }

   private static BigDecimal calculateHourlyDefensePrice(Integer defense, BigDecimal price) {
      BigDecimal bandwidHourlyPrice = price.divide(BigDecimal.valueOf(24L), 6, RoundingMode.HALF_UP);
      return bandwidHourlyPrice.multiply(BigDecimal.valueOf((long)defense.intValue()));
   }

   private static BigDecimal calculateMonthlyDefensePrice(Integer defense, BigDecimal price, Integer duration) {
      return price.multiply(BigDecimal.valueOf((long)defense.intValue())).multiply(BigDecimal.valueOf((long)duration.intValue()));
   }

   private BigDecimal calculateMonthlyRenewPrice(CalculatePriceResponse response) {
      BigDecimal core = response.getCoreMonthPrice() == null ? BigDecimal.ZERO : response.getCoreMonthPrice();
      int systemPaid = Math.max(
         0,
         (response.getSystemDisk() == null ? 0 : response.getSystemDisk()) - (response.getSystemDiskFreeSize() == null ? 0 : response.getSystemDiskFreeSize())
      );
      BigDecimal system = (response.getSystemDiskMonthPrice() == null ? BigDecimal.ZERO : response.getSystemDiskMonthPrice())
         .multiply(BigDecimal.valueOf((long)systemPaid));
      int bandwidthPaid = Math.max(
         0, (response.getBandwidth() == null ? 0 : response.getBandwidth()) - (response.getBandwidthFreeSize() == null ? 0 : response.getBandwidthFreeSize())
      );
      BigDecimal bandwidth = (response.getBandwidthMonthPrice() == null ? BigDecimal.ZERO : response.getBandwidthMonthPrice())
         .multiply(BigDecimal.valueOf((long)bandwidthPaid));
      int ipPaid = Math.max(0, (response.getIpNum() == null ? 0 : response.getIpNum()) - (response.getIpFreeSize() == null ? 0 : response.getIpFreeSize()));
      BigDecimal ip = (response.getIpMonthPrice() == null ? BigDecimal.ZERO : response.getIpMonthPrice()).multiply(BigDecimal.valueOf((long)ipPaid));
      int dataDiskTotal = 0;
      if (response.getDataDisks() != null) {
         for (Integer disk : response.getDataDisks()) {
            if (disk != null) {
               dataDiskTotal += disk;
            }
         }
      }

      int dataDiskPaid = Math.max(0, dataDiskTotal - (response.getDataDisksFreeSize() == null ? 0 : response.getDataDisksFreeSize()));
      BigDecimal dataDisk = (response.getDataDisksMonthPrice() == null ? BigDecimal.ZERO : response.getDataDisksMonthPrice())
         .multiply(BigDecimal.valueOf((long)dataDiskPaid));
      int defensePaid = Math.max(
         0, (response.getDefense() == null ? 0 : response.getDefense()) - (response.getDefenseFreeSize() == null ? 0 : response.getDefenseFreeSize())
      );
      BigDecimal defense = (response.getDefenseMonthPrice() == null ? BigDecimal.ZERO : response.getDefenseMonthPrice())
         .multiply(BigDecimal.valueOf((long)defensePaid));
      return core.add(system).add(bandwidth).add(ip).add(dataDisk).add(defense);
   }
}
