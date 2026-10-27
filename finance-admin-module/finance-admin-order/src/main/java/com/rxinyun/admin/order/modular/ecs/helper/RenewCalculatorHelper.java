package com.rxinyun.admin.order.modular.ecs.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.rxinyun.admin.order.modular.ecs.param.RenewOrderParam;
import com.rxinyun.admin.order.modular.ecs.response.CalculatePriceResponse;
import com.rxinyun.admin.order.modular.ecs.response.RenewItemResponse;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.RenewServerIdSupport;
import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import com.rxinyun.production.api.MemberLineSkuApi;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.core.pojo.BaseMemberLineSku;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RenewCalculatorHelper {
   private static final Logger log = LoggerFactory.getLogger(RenewCalculatorHelper.class);
   @Resource
   private MemberServerApi memberServerApi;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private MemberLineSkuApi memberLineSkuApi;
   @Resource
   private MemberDiscountDetailApi memberDiscountApi;
   @Resource
   private MemberApi memberApi;

   public CalculatePriceResponse renewCalculatePrice(RenewOrderParam renewOrderParam) {
      List<String> serverIds = RenewServerIdSupport.resolve(renewOrderParam.getServerId(), renewOrderParam.getServerIds());
      Integer duration = renewOrderParam.getDuration();
      if (duration != null && duration > 0) {
         List<RenewItemResponse> items = new ArrayList<>();
         CalculatePriceResponse first = null;
         BigDecimal totalPrice = BigDecimal.ZERO;
         BigDecimal discountPrice = BigDecimal.ZERO;
         BigDecimal payPrice = BigDecimal.ZERO;

         for (String serverId : serverIds) {
            CalculatePriceResponse one = this.calculateOne(serverId, duration);
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

   public BigDecimal findItemPayPrice(CalculatePriceResponse response, String serverId) {
      if (response != null && response.getItems() != null) {
         for (RenewItemResponse item : response.getItems()) {
            if (serverId != null && serverId.equals(item.getServerId())) {
               return item.getPayPrice();
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private RenewItemResponse toItem(String serverId, CalculatePriceResponse one) {
      BaseMemberServer server = this.memberServerApi.selectByServerId(serverId);
      RenewItemResponse item = new RenewItemResponse();
      item.setServerId(serverId);
      item.setServerName(server == null ? null : server.getHostName());
      item.setLineId(server == null ? null : server.getLineId());
      item.setEndTime(server == null ? null : server.getEndTime());
      item.setRenewPrice(one.getRenewPrice());
      item.setTotalPrice(one.getTotalPrice());
      item.setPayPrice(one.getPayPrice());
      return item;
   }

   private CalculatePriceResponse calculateOne(String serverId, Integer duration) {
      CalculatePriceResponse MemberCalculatePriceResponse = new CalculatePriceResponse();
      BaseMemberServer memberServer = this.memberServerApi.selectByServerId(serverId);
      if (memberServer == null) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else if (memberServer.getRenewPrice() == null) {
         throw new CommonException("服务器续费价格不存在", new Object[0]);
      } else if (memberServer.getMemberNo() == null) {
         throw new CommonException("服务器未关联会员，无法计算续费价格", new Object[0]);
      } else {
         BaseMember member = this.memberApi.detail(memberServer.getMemberNo());
         if (member == null) {
            throw new CommonException("会员不存在", new Object[0]);
         } else {
            BaseMemberLocalLine line = this.memberLocalLineApi.selectByLineId(memberServer.getLineId());
            log.info("查询到的线路为: {}", JSONUtil.toJsonStr(line));
            if (line == null) {
               throw new CommonException("该线路不存在", new Object[0]);
            } else if (line.getRenewStatus() != null && line.getRenewStatus() == 0) {
               throw new CommonException("该线路关闭续费中", new Object[0]);
            } else {
               MemberCalculatePriceResponse.setBillingMode(1);
               MemberCalculatePriceResponse.setUnit("m");
               MemberCalculatePriceResponse.setDuration(duration);
               MemberCalculatePriceResponse.setNumber(1);
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
               MemberCalculatePriceResponse.setDataDisks(memberServer.getDataDisks());
               MemberCalculatePriceResponse.setDataDisksFreeSize(line.getDataDiskFreeSize());
               MemberCalculatePriceResponse.setDataDisksDayPrice(line.getDataDiskDayPrice());
               MemberCalculatePriceResponse.setDataDisksMonthPrice(line.getDataDiskMonthPrice());
               MemberCalculatePriceResponse.setDefense(memberServer.getDefense());
               MemberCalculatePriceResponse.setDefenseFreeSize(line.getDefenseFreeSize());
               MemberCalculatePriceResponse.setDefenseDayPrice(line.getDefenseDayPrice());
               MemberCalculatePriceResponse.setDefenseMonthPrice(line.getDefensePrice());
               BigDecimal monthlyRenewPrice = memberServer.getRenewPrice();
               BigDecimal renewPrice = monthlyRenewPrice.multiply(BigDecimal.valueOf((long)duration.intValue()));
               MemberCalculatePriceResponse.setUnitPrice(monthlyRenewPrice);
               MemberCalculatePriceResponse.setTotalPrice(renewPrice);
               BigDecimal payPrice = renewPrice;
               if (line.getDiscountTemplateId() != null) {
                  BigDecimal discount = this.memberDiscountApi.queryDiscount(member.getGroupId(), line.getDiscountTemplateId());
                  if (discount == null) {
                     log.warn("未查询到用户组 {} 对应的折扣信息，使用默认折扣 1.0", member.getGroupId());
                     discount = BigDecimal.ONE;
                  }

                  payPrice = renewPrice.multiply(discount);
                  MemberCalculatePriceResponse.setDiscountPrice(renewPrice.subtract(payPrice));
               }

               MemberCalculatePriceResponse.setCouponPrice(BigDecimal.ZERO);
               MemberCalculatePriceResponse.setPayPrice(payPrice);
               MemberCalculatePriceResponse.setRenewPrice(monthlyRenewPrice);
               MemberCalculatePriceResponse.setTips("服务器续费 " + duration + " 个月");
               return MemberCalculatePriceResponse;
            }
         }
      }
   }

   private BigDecimal nz(BigDecimal value) {
      return (BigDecimal)ObjectUtil.defaultIfNull(value, BigDecimal.ZERO);
   }
}
