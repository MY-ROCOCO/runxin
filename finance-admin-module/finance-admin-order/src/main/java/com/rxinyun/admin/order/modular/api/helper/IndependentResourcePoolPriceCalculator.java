package com.rxinyun.admin.order.modular.api.helper;

import com.rxinyun.admin.order.modular.ecs.entity.IndependentOrderServerConfig;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class IndependentResourcePoolPriceCalculator {
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private IndependentLineApi independentLineApi;

   public void fillNewPurchasePricing(
      IndependentOrderServerConfig cfg,
      int billingMode,
      String unit,
      String machineId,
      String lineId,
      int bandwidth,
      int defense,
      int ipSize,
      int duration,
      int number
   ) {
      if (billingMode != 1) {
         throw new CommonException("当前仅支持包年包月计费", new Object[0]);
      } else {
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(machineId);
         if (machine == null) {
            throw new CommonException("该机型不存在", new Object[0]);
         } else {
            BaseIndependentLine line = this.independentLineApi.selectByLineId(lineId);
            if (line == null) {
               throw new CommonException("线路不存在", new Object[0]);
            } else {
               int bwFree = machine.getBandwidthFreeSize() == null ? 0 : machine.getBandwidthFreeSize();
               int defFree = machine.getDefenseFreeSize() == null ? 0 : machine.getDefenseFreeSize();
               int ipFree = machine.getIpFreeSize() == null ? 0 : machine.getIpFreeSize();
               if (bandwidth == 0) {
                  bandwidth = bwFree;
               }

               if (defense == 0) {
                  defense = defFree;
               }

               if (ipSize == 0) {
                  ipSize = ipFree;
               }

               if (bandwidth < bwFree) {
                  throw new CommonException("不能低于赠送带宽", new Object[0]);
               } else {
                  Integer bwMin = machine.getBandwidthMinSize();
                  Integer bwMax = machine.getBandwidthMaxSize();
                  if (bwMin != null && bandwidth < bwMin) {
                     throw new CommonException("带宽不在机型允许范围内", new Object[0]);
                  } else if (bwMax != null && bandwidth > bwMax) {
                     throw new CommonException("带宽不在机型允许范围内", new Object[0]);
                  } else if (defense < defFree) {
                     throw new CommonException("不能低于赠送防御值", new Object[0]);
                  } else if (ipSize < ipFree) {
                     throw new CommonException("不能低于赠送IP数量", new Object[0]);
                  } else {
                     cfg.setMachineId(machine.getId());
                     cfg.setMachineName(machine.getName());
                     cfg.setMachineMonthPrice(machine.getMachineMonthPrice());
                     cfg.setType(machine.getType());
                     cfg.setAutomation(machine.getAutomation());
                     cfg.setCpuInfo(machine.getCpuInfo());
                     cfg.setRamInfo(machine.getRamInfo());
                     cfg.setDisksInfo(machine.getDisksInfo());
                     cfg.setBandwidth(bandwidth);
                     cfg.setBandwidthFreeSize(machine.getBandwidthFreeSize());
                     cfg.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
                     cfg.setDefense(defense);
                     cfg.setDefenseFreeSize(machine.getDefenseFreeSize());
                     cfg.setDefenseMonthPrice(machine.getDefenseMonthPrice());
                     cfg.setIpSize(ipSize);
                     cfg.setIpFreeSize(machine.getIpFreeSize());
                     cfg.setIpMonthPrice(machine.getIpMonthPrice());
                     cfg.setUpgrades(machine.getUpgrades());
                     cfg.setUnit(unit);
                     cfg.setDuration(duration);
                     cfg.setNumber(number);
                     BigDecimal core = machine.getMachineMonthPrice().multiply(BigDecimal.valueOf((long)duration));
                     int bwBill = bandwidth - machine.getBandwidthFreeSize();
                     BigDecimal bw = machine.getBandwidthMonthPrice().multiply(BigDecimal.valueOf((long)bwBill)).multiply(BigDecimal.valueOf((long)duration));
                     int defBill = defense - machine.getDefenseFreeSize();
                     BigDecimal def = machine.getDefenseMonthPrice().multiply(BigDecimal.valueOf((long)defBill)).multiply(BigDecimal.valueOf((long)duration));
                     int ipBill = ipSize - machine.getIpFreeSize();
                     BigDecimal ip = machine.getIpMonthPrice().multiply(BigDecimal.valueOf((long)ipBill)).multiply(BigDecimal.valueOf((long)duration));
                     BigDecimal unitPrice = core.add(bw).add(def).add(ip);
                     cfg.setUnitPrice(unitPrice);
                     cfg.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf((long)number)));
                     cfg.setPayPrice(cfg.getTotalPrice());
                     cfg.setRenewPrice(this.monthlyRenewPriceForMachineConfig(machine, bandwidth, defense, ipSize));
                  }
               }
            }
         }
      }
   }

   public BigDecimal monthlyRenewPriceForMachineConfig(BaseAdminMachine machine, int bandwidth, int defense, int ipSize) {
      if (machine == null) {
         throw new CommonException("机型不存在", new Object[0]);
      } else {
         int bwFree = machine.getBandwidthFreeSize() == null ? 0 : machine.getBandwidthFreeSize();
         int defFree = machine.getDefenseFreeSize() == null ? 0 : machine.getDefenseFreeSize();
         int ipFree = machine.getIpFreeSize() == null ? 0 : machine.getIpFreeSize();
         BigDecimal core = nz(machine.getMachineMonthPrice());
         int bwBill = Math.max(0, bandwidth - bwFree);
         BigDecimal bw = nz(machine.getBandwidthMonthPrice()).multiply(BigDecimal.valueOf((long)bwBill));
         int defBill = Math.max(0, defense - defFree);
         BigDecimal def = nz(machine.getDefenseMonthPrice()).multiply(BigDecimal.valueOf((long)defBill));
         int ipBill = Math.max(0, ipSize - ipFree);
         BigDecimal ip = nz(machine.getIpMonthPrice()).multiply(BigDecimal.valueOf((long)ipBill));
         return core.add(bw).add(def).add(ip);
      }
   }

   private static BigDecimal nz(BigDecimal v) {
      return v == null ? BigDecimal.ZERO : v;
   }
}
