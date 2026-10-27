package com.rxinyun.admin.order.modular.api.helper;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.business.core.pojo.BaseIndependent;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.DateUtils;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
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
public class IndependentResourcePoolUpgradeCalculator {
   private static final Logger log = LoggerFactory.getLogger(IndependentResourcePoolUpgradeCalculator.class);
   @Resource
   private IndependentResourcePoolPriceCalculator independentResourcePoolPriceCalculator;
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private MemberApi memberApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;

   public IndependentPoolUpgradePricing computeBandwidthUpgrade(BaseIndependent server, int targetBandwidth, Long memberNo) {
      int current = nz(server.getBandwidth());
      if (targetBandwidth <= current) {
         throw new CommonException("新带宽必须大于当前带宽", new Object[0]);
      } else {
         BaseAdminMachine machine = this.requireMachine(server);
         BaseIndependentLine line = this.requireLine(server);
         if (machine.getUpgrades() != null && machine.getUpgrades() == 0) {
            throw new CommonException("该机型不支持升级", new Object[0]);
         } else {
            int bwFree = nz(machine.getBandwidthFreeSize());
            if (targetBandwidth < bwFree) {
               throw new CommonException("不能低于赠送带宽", new Object[0]);
            } else {
               Integer bwMin = machine.getBandwidthMinSize();
               Integer bwMax = machine.getBandwidthMaxSize();
               if (bwMin != null && targetBandwidth < bwMin) {
                  throw new CommonException("带宽不在机型允许范围内", new Object[0]);
               } else if (bwMax != null && targetBandwidth > bwMax) {
                  throw new CommonException("带宽不在机型允许范围内", new Object[0]);
               } else {
                  int diff = targetBandwidth - current;
                  BigDecimal dayUnit = nz(machine.getBandwidthDayPrice()).multiply(BigDecimal.valueOf((long)diff));
                  return this.buildPricing(server, memberNo, machine, line, dayUnit, targetBandwidth, nz(server.getDefense()), nz(server.getIpSize()), "带宽升级差价");
               }
            }
         }
      }
   }

   public IndependentPoolUpgradePricing computeDefenseUpgrade(BaseIndependent server, int targetDefense, Long memberNo) {
      int current = nz(server.getDefense());
      if (targetDefense <= current) {
         throw new CommonException("新防御必须大于当前防御", new Object[0]);
      } else {
         BaseAdminMachine machine = this.requireMachine(server);
         BaseIndependentLine line = this.requireLine(server);
         if (machine.getUpgrades() != null && machine.getUpgrades() == 0) {
            throw new CommonException("该机型不支持升级", new Object[0]);
         } else {
            int defFree = nz(machine.getDefenseFreeSize());
            if (targetDefense < defFree) {
               throw new CommonException("不能低于赠送防御值", new Object[0]);
            } else {
               int diff = targetDefense - current;
               BigDecimal dayUnit = nz(machine.getDefenseDayPrice()).multiply(BigDecimal.valueOf((long)diff));
               return this.buildPricing(server, memberNo, machine, line, dayUnit, nz(server.getBandwidth()), targetDefense, nz(server.getIpSize()), "防御升级差价");
            }
         }
      }
   }

   public IndependentPoolUpgradePricing computeIpUpgrade(BaseIndependent server, int targetIpSize, Long memberNo) {
      int current = nz(server.getIpSize());
      if (targetIpSize <= current) {
         throw new CommonException("新 IP 数量必须大于当前数量", new Object[0]);
      } else {
         BaseAdminMachine machine = this.requireMachine(server);
         BaseIndependentLine line = this.requireLine(server);
         if (machine.getUpgrades() != null && machine.getUpgrades() == 0) {
            throw new CommonException("该机型不支持升级", new Object[0]);
         } else {
            int ipFree = nz(machine.getIpFreeSize());
            if (targetIpSize < ipFree) {
               throw new CommonException("不能低于赠送 IP 数量", new Object[0]);
            } else {
               Integer ipMin = machine.getIpMinSize();
               Integer ipMax = machine.getIpMaxSize();
               if (ipMin != null && targetIpSize < ipMin) {
                  throw new CommonException("IP 数量不在机型允许范围内", new Object[0]);
               } else if (ipMax != null && targetIpSize > ipMax) {
                  throw new CommonException("IP 数量不在机型允许范围内", new Object[0]);
               } else {
                  int diff = targetIpSize - current;
                  BigDecimal dayUnit = nz(machine.getIpDayPrice()).multiply(BigDecimal.valueOf((long)diff));
                  return this.buildPricing(
                     server, memberNo, machine, line, dayUnit, nz(server.getBandwidth()), nz(server.getDefense()), targetIpSize, "IP 升级差价"
                  );
               }
            }
         }
      }
   }

