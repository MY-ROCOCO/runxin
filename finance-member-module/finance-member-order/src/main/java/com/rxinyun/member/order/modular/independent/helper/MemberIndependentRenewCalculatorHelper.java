package com.rxinyun.member.order.modular.independent.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.RenewServerIdSupport;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.order.modular.independent.param.MemberIndependentRenewOrderParam;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import com.rxinyun.member.order.modular.independent.response.IndependentRenewItemResponse;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemberIndependentRenewCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentRenewCalculatorHelper.class);
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private MemberIndependentApi memberIndependentApi;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;

   public IndependentCalculatePriceResponse renewCalculatePrice(MemberIndependentRenewOrderParam renewOrderParam) {
      List<String> serverIds = RenewServerIdSupport.resolve(renewOrderParam.getServerId(), renewOrderParam.getServerIds());
      Integer duration = renewOrderParam.getDuration();
      if (duration != null && duration > 0) {
         List<IndependentRenewItemResponse> items = new ArrayList<>();
         IndependentCalculatePriceResponse first = null;
         BigDecimal totalPrice = BigDecimal.ZERO;
         BigDecimal discountPrice = BigDecimal.ZERO;
         BigDecimal payPrice = BigDecimal.ZERO;

         for (String serverId : serverIds) {
            IndependentCalculatePriceResponse one = this.calculateOne(serverId, duration);
            if (first == null) {
               first = one;
            }

            items.add(this.toItem(serverId, one));
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

         return first;
      } else {
         throw new CommonException("续费时长必须大于0", new Object[0]);
      }
   }

   public String resolveCouponLineId(IndependentCalculatePriceResponse response, String couponCode) {
      return RenewServerIdSupport.requireUniqueCouponLineId(this.collectLineIds(response), couponCode);
   }

   public BigDecimal findItemPayPrice(IndependentCalculatePriceResponse response, String serverId) {
      if (response != null && response.getItems() != null) {
         for (IndependentRenewItemResponse item : response.getItems()) {
            if (serverId != null && serverId.equals(item.getServerId())) {
               return item.getPayPrice();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Set<String> collectLineIds(IndependentCalculatePriceResponse response) {
      Set<String> lineIds = new LinkedHashSet<>();
      if (response != null && response.getItems() != null) {
         for (IndependentRenewItemResponse item : response.getItems()) {
            if (StrUtil.isNotBlank(item.getLineId())) {
               lineIds.add(item.getLineId());
            }
         }
      }

      return lineIds;
   }

   private IndependentRenewItemResponse toItem(String serverId, IndependentCalculatePriceResponse one) {
      BaseMemberIndependentServer server = this.memberIndependentApi.selectByServerId(serverId);
      IndependentRenewItemResponse item = new IndependentRenewItemResponse();
      item.setServerId(serverId);
      item.setServerName(server == null ? null : server.getServerName());
      item.setLineId(server == null ? null : server.getLineId());
      item.setEndTime(server == null ? null : server.getEndTime());
      item.setRenewPrice(one.getRenewPrice());
      item.setTotalPrice(one.getTotalPrice());
      item.setPayPrice(one.getPayPrice());
      return item;
   }

   private IndependentCalculatePriceResponse calculateOne(String serverId, Integer duration) {
      IndependentCalculatePriceResponse calculatePriceResponse = new IndependentCalculatePriceResponse();
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      BaseMemberIndependentServer memberServer = this.memberIndependentApi.selectByServerId(serverId);
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServer.getMachineId());
         log.info("查询到的机型为: {}", JSONUtil.toJsonStr(machine));
         if (machine == null) {
            throw new CommonException("该机型不存在", new Object[0]);
         } else {
            BaseIndependentLine line = this.independentLineApi.selectByLineId(memberServer.getLineId());
            if (line == null) {
               throw new CommonException("线路不存在", new Object[0]);
            } else {
               calculatePriceResponse.setBillingMode(1);
               calculatePriceResponse.setUnit("m");
               calculatePriceResponse.setDuration(duration);
               calculatePriceResponse.setNumber(1);
               calculatePriceResponse.setMachineId(machine.getId());
               calculatePriceResponse.setMachineName(machine.getName());
               calculatePriceResponse.setMachineMonthPrice(machine.getMachineMonthPrice());
               calculatePriceResponse.setIpSize(memberServer.getIpSize());
               calculatePriceResponse.setIpFreeSize(machine.getIpFreeSize());
               calculatePriceResponse.setIpMonthPrice(machine.getIpMonthPrice());
               calculatePriceResponse.setBandwidth(memberServer.getBandwidth());
               calculatePriceResponse.setBandwidthFreeSize(machine.getBandwidthFreeSize());
               calculatePriceResponse.setBandwidthMonthPrice(machine.getBandwidthMonthPrice());
               calculatePriceResponse.setDefense(memberServer.getDefense());
               calculatePriceResponse.setDefenseFreeSize(machine.getDefenseFreeSize());
               calculatePriceResponse.setDefenseMonthPrice(machine.getDefenseMonthPrice());
               BigDecimal monthlyRenewPrice = memberServer.getRenewPrice();
               if (monthlyRenewPrice == null) {
                  throw new CommonException("服务器续费价格不存在", new Object[0]);
               } else {
                  BigDecimal renewPrice = monthlyRenewPrice.multiply(BigDecimal.valueOf((long)duration.intValue()));
                  calculatePriceResponse.setUnitPrice(monthlyRenewPrice);
                  calculatePriceResponse.setTotalPrice(renewPrice);
                  BigDecimal payPrice = calculatePriceResponse.getTotalPrice();
                  if (StrUtil.isNotBlank(line.getDiscountTemplateId())) {
                     BigDecimal discount = this.memberDiscountApi.queryDiscount(clientLoginUser.getGroupId(), line.getDiscountTemplateId());
                     if (discount == null) {
                        log.warn("未查询到用户组 {} 对应的独立服务器线路折扣，按原价计算", clientLoginUser.getGroupId());
                        discount = BigDecimal.ONE;
                     }

                     payPrice = calculatePriceResponse.getTotalPrice().multiply(discount);
                     calculatePriceResponse.setDiscountPrice(calculatePriceResponse.getTotalPrice().subtract(payPrice));
                  }

                  calculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
                  calculatePriceResponse.setPayPrice(payPrice);
                  calculatePriceResponse.setRenewPrice(monthlyRenewPrice);
                  calculatePriceResponse.setTips("服务器续费 " + duration + " 个月");
                  return calculatePriceResponse;
               }
            }
         }
      }
   }

   private BigDecimal nz(BigDecimal value) {
      return (BigDecimal)ObjectUtil.defaultIfNull(value, BigDecimal.ZERO);
   }
}
