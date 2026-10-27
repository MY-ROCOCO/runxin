package com.rxinyun.admin.marketing.modular.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.marketing.modular.coupon.entity.MemberCoupon;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponIssueParam;
import com.rxinyun.admin.marketing.modular.coupon.param.MemberCouponPageParam;

public interface AdminMemberCouponService {
   void issueCoupons(CouponIssueParam issueParam);

   Page<MemberCoupon> page(MemberCouponPageParam pageParam);

   MemberCoupon detail(String id);
}
