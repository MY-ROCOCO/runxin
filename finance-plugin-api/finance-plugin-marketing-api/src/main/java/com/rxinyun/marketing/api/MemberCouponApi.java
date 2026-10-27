package com.rxinyun.marketing.api;

import com.rxinyun.marketing.api.dto.CouponCalculateRequest;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.marketing.api.dto.CouponLockRequest;
import com.rxinyun.marketing.api.dto.MemberCouponQueryRequest;
import com.rxinyun.marketing.api.dto.MemberCouponSimpleResult;
import java.util.List;

public interface MemberCouponApi {
   CouponCalculateResponse calculateCoupon(CouponCalculateRequest request);

   CouponCalculateResponse lockCoupon(CouponLockRequest request);

   void useCoupon(String orderNo);

   void releaseCoupon(String orderNo);

   String getLockOrderNo(String couponCode);

   void rollbackCoupon(String orderNo);

   List<MemberCouponSimpleResult> listAvailableCoupons(MemberCouponQueryRequest request);
}
