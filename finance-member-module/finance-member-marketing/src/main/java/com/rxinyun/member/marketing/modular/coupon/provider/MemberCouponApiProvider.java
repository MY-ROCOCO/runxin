package com.rxinyun.member.marketing.modular.coupon.provider;

import com.rxinyun.marketing.api.MemberCouponApi;
import com.rxinyun.marketing.api.dto.CouponCalculateRequest;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.marketing.api.dto.CouponLockRequest;
import com.rxinyun.marketing.api.dto.MemberCouponQueryRequest;
import com.rxinyun.marketing.api.dto.MemberCouponSimpleResult;
import com.rxinyun.member.marketing.modular.coupon.service.MemberCouponService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberCouponApiProvider implements MemberCouponApi {
   @Resource
   private MemberCouponService memberCouponService;

   public CouponCalculateResponse calculateCoupon(CouponCalculateRequest request) {
      return this.memberCouponService.calculateCoupon(request);
   }

   public CouponCalculateResponse lockCoupon(CouponLockRequest request) {
      return this.memberCouponService.lockCoupon(request);
   }

   public void useCoupon(String orderNo) {
      this.memberCouponService.useCoupon(orderNo);
   }

   public void releaseCoupon(String orderNo) {
      this.memberCouponService.releaseCoupon(orderNo);
   }

   public String getLockOrderNo(String couponCode) {
      return this.memberCouponService.getLockOrderNo(couponCode);
   }

   public void rollbackCoupon(String orderNo) {
      this.memberCouponService.rollbackCoupon(orderNo);
   }

   public List<MemberCouponSimpleResult> listAvailableCoupons(MemberCouponQueryRequest request) {
      return this.memberCouponService.listAvailableCoupons(request);
   }
}
