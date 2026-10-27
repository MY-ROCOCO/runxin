package com.rxinyun.member.order.modular.coupon;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.marketing.api.MemberCouponApi;
import com.rxinyun.marketing.api.dto.CouponCalculateRequest;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.marketing.api.dto.CouponLockRequest;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.mapper.MemberOrderMapper;
import com.rxinyun.member.order.modular.ecs.response.MemberCalculatePriceResponse;
import com.rxinyun.member.order.modular.independent.response.IndependentCalculatePriceResponse;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OrderCouponSupportService {
   @Resource
   private MemberCouponApi memberCouponApi;
   @Resource
   private MemberOrderMapper memberOrderMapper;

   public void applyCoupon(MemberCalculatePriceResponse response, Long memberNo, Integer productType, Integer orderType, String lineId, String couponCode) {
      CouponCalculateResponse couponResponse = this.calculateCoupon(memberNo, productType, orderType, lineId, couponCode, response.getPayPrice());
      if (couponResponse == null) {
         response.setCouponPrice(this.defaultAmount(response.getCouponPrice()));
      } else {
         response.setCouponPrice(couponResponse.getCouponPrice());
         response.setPayPrice(couponResponse.getPayPrice());
      }
   }

   public void applyCoupon(IndependentCalculatePriceResponse response, Long memberNo, Integer productType, Integer orderType, String lineId, String couponCode) {
      CouponCalculateResponse couponResponse = this.calculateCoupon(memberNo, productType, orderType, lineId, couponCode, response.getPayPrice());
      if (couponResponse == null) {
         response.setCouponPrice(this.defaultAmount(response.getCouponPrice()));
      } else {
         response.setCouponPrice(couponResponse.getCouponPrice());
         response.setPayPrice(couponResponse.getPayPrice());
      }
   }

   public CouponCalculateResponse calculateCoupon(
      Long memberNo, Integer productType, Integer orderType, String lineId, String couponCode, BigDecimal orderAmount
   ) {
      if (StrUtil.isBlank(couponCode)) {
         return null;
      } else {
         this.releaseStaleCouponLock(couponCode);
         CouponCalculateRequest request = new CouponCalculateRequest();
         request.setMemberNo(memberNo);
         request.setProductType(productType);
         request.setOrderType(orderType);
         request.setLineId(lineId);
         request.setCouponCode(couponCode);
         request.setOrderAmount(orderAmount);
         request.setUseCount(1);
         return this.memberCouponApi.calculateCoupon(request);
      }
   }

   public void lockCoupon(MemberOrder order, Long memberNo, Integer productType, Integer orderType, String lineId, String couponCode, BigDecimal orderAmount) {
      if (StrUtil.isBlank(couponCode)) {
         order.setCouponPrice(this.defaultAmount(order.getCouponPrice()));
         order.setCouponUseCount(this.defaultInt(order.getCouponUseCount()));
      } else {
         this.releaseStaleCouponLock(couponCode);
         CouponLockRequest request = new CouponLockRequest();
         request.setMemberNo(memberNo);
         request.setProductType(productType);
         request.setOrderType(orderType);
         request.setLineId(lineId);
         request.setCouponCode(couponCode);
         request.setOrderNo(order.getOrderNo());
         request.setOrderAmount(orderAmount);
         request.setUseCount(1);
         CouponCalculateResponse response = this.memberCouponApi.lockCoupon(request);
         this.fillOrderCoupon(order, response);
         order.setPayPrice(response.getPayPrice());
      }
   }

   public void useCoupon(String orderNo) {
      this.memberCouponApi.useCoupon(orderNo);
   }

   public void releaseCoupon(String orderNo) {
      this.memberCouponApi.releaseCoupon(orderNo);
   }

   private void releaseStaleCouponLock(String couponCode) {
      String lockOrderNo = this.memberCouponApi.getLockOrderNo(couponCode);
      if (!StrUtil.isBlank(lockOrderNo)) {
         List<MemberOrder> lockOrders = this.memberOrderMapper
            .selectList(
               (Wrapper<MemberOrder>)((LambdaQueryWrapper<MemberOrder>)((LambdaQueryWrapper<MemberOrder>)new LambdaQueryWrapper<MemberOrder>().eq(MemberOrder::getOrderNo, lockOrderNo))
                     .orderByDesc(MemberOrder::getCreateTime))
                  .last("LIMIT 1")
            );
         MemberOrder lockOrder = lockOrders != null && !lockOrders.isEmpty() ? lockOrders.get(0) : null;
         if (lockOrder == null || !OrderStatusEnum.UNPAID.getCode().equals(lockOrder.getOrderStatus())) {
            this.memberCouponApi.releaseCoupon(lockOrderNo);
         }
      }
   }

   public void rollbackCoupon(String orderNo) {
      this.memberCouponApi.rollbackCoupon(orderNo);
   }

   public BigDecimal resolveOrderAmountBeforeCoupon(BigDecimal payPrice, BigDecimal couponPrice) {
      return this.defaultAmount(payPrice).add(this.defaultAmount(couponPrice));
   }

   private void fillOrderCoupon(MemberOrder order, CouponCalculateResponse response) {
      if (response == null) {
         order.setCouponPrice(this.defaultAmount(order.getCouponPrice()));
         order.setCouponUseCount(this.defaultInt(order.getCouponUseCount()));
      } else {
         order.setCouponCode(response.getCouponCode());
         order.setMemberCouponId(response.getMemberCouponId());
         order.setCouponPrice(this.defaultAmount(response.getCouponPrice()));
         order.setCouponUseCount(this.defaultInt(response.getUseCount()));
      }
   }

   private BigDecimal defaultAmount(BigDecimal amount) {
      return amount == null ? BigDecimal.ZERO : amount;
   }

   private Integer defaultInt(Integer value) {
      return value == null ? 0 : value;
   }
}