   private IndependentPoolUpgradePricing buildPricing(
      BaseIndependent server,
      Long memberNo,
      BaseAdminMachine machine,
      BaseIndependentLine line,
      BigDecimal dayUnit,
      int newBandwidth,
      int newDefense,
      int newIpSize,
      String tips
   ) {
      int billingMode = 1;
      if (billingMode != 1) {
         throw new CommonException("当前仅支持包年包月计费下的升级", new Object[0]);
      } else {
         int betweenDays = DateUtils.betweenDay(new Date(), server.getEndTime(), false);
         BigDecimal total = dayUnit.multiply(BigDecimal.valueOf((long)betweenDays));
         BigDecimal payPrice = this.applyLineDiscount(total, memberNo, line);
         BigDecimal renewPrice = this.independentResourcePoolPriceCalculator.monthlyRenewPriceForMachineConfig(machine, newBandwidth, newDefense, newIpSize);
         IndependentPoolUpgradePricing p = new IndependentPoolUpgradePricing();
         p.setPayPrice(payPrice);
         p.setRenewPrice(renewPrice);
         p.setTotalPrice(total);
         p.setUnitPrice(dayUnit);
         p.setDurationDays(betweenDays);
         p.setUnit("d");
         p.setBillingMode(billingMode);
         IndependentOrderServerConfig cfg = new IndependentOrderServerConfig();
         cfg.setRegionId(server.getRegionId());
         cfg.setLineId(server.getLineId());
         cfg.setMachineId(machine.getId());
         cfg.setMachineName(machine.getName());
         cfg.setMachineMonthPrice(machine.getMachineMonthPrice());
         cfg.setType(machine.getType());
         cfg.setAutomation(machine.getAutomation());
         cfg.setCpuInfo(server.getCpuInfo());
         cfg.setRamInfo(server.getRamInfo());
         cfg.setDisksInfo(server.getDisksInfo());
         cfg.setSystemClassId(server.getSystemClassId());
         cfg.setSystemId(server.getSystemId());
         cfg.setLoginPassword(server.getSystemPassword());
         cfg.setBandwidth(newBandwidth);
         cfg.setBandwidthFreeSize(machine.getBandwidthFreeSize());
         cfg.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
         cfg.setDefense(newDefense);
         cfg.setDefenseFreeSize(machine.getDefenseFreeSize());
         cfg.setDefenseMonthPrice(machine.getDefenseMonthPrice());
         cfg.setIpSize(newIpSize);
         cfg.setIpFreeSize(machine.getIpFreeSize());
         cfg.setIpMonthPrice(machine.getIpMonthPrice());
         cfg.setUpgrades(machine.getUpgrades());
         cfg.setUnit("d");
         cfg.setDuration(betweenDays);
         cfg.setUnitPrice(dayUnit);
         cfg.setNumber(1);
         cfg.setTotalPrice(total);
         cfg.setPayPrice(payPrice);
         cfg.setRenewPrice(renewPrice);
         cfg.setRemark(tips);
         p.setOrderConfig(cfg);
         return p;
      }
   }

   private BigDecimal applyLineDiscount(BigDecimal total, Long memberNo, BaseIndependentLine line) {
      if (line.getDiscountTemplateId() != null && !StrUtil.isBlank(line.getDiscountTemplateId())) {
         BaseMember member = this.memberApi.detail(memberNo);
         if (member == null) {
            throw new CommonException("会员不存在", new Object[0]);
         } else {
            BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
            if (discount == null) {
               log.warn("未查询到会员组折扣，按原价，groupId={}", member.getGroupId());
               discount = BigDecimal.ONE;
            }

            return total.multiply(discount);
         }
      } else {
         return total;
      }
   }

   private BaseAdminMachine requireMachine(BaseIndependent server) {
      if (server.getMachineId() == null) {
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

   private BaseIndependentLine requireLine(BaseIndependent server) {
      if (server.getLineId() == null) {
         throw new CommonException("服务器未关联线路", new Object[0]);
      } else {
         BaseIndependentLine line = this.independentLineApi.selectByLineId(server.getLineId());
         if (line == null) {
            throw new CommonException("线路不存在", new Object[0]);
         } else {
            return line;
         }
      }
   }

   private static int nz(Integer v) {
      return v == null ? 0 : v;
   }

   private static BigDecimal nz(BigDecimal v) {
      return v == null ? BigDecimal.ZERO : v;
   }
}
