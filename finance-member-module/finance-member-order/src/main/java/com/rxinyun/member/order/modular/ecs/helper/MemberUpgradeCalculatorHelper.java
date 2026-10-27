package com.rxinyun.member.order.modular.ecs.helper;

import cn.hutool.json.JSONUtil;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.DateUtils;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.ecs.param.MemberBandwidthUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberCoreUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDefenseUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.param.MemberDiskUpgradeOrderParam;
import com.rxinyun.member.order.modular.ecs.response.MemberCalculatePriceResponse;
import com.rxinyun.production.api.MemberLineSkuApi;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.core.pojo.BaseMemberLineSku;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberUpgradeCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(MemberUpgradeCalculatorHelper.class);
   @Resource
   private MemberLineSkuApi memberLineSkuApi;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberServerApi memberServerApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;

   public MemberCalculatePriceResponse upgradeCoreCalculatePrice(MemberCoreUpgradeOrderParam coreUpgradeOrderParam) {
      MemberCalculatePriceResponse MemberCalculatePriceResponse = new MemberCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberServer memberServer = this.memberServerApi.selectByServerId(coreUpgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(memberServer.getLineId());
         log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else if (line.getUpgradeStatus() == 0) {
            throw new CommonException("该线路关闭升级中", new Object[0]);
         } else {
            log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
            BaseMemberLineSku lineSku = this.memberLineSkuApi
               .selectByLineSku(memberServer.getLineId(), coreUpgradeOrderParam.getCpu(), coreUpgradeOrderParam.getRam());
            log.info("查询到的实例规格为: {}", JSONUtil.toJsonStr(lineSku));
            if (lineSku == null) {
               throw new CommonException("该规格的实例不存在", new Object[0]);
            } else {
               BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(memberServer.getLineId(), memberServer.getCpu(), memberServer.getRam());
               if (currentLineSku == null) {
                  throw new CommonException("当前规格的实例不存在", new Object[0]);
               } else {
                  Integer billingMode = 1;
                  MemberCalculatePriceResponse.setBillingMode(billingMode);
                  MemberCalculatePriceResponse.setUnit("d");
                  MemberCalculatePriceResponse.setDuration(betweenDays);
                  MemberCalculatePriceResponse.setNumber(1);
                  MemberCalculatePriceResponse.setCpu(coreUpgradeOrderParam.getCpu());
                  MemberCalculatePriceResponse.setRam(coreUpgradeOrderParam.getRam());
                  MemberCalculatePriceResponse.setCoreDayPrice(lineSku.getCoreDayPrice());
                  MemberCalculatePriceResponse.setCoreMonthPrice(lineSku.getCoreMonthPrice());
                  MemberCalculatePriceResponse.setSystemDisk(memberServer.getSystemDisk());
                  MemberCalculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                  MemberCalculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                  MemberCalculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                  MemberCalculatePriceResponse.setBandwidth(memberServer.getBandwidth());
                  MemberCalculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                  MemberCalculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                  MemberCalculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                  MemberCalculatePriceResponse.setIpNum(memberServer.getIpNum());
                  MemberCalculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                  MemberCalculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                  MemberCalculatePriceResponse.setDataDisks(memberServer.getDataDisks());
                  MemberCalculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                  MemberCalculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                  MemberCalculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                  MemberCalculatePriceResponse.setDefense(memberServer.getDefense());
                  MemberCalculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                  MemberCalculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                  MemberCalculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                  BigDecimal coreDayPrice = lineSku.getCoreDayPrice().subtract(currentLineSku.getCoreDayPrice());
                  if (coreDayPrice.compareTo(BigDecimal.ZERO) < 0) {
                     throw new CommonException("新规格价格不能低于当前规格价格", new Object[0]);
                  } else {
                     BigDecimal corePrice = coreDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                     log.info("核心差价: {}", corePrice);
                     MemberCalculatePriceResponse.setUnitPrice(coreDayPrice);
                     MemberCalculatePriceResponse.setTotalPrice(corePrice);
                     BigDecimal payPrice = MemberCalculatePriceResponse.getTotalPrice();
                     if (line.getDiscountTemplateId() != null) {
                        BigDecimal discount = this.memberDiscountApi.queryDiscount(clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                        payPrice = MemberCalculatePriceResponse.getTotalPrice().multiply(discount);
                        MemberCalculatePriceResponse.setDiscountPrice(MemberCalculatePriceResponse.getTotalPrice().subtract(payPrice));
                     }

                     MemberCalculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                     MemberCalculatePriceResponse.setPayPrice(payPrice);
                     MemberCalculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(MemberCalculatePriceResponse));
                     MemberCalculatePriceResponse.setTips("核心升级差价");
                     return MemberCalculatePriceResponse;
                  }
               }
            }
         }
      }
   }

   public MemberCalculatePriceResponse upgradeBandwidthCalculatePrice(MemberBandwidthUpgradeOrderParam bandwidthUpgradeOrderParam) {
      MemberCalculatePriceResponse MemberCalculatePriceResponse = new MemberCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberServer memberServer = this.memberServerApi.selectByServerId(bandwidthUpgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(memberServer.getLineId());
         log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else if (line.getUpgradeStatus() == 0) {
            throw new CommonException("该线路关闭升级中", new Object[0]);
         } else {
            log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
            if (bandwidthUpgradeOrderParam.getBandwidth() <= memberServer.getBandwidth()) {
               throw new CommonException("新带宽大小必须大于原带宽大小", new Object[0]);
            } else {
               Integer billingMode = 1;
               MemberCalculatePriceResponse.setBillingMode(billingMode);
               MemberCalculatePriceResponse.setUnit("d");
               MemberCalculatePriceResponse.setDuration(betweenDays);
               MemberCalculatePriceResponse.setNumber(1);
               MemberCalculatePriceResponse.setBandwidth(bandwidthUpgradeOrderParam.getBandwidth());
               MemberCalculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
               MemberCalculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
               MemberCalculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
               MemberCalculatePriceResponse.setCpu(memberServer.getCpu());
               MemberCalculatePriceResponse.setRam(memberServer.getRam());
               BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(memberServer.getLineId(), memberServer.getCpu(), memberServer.getRam());
               if (currentLineSku != null) {
                  MemberCalculatePriceResponse.setCoreDayPrice(currentLineSku.getCoreDayPrice());
                  MemberCalculatePriceResponse.setCoreMonthPrice(currentLineSku.getCoreMonthPrice());
               }

               MemberCalculatePriceResponse.setSystemDisk(memberServer.getSystemDisk());
               MemberCalculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
               MemberCalculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
               MemberCalculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
               MemberCalculatePriceResponse.setDataDisks(memberServer.getDataDisks());
               MemberCalculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
               MemberCalculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
               MemberCalculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
               MemberCalculatePriceResponse.setIpNum(memberServer.getIpNum());
               MemberCalculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
               MemberCalculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
               MemberCalculatePriceResponse.setDefense(memberServer.getDefense());
               MemberCalculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
               MemberCalculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
               MemberCalculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
               Integer bandwidthDifference = bandwidthUpgradeOrderParam.getBandwidth() - memberServer.getBandwidth();
               BigDecimal bandwidthDayPrice = BigDecimal.ZERO;
               BigDecimal bandwidthMonthPrice = BigDecimal.ZERO;
               BigDecimal bandwidthPrice = BigDecimal.ZERO;
               if (billingMode.equals(1)) {
                  bandwidthDayPrice = line.getBandwidthDayPrice().multiply(BigDecimal.valueOf((long)bandwidthDifference.intValue()));
                  bandwidthMonthPrice = line.getBandwidthPrice().multiply(BigDecimal.valueOf((long)bandwidthDifference.intValue()));
                  bandwidthPrice = bandwidthDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                  MemberCalculatePriceResponse.setUnitPrice(bandwidthDayPrice);
                  MemberCalculatePriceResponse.setTotalPrice(bandwidthPrice);
                  BigDecimal payPrice = MemberCalculatePriceResponse.getTotalPrice();
                  log.info("payPrice: {}", payPrice);
                  if (line.getDiscountTemplateId() != null) {
                     BigDecimal discount = this.memberDiscountApi.queryDiscount(clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                     log.info("折扣: {}", discount);
                     payPrice = MemberCalculatePriceResponse.getTotalPrice().multiply(discount);
                     log.info("折扣金额: {}", payPrice);
                     MemberCalculatePriceResponse.setDiscountPrice(MemberCalculatePriceResponse.getTotalPrice().subtract(payPrice));
                  }

                  MemberCalculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                  MemberCalculatePriceResponse.setPayPrice(payPrice);
                  MemberCalculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(MemberCalculatePriceResponse));
                  MemberCalculatePriceResponse.setTips("带宽升级差价");
                  return MemberCalculatePriceResponse;
               } else {
                  throw new CommonException("计费方式不存在", new Object[0]);
               }
            }
         }
      }
   }

   public MemberCalculatePriceResponse upgradeDiskCalculatePrice(MemberDiskUpgradeOrderParam diskUpgradeOrderParam) {
      MemberCalculatePriceResponse MemberCalculatePriceResponse = new MemberCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberServer memberServer = this.memberServerApi.selectByServerId(diskUpgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(memberServer.getLineId());
         log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else if (line.getUpgradeStatus() == 0) {
            throw new CommonException("该线路关闭升级中", new Object[0]);
         } else {
            log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
            boolean systemDiskChanged = diskUpgradeOrderParam.getSystemDisk() > memberServer.getSystemDisk();
            if (diskUpgradeOrderParam.getSystemDisk() < memberServer.getSystemDisk()) {
               throw new CommonException("新系统盘大小必须大于等于原系统盘大小", new Object[0]);
            } else {
               int oldDataDisksTotal = 0;
               if (memberServer.getDataDisks() != null) {
                  for (Integer dataDisk : memberServer.getDataDisks()) {
                     oldDataDisksTotal += dataDisk;
                  }
               }

               int newDataDisksTotal = 0;
               if (diskUpgradeOrderParam.getDataDisks() != null) {
                  for (Integer dataDisk : diskUpgradeOrderParam.getDataDisks()) {
                     newDataDisksTotal += dataDisk;
                  }
               }

               boolean dataDisksChanged = newDataDisksTotal > oldDataDisksTotal;
               if (newDataDisksTotal < oldDataDisksTotal) {
                  throw new CommonException("新数据盘总容量必须大于等于原数据盘总容量", new Object[0]);
               } else if (!systemDiskChanged && !dataDisksChanged) {
                  throw new CommonException("系统盘或数据盘至少有一项需要升级", new Object[0]);
               } else {
                  Integer billingMode = 1;
                  MemberCalculatePriceResponse.setBillingMode(billingMode);
                  MemberCalculatePriceResponse.setUnit("d");
                  MemberCalculatePriceResponse.setDuration(betweenDays);
                  MemberCalculatePriceResponse.setNumber(1);
                  if (systemDiskChanged) {
                     MemberCalculatePriceResponse.setSystemDisk(diskUpgradeOrderParam.getSystemDisk());
                     MemberCalculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                     MemberCalculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                     MemberCalculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                  } else {
                     MemberCalculatePriceResponse.setSystemDisk(memberServer.getSystemDisk());
                     MemberCalculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                     MemberCalculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                     MemberCalculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                  }

                  if (dataDisksChanged) {
                     MemberCalculatePriceResponse.setDataDisks(diskUpgradeOrderParam.getDataDisks());
                     MemberCalculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                     MemberCalculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                     MemberCalculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                  } else {
                     MemberCalculatePriceResponse.setDataDisks(memberServer.getDataDisks());
                     MemberCalculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                     MemberCalculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                     MemberCalculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                  }

                  MemberCalculatePriceResponse.setCpu(memberServer.getCpu());
                  MemberCalculatePriceResponse.setRam(memberServer.getRam());
                  BaseMemberLineSku currentLineSku = this.memberLineSkuApi
                     .selectByLineSku(memberServer.getLineId(), memberServer.getCpu(), memberServer.getRam());
                  if (currentLineSku != null) {
                     MemberCalculatePriceResponse.setCoreDayPrice(currentLineSku.getCoreDayPrice());
                     MemberCalculatePriceResponse.setCoreMonthPrice(currentLineSku.getCoreMonthPrice());
                  }

                  MemberCalculatePriceResponse.setBandwidth(memberServer.getBandwidth());
                  MemberCalculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                  MemberCalculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                  MemberCalculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                  MemberCalculatePriceResponse.setIpNum(memberServer.getIpNum());
                  MemberCalculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                  MemberCalculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                  MemberCalculatePriceResponse.setDefense(memberServer.getDefense());
                  MemberCalculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                  MemberCalculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                  MemberCalculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                  BigDecimal systemDiskPrice = BigDecimal.ZERO;
                  BigDecimal systemDiskDayPrice = BigDecimal.ZERO;
                  BigDecimal systemDiskMonthPrice = BigDecimal.ZERO;
                  StringBuilder tipsBuilder = new StringBuilder();
                  if (systemDiskChanged) {
                     Integer systemDiskDifference = diskUpgradeOrderParam.getSystemDisk() - memberServer.getSystemDisk();
                     if (!billingMode.equals(1)) {
                        throw new CommonException("计费方式不存在", new Object[0]);
                     }

                     systemDiskDayPrice = line.getSystemDiskDayPrice().multiply(BigDecimal.valueOf((long)systemDiskDifference.intValue()));
                     systemDiskMonthPrice = line.getSystemDiskMonthPrice().multiply(BigDecimal.valueOf((long)systemDiskDifference.intValue()));
                     systemDiskPrice = systemDiskDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                     tipsBuilder.append("系统盘升级：增加").append(systemDiskDifference).append("GB");
                  }

                  BigDecimal dataDiskPrice = BigDecimal.ZERO;
                  BigDecimal dataDiskDayPrice = BigDecimal.ZERO;
                  BigDecimal dataDiskMonthPrice = BigDecimal.ZERO;
                  if (dataDisksChanged) {
                     Integer dataDiskDifference = newDataDisksTotal - oldDataDisksTotal;
                     if (!billingMode.equals(1)) {
                        throw new CommonException("计费方式不存在", new Object[0]);
                     }

                     dataDiskDayPrice = line.getDataDiskDayPrice().multiply(BigDecimal.valueOf((long)dataDiskDifference.intValue()));
                     dataDiskMonthPrice = line.getDataDiskMonthPrice().multiply(BigDecimal.valueOf((long)dataDiskDifference.intValue()));
                     dataDiskPrice = dataDiskDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                     if (tipsBuilder.length() > 0) {
                        tipsBuilder.append("，");
                     }

                     tipsBuilder.append("数据盘升级：增加").append(dataDiskDifference).append("GB");
                  }

                  BigDecimal totalDayPrice = systemDiskDayPrice.add(dataDiskDayPrice);
                  BigDecimal totalPrice = systemDiskPrice.add(dataDiskPrice);
                  BigDecimal totalMonthPrice = systemDiskMonthPrice.add(dataDiskMonthPrice);
                  MemberCalculatePriceResponse.setUnitPrice(totalDayPrice);
                  MemberCalculatePriceResponse.setTotalPrice(totalPrice);
                  BigDecimal payPrice = MemberCalculatePriceResponse.getTotalPrice();
                  if (line.getDiscountTemplateId() != null) {
                     BigDecimal discount = this.memberDiscountApi.queryDiscount(clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                     if (discount == null) {
                        log.warn("未查询到用户组 {} 对应的折扣信息，使用默认折扣 1.0", clientLoginUser.getGroupId());
                        discount = BigDecimal.ONE;
                     }

                     payPrice = MemberCalculatePriceResponse.getTotalPrice().multiply(discount);
                     MemberCalculatePriceResponse.setDiscountPrice(MemberCalculatePriceResponse.getTotalPrice().subtract(payPrice));
                  }

                  MemberCalculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                  MemberCalculatePriceResponse.setPayPrice(payPrice);
                  MemberCalculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(MemberCalculatePriceResponse));
                  tipsBuilder.append("，剩余使用时间").append(betweenDays).append("天");
                  MemberCalculatePriceResponse.setTips(tipsBuilder.toString());
                  return MemberCalculatePriceResponse;
               }
            }
         }
      }
   }

   public MemberCalculatePriceResponse upgradeDefenseCalculatePrice(MemberDefenseUpgradeOrderParam defenseUpgradeOrderParam) {
      MemberCalculatePriceResponse MemberCalculatePriceResponse = new MemberCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberServer memberServer = this.memberServerApi.selectByServerId(defenseUpgradeOrderParam.getServerId());
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         Integer betweenDays = DateUtils.betweenDay(new Date(), memberServer.getEndTime(), false);
         log.info("剩余天数:{}", betweenDays);
         BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(memberServer.getLineId());
         log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
         if (line == null) {
            throw new CommonException("该线路不存在", new Object[0]);
         } else if (line.getUpgradeStatus() == 0) {
            throw new CommonException("该线路关闭升级中", new Object[0]);
         } else {
            log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
            if (defenseUpgradeOrderParam.getDefense() <= memberServer.getDefense()) {
               throw new CommonException("新防御大小必须大于原防御大小", new Object[0]);
            } else {
               Integer billingMode = 1;
               MemberCalculatePriceResponse.setBillingMode(billingMode);
               MemberCalculatePriceResponse.setUnit("d");
               MemberCalculatePriceResponse.setDuration(betweenDays);
               MemberCalculatePriceResponse.setNumber(1);
               MemberCalculatePriceResponse.setDefense(defenseUpgradeOrderParam.getDefense());
               MemberCalculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
               MemberCalculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
               MemberCalculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
               MemberCalculatePriceResponse.setCpu(memberServer.getCpu());
               MemberCalculatePriceResponse.setRam(memberServer.getRam());
               BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(memberServer.getLineId(), memberServer.getCpu(), memberServer.getRam());
               if (currentLineSku != null) {
                  MemberCalculatePriceResponse.setCoreDayPrice(currentLineSku.getCoreDayPrice());
                  MemberCalculatePriceResponse.setCoreMonthPrice(currentLineSku.getCoreMonthPrice());
               }

               MemberCalculatePriceResponse.setSystemDisk(memberServer.getSystemDisk());
               MemberCalculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
               MemberCalculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
               MemberCalculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
               MemberCalculatePriceResponse.setBandwidth(memberServer.getBandwidth());
               MemberCalculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
               MemberCalculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
               MemberCalculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
               MemberCalculatePriceResponse.setIpNum(memberServer.getIpNum());
               MemberCalculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
               MemberCalculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
               MemberCalculatePriceResponse.setDataDisks(memberServer.getDataDisks());
               MemberCalculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
               MemberCalculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
               MemberCalculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
               Integer defenseDifference = defenseUpgradeOrderParam.getDefense() - memberServer.getDefense();
               BigDecimal defenseDayPrice = BigDecimal.ZERO;
               BigDecimal defenseMonthPrice = BigDecimal.ZERO;
               BigDecimal defensePrice = BigDecimal.ZERO;
               if (billingMode.equals(1)) {
                  defenseDayPrice = line.getDefenseDayPrice().multiply(BigDecimal.valueOf((long)defenseDifference.intValue()));
                  defenseMonthPrice = line.getDefensePrice().multiply(BigDecimal.valueOf((long)defenseDifference.intValue()));
                  defensePrice = defenseDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                  MemberCalculatePriceResponse.setUnitPrice(defenseDayPrice);
                  MemberCalculatePriceResponse.setTotalPrice(defensePrice);
                  BigDecimal payPrice = MemberCalculatePriceResponse.getTotalPrice();
                  if (line.getDiscountTemplateId() != null) {
                     BigDecimal discount = this.memberDiscountApi.queryDiscount(clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                     if (discount == null) {
                        log.warn("未查询到用户组 {} 对应的折扣信息，使用默认折扣 1.0", clientLoginUser.getGroupId());
                        discount = BigDecimal.ONE;
                     }

                     payPrice = MemberCalculatePriceResponse.getTotalPrice().multiply(discount);
                     MemberCalculatePriceResponse.setDiscountPrice(MemberCalculatePriceResponse.getTotalPrice().subtract(payPrice));
                  }

                  MemberCalculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                  MemberCalculatePriceResponse.setPayPrice(payPrice);
                  MemberCalculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(MemberCalculatePriceResponse));
                  MemberCalculatePriceResponse.setTips("防御升级差价");
                  return MemberCalculatePriceResponse;
               } else {
                  throw new CommonException("计费方式不存在", new Object[0]);
               }
            }
         }
      }
   }

   private BigDecimal calculateCurrentConfigRenewPrice(MemberCalculatePriceResponse response) {
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
      List<Integer> disks = response.getDataDisks();
      int dataDiskTotal = 0;
      if (disks != null) {
         for (Integer d : disks) {
            if (d != null) {
               dataDiskTotal += d;
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
