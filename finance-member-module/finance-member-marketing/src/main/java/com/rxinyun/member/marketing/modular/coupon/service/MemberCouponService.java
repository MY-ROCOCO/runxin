package com.rxinyun.member.marketing.modular.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.marketing.api.dto.CouponCalculateRequest;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.marketing.api.dto.CouponLockRequest;
import com.rxinyun.marketing.api.dto.MemberCouponQueryRequest;
import com.rxinyun.marketing.api.dto.MemberCouponSimpleResult;
import com.rxinyun.member.marketing.modular.coupon.param.MemberCouponPageParam;
import com.rxinyun.member.marketing.modular.coupon.result.CouponTemplateClaimResult;
import com.rxinyun.member.marketing.modular.coupon.result.MemberCouponPageResult;
import java.util.List;

public interface MemberCouponService {
   Page<MemberCouponPageResult> pageMyCoupons(Long memberNo, MemberCouponPageParam pageParam);

   Page<CouponTemplateClaimResult> pageClaimableTemplates(Long memberNo);

   MemberCouponPageResult claimCoupon(Long memberNo, String memberName, String templateId);

   CouponCalculateResponse calculateCoupon(CouponCalculateRequest request);

   CouponCalculateResponse lockCoupon(CouponLockRequest request);

   void useCoupon(String orderNo);

   void releaseCoupon(String orderNo);

   String getLockOrderNo(String couponCode);

   void rollbackCoupon(String orderNo);

   List<MemberCouponSimpleResult> listAvailableCoupons(MemberCouponQueryRequest request);
}
