package com.rxinyun.admin.marketing.modular.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.marketing.modular.coupon.entity.MemberCoupon;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponIssueParam;
import com.rxinyun.admin.marketing.modular.coupon.param.MemberCouponPageParam;
import com.rxinyun.admin.marketing.modular.coupon.service.AdminMemberCouponService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员优惠券管理"
)
@RestController
@RequestMapping({"/admin/marketing/member-coupon"})
public class MemberCouponController {
   @Resource
   private AdminMemberCouponService adminMemberCouponService;

   @Operation(
      summary = "发券",
      description = "按模板给指定会员批量发券，或发券给全部启用会员"
   )
   @PostMapping({"/issue"})
   public CommonResult<String> issue(@Valid @RequestBody CouponIssueParam issueParam) {
      this.adminMemberCouponService.issueCoupons(issueParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "会员优惠券分页",
      description = "分页查询已发放给会员的优惠券记录"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberCoupon>> page(MemberCouponPageParam pageParam) {
      return CommonResult.data(this.adminMemberCouponService.page(pageParam));
   }

   @Operation(
      summary = "会员优惠券详情",
      description = "根据会员优惠券ID查询详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberCoupon> detail(@Parameter(description = "会员优惠券ID",required = true,example = "2039000000000000002") @RequestParam String id) {
      return CommonResult.data(this.adminMemberCouponService.detail(id));
   }
}
