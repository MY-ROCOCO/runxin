package com.rxinyun.admin.order.modular.ecs.helper;

import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.order.modular.ecs.param.BandwidthUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.CoreUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DefenseUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.param.DiskUpgradeOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.business.api.ServerApi;
import com.rxinyun.business.core.pojo.BaseServer;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.DateUtils;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
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
public class UpgradeCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(UpgradeCalculatorHelper.class);
   @Resource
   private MemberLineSkuApi memberLineSkuApi;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private ServerApi serverApi;
   @Resource
   private MemberApi memberApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;

   public CalculatePriceResponse upgradeCoreCalculatePrice(CoreUpgradeOrderParam coreUpgradeOrderParam) {
      CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
      BaseServer server = this.serverApi.detail(coreUpgradeOrderParam.getServerId());
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         BaseMember member = this.memberApi.detail(Long.valueOf(server.getMemberNo()));
         if (member == null) {
            throw new CommonException("会员不存在！", new Object[0]);
         } else {
            Integer betweenDays = DateUtils.betweenDay(new Date(), server.getEndTime(), false);
            log.info("剩余天数:{}", betweenDays);
            BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(server.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("该线路不存在", new Object[0]);
            } else if (line.getUpgradeStatus() == 0) {
               throw new CommonException("该线路关闭升级中", new Object[0]);
            } else {
               log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
               BaseMemberLineSku lineSku = this.memberLineSkuApi
                  .selectByLineSku(server.getLineId(), coreUpgradeOrderParam.getCpu(), coreUpgradeOrderParam.getRam());
               log.info("查询到的实例规格为: {}", JSONUtil.toJsonStr(lineSku));
               if (lineSku == null) {
                  throw new CommonException("该规格的实例不存在", new Object[0]);
               } else {
                  BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(server.getLineId(), server.getCpu(), server.getRam());
                  if (currentLineSku == null) {
                     throw new CommonException("当前规格的实例不存在", new Object[0]);
                  } else {
                     Integer billingMode = 1;
                     calculatePriceResponse.setBillingMode(billingMode);
                     calculatePriceResponse.setUnit("d");
                     calculatePriceResponse.setDuration(betweenDays);
                     calculatePriceResponse.setNumber(1);
                     calculatePriceResponse.setCpu(coreUpgradeOrderParam.getCpu());
                     calculatePriceResponse.setRam(coreUpgradeOrderParam.getRam());
                     calculatePriceResponse.setCoreDayPrice(lineSku.getCoreDayPrice());
                     calculatePriceResponse.setCoreMonthPrice(lineSku.getCoreMonthPrice());
                     calculatePriceResponse.setSystemDisk(server.getSystemDisk());
                     calculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                     calculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                     calculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                     calculatePriceResponse.setBandwidth(server.getBandwidth());
                     calculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                     calculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                     calculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                     calculatePriceResponse.setIpNum(server.getIpNum());
                     calculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                     calculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                     calculatePriceResponse.setDataDisks(server.getDataDisks());
                     calculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                     calculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                     calculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                     calculatePriceResponse.setDefense(server.getDefense());
                     calculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                     calculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                     calculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                     BigDecimal coreDayPrice = lineSku.getCoreDayPrice().subtract(currentLineSku.getCoreDayPrice());
                     if (coreDayPrice.compareTo(BigDecimal.ZERO) < 0) {
                        throw new CommonException("新规格价格不能低于当前规格价格", new Object[0]);
                     } else {
                        BigDecimal corePrice = coreDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                        log.info("核心差价: {}", corePrice);
                        calculatePriceResponse.setUnitPrice(coreDayPrice);
                        calculatePriceResponse.setTotalPrice(corePrice);
                        BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                        if (line.getDiscountTemplateId() != null) {
                           BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
                           payPrice = calculatePriceResponse.getTotalPrice().multiply(discount);
                           calculatePriceResponse.setDiscountPrice(calculatePriceResponse.getTotalPrice().subtract(payPrice));
                        }

                        calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                        calculatePriceResponse.setPayPrice(payPrice);
                        calculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(calculatePriceResponse));
                        calculatePriceResponse.setTips("核心升级差价");
                        return calculatePriceResponse;
                     }
                  }
               }
            }
         }
      }
   }

   public CalculatePriceResponse upgradeBandwidthCalculatePrice(BandwidthUpgradeOrderParam bandwidthUpgradeOrderParam) {
      CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
      BaseServer server = this.serverApi.detail(bandwidthUpgradeOrderParam.getServerId());
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         BaseMember member = this.memberApi.detail(Long.valueOf(server.getMemberNo()));
         if (member == null) {
            throw new CommonException("会员不存在！", new Object[0]);
         } else {
            Integer betweenDays = DateUtils.betweenDay(new Date(), server.getEndTime(), false);
            log.info("剩余天数:{}", betweenDays);
            BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(server.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("该线路不存在", new Object[0]);
            } else if (line.getUpgradeStatus() == 0) {
               throw new CommonException("该线路关闭升级中", new Object[0]);
            } else {
               log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
               if (bandwidthUpgradeOrderParam.getBandwidth() <= server.getBandwidth()) {
                  throw new CommonException("新带宽大小必须大于原带宽大小", new Object[0]);
               } else {
                  Integer billingMode = 1;
                  calculatePriceResponse.setBillingMode(billingMode);
                  calculatePriceResponse.setUnit("d");
                  calculatePriceResponse.setDuration(betweenDays);
                  calculatePriceResponse.setNumber(1);
                  calculatePriceResponse.setBandwidth(bandwidthUpgradeOrderParam.getBandwidth());
                  calculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                  calculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                  calculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                  calculatePriceResponse.setCpu(server.getCpu());
                  calculatePriceResponse.setRam(server.getRam());
                  BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(server.getLineId(), server.getCpu(), server.getRam());
                  if (currentLineSku != null) {
                     calculatePriceResponse.setCoreDayPrice(currentLineSku.getCoreDayPrice());
                     calculatePriceResponse.setCoreMonthPrice(currentLineSku.getCoreMonthPrice());
                  }

                  calculatePriceResponse.setSystemDisk(server.getSystemDisk());
                  calculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                  calculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                  calculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                  calculatePriceResponse.setDataDisks(server.getDataDisks());
                  calculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                  calculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                  calculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                  calculatePriceResponse.setIpNum(server.getIpNum());
                  calculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                  calculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                  calculatePriceResponse.setDefense(server.getDefense());
                  calculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                  calculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                  calculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                  Integer bandwidthDifference = bandwidthUpgradeOrderParam.getBandwidth() - server.getBandwidth();
                  BigDecimal bandwidthDayPrice = BigDecimal.ZERO;
                  BigDecimal bandwidthMonthPrice = BigDecimal.ZERO;
                  BigDecimal bandwidthPrice = BigDecimal.ZERO;
                  if (billingMode.equals(1)) {
                     bandwidthDayPrice = line.getBandwidthDayPrice().multiply(BigDecimal.valueOf((long)bandwidthDifference.intValue()));
                     bandwidthMonthPrice = line.getBandwidthPrice().multiply(BigDecimal.valueOf((long)bandwidthDifference.intValue()));
                     bandwidthPrice = bandwidthDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                     calculatePriceResponse.setUnitPrice(bandwidthDayPrice);
                     calculatePriceResponse.setTotalPrice(bandwidthPrice);
                     BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                     log.info("payPrice: {}", payPrice);
                     if (line.getDiscountTemplateId() != null) {
                        BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
                        log.info("折扣: {}", discount);
                        payPrice = calculatePriceResponse.getTotalPrice().multiply(discount);
                        log.info("折扣金额: {}", payPrice);
                        calculatePriceResponse.setDiscountPrice(calculatePriceResponse.getTotalPrice().subtract(payPrice));
                     }

                     calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                     calculatePriceResponse.setPayPrice(payPrice);
                     calculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(calculatePriceResponse));
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

   public CalculatePriceResponse upgradeDiskCalculatePrice(DiskUpgradeOrderParam diskUpgradeOrderParam) {
      CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
      BaseServer server = this.serverApi.detail(diskUpgradeOrderParam.getServerId());
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         BaseMember member = this.memberApi.detail(Long.valueOf(server.getMemberNo()));
         if (member == null) {
            throw new CommonException("会员不存在！", new Object[0]);
         } else {
            Integer betweenDays = DateUtils.betweenDay(new Date(), server.getEndTime(), false);
            log.info("剩余天数:{}", betweenDays);
            BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(server.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("该线路不存在", new Object[0]);
            } else if (line.getUpgradeStatus() == 0) {
               throw new CommonException("该线路关闭升级中", new Object[0]);
            } else {
               log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
               boolean systemDiskChanged = diskUpgradeOrderParam.getSystemDisk() > server.getSystemDisk();
               if (diskUpgradeOrderParam.getSystemDisk() < server.getSystemDisk()) {
                  throw new CommonException("新系统盘大小必须大于等于原系统盘大小", new Object[0]);
               } else {
                  int oldDataDisksTotal = 0;
                  if (server.getDataDisks() != null) {
                     for (Integer dataDisk : server.getDataDisks()) {
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
                     calculatePriceResponse.setBillingMode(billingMode);
                     calculatePriceResponse.setUnit("d");
                     calculatePriceResponse.setDuration(betweenDays);
                     calculatePriceResponse.setNumber(1);
                     if (systemDiskChanged) {
                        calculatePriceResponse.setSystemDisk(diskUpgradeOrderParam.getSystemDisk());
                        calculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                        calculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                        calculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                     } else {
                        calculatePriceResponse.setSystemDisk(server.getSystemDisk());
                        calculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                        calculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                        calculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                     }

                     if (dataDisksChanged) {
                        calculatePriceResponse.setDataDisks(diskUpgradeOrderParam.getDataDisks());
                        calculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                        calculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                        calculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                     } else {
                        calculatePriceResponse.setDataDisks(server.getDataDisks());
                        calculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                        calculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                        calculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                     }

                     calculatePriceResponse.setCpu(server.getCpu());
                     calculatePriceResponse.setRam(server.getRam());
                     BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(server.getLineId(), server.getCpu(), server.getRam());
                     if (currentLineSku != null) {
                        calculatePriceResponse.setCoreDayPrice(currentLineSku.getCoreDayPrice());
                        calculatePriceResponse.setCoreMonthPrice(currentLineSku.getCoreMonthPrice());
                     }

                     calculatePriceResponse.setBandwidth(server.getBandwidth());
                     calculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                     calculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                     calculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                     calculatePriceResponse.setIpNum(server.getIpNum());
                     calculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                     calculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                     calculatePriceResponse.setDefense(server.getDefense());
                     calculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                     calculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                     calculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                     BigDecimal systemDiskPrice = BigDecimal.ZERO;
                     BigDecimal systemDiskDayPrice = BigDecimal.ZERO;
                     BigDecimal systemDiskMonthPrice = BigDecimal.ZERO;
                     StringBuilder tipsBuilder = new StringBuilder();
                     if (systemDiskChanged) {
                        Integer systemDiskDifference = diskUpgradeOrderParam.getSystemDisk() - server.getSystemDisk();
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
                     calculatePriceResponse.setUnitPrice(totalDayPrice);
                     calculatePriceResponse.setTotalPrice(totalPrice);
                     BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                     if (line.getDiscountTemplateId() != null) {
                        BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
                        if (discount == null) {
                           log.warn("未查询到用户组 {} 对应的折扣信息，使用默认折扣 1.0", member.getGroupId());
                           discount = BigDecimal.ONE;
                        }

                        payPrice = calculatePriceResponse.getTotalPrice().multiply(discount);
                        calculatePriceResponse.setDiscountPrice(calculatePriceResponse.getTotalPrice().subtract(payPrice));
                     }

                     calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                     calculatePriceResponse.setPayPrice(payPrice);
                     calculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(calculatePriceResponse));
                     tipsBuilder.append("，剩余使用时间").append(betweenDays).append("天");
                     calculatePriceResponse.setTips(tipsBuilder.toString());
                     return calculatePriceResponse;
                  }
               }
            }
         }
      }
   }

   public CalculatePriceResponse upgradeDefenseCalculatePrice(DefenseUpgradeOrderParam defenseUpgradeOrderParam) {
      CalculatePriceResponse calculatePriceResponse = new CalculatePriceResponse();
      BaseServer server = this.serverApi.detail(defenseUpgradeOrderParam.getServerId());
      if (server == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         BaseMember member = this.memberApi.detail(Long.valueOf(server.getMemberNo()));
         if (member == null) {
            throw new CommonException("会员不存在！", new Object[0]);
         } else {
            Integer betweenDays = DateUtils.betweenDay(new Date(), server.getEndTime(), false);
            log.info("剩余天数:{}", betweenDays);
            BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(server.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("该线路不存在", new Object[0]);
            } else if (line.getUpgradeStatus() == 0) {
               throw new CommonException("该线路关闭升级中", new Object[0]);
            } else {
               log.info("获取线路成功, ID: {}, 线路名称: {}", line.getId(), line.getLineName());
               if (defenseUpgradeOrderParam.getDefense() <= server.getDefense()) {
                  throw new CommonException("新防御大小必须大于原防御大小", new Object[0]);
               } else {
                  Integer billingMode = 1;
                  calculatePriceResponse.setBillingMode(billingMode);
                  calculatePriceResponse.setUnit("d");
                  calculatePriceResponse.setDuration(betweenDays);
                  calculatePriceResponse.setNumber(1);
                  calculatePriceResponse.setDefense(defenseUpgradeOrderParam.getDefense());
                  calculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
                  calculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
                  calculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
                  calculatePriceResponse.setCpu(server.getCpu());
                  calculatePriceResponse.setRam(server.getRam());
                  BaseMemberLineSku currentLineSku = this.memberLineSkuApi.selectByLineSku(server.getLineId(), server.getCpu(), server.getRam());
                  if (currentLineSku != null) {
                     calculatePriceResponse.setCoreDayPrice(currentLineSku.getCoreDayPrice());
                     calculatePriceResponse.setCoreMonthPrice(currentLineSku.getCoreMonthPrice());
                  }

                  calculatePriceResponse.setSystemDisk(server.getSystemDisk());
                  calculatePriceResponse.setSystemDiskFreeSize(line.getSystemDiskFreeSize());
                  calculatePriceResponse.setSystemDiskDayPrice(line.getSystemDiskDayPrice());
                  calculatePriceResponse.setSystemDiskMonthPrice(line.getSystemDiskMonthPrice());
                  calculatePriceResponse.setBandwidth(server.getBandwidth());
                  calculatePriceResponse.setBandwidthFreeSize(line.getBandwidthFreeSize());
                  calculatePriceResponse.setBandwidthDayPrice(line.getBandwidthDayPrice());
                  calculatePriceResponse.setBandwidthMonthPrice(line.getBandwidthPrice());
                  calculatePriceResponse.setIpNum(server.getIpNum());
                  calculatePriceResponse.setIpFreeSize(line.getIpFreeSize());
                  calculatePriceResponse.setIpMonthPrice(line.getAppendIpPrice());
                  calculatePriceResponse.setDataDisks(server.getDataDisks());
                  calculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
                  calculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
                  calculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
                  Integer defenseDifference = defenseUpgradeOrderParam.getDefense() - server.getDefense();
                  BigDecimal defenseDayPrice = BigDecimal.ZERO;
                  BigDecimal defenseMonthPrice = BigDecimal.ZERO;
                  BigDecimal defensePrice = BigDecimal.ZERO;
                  if (billingMode.equals(1)) {
                     defenseDayPrice = line.getDefenseDayPrice().multiply(BigDecimal.valueOf((long)defenseDifference.intValue()));
                     defenseMonthPrice = line.getDefensePrice().multiply(BigDecimal.valueOf((long)defenseDifference.intValue()));
                     defensePrice = defenseDayPrice.multiply(BigDecimal.valueOf((long)betweenDays.intValue()));
                     calculatePriceResponse.setUnitPrice(defenseDayPrice);
                     calculatePriceResponse.setTotalPrice(defensePrice);
                     BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                     if (line.getDiscountTemplateId() != null) {
                        BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
                        if (discount == null) {
                           log.warn("未查询到用户组 {} 对应的折扣信息，使用默认折扣 1.0", member.getGroupId());
                           discount = BigDecimal.ONE;
                        }

                        payPrice = calculatePriceResponse.getTotalPrice().multiply(discount);
                        calculatePriceResponse.setDiscountPrice(calculatePriceResponse.getTotalPrice().subtract(payPrice));
                     }

                     calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                     calculatePriceResponse.setPayPrice(payPrice);
                     calculatePriceResponse.setRenewPrice(this.calculateCurrentConfigRenewPrice(calculatePriceResponse));
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

   private BigDecimal calculateCurrentConfigRenewPrice(CalculatePriceResponse response) {
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
