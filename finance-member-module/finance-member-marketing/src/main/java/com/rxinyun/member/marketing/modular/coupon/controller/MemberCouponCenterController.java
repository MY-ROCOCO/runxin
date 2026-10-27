package com.rxinyun.member.marketing.modular.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.marketing.modular.coupon.param.MemberCouponPageParam;
import com.rxinyun.member.marketing.modular.coupon.result.CouponTemplateClaimResult;
import com.rxinyun.member.marketing.modular.coupon.result.MemberCouponPageResult;
import com.rxinyun.member.marketing.modular.coupon.service.MemberCouponService;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员优惠券中心"
)
@RestController
@RequestMapping({"/member/marketing/coupon"})
@Validated
public class MemberCouponCenterController {
   @Resource
   private MemberCouponService memberCouponService;

   @Operation(
      summary = "我的优惠券分页",
      description = "分页查询当前登录会员的优惠券列表，下单时仍使用优惠券编码 couponCode"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberCouponPageResult>> page(MemberCouponPageParam pageParam) {
      Long memberNo = StpClientLoginUserUtil.getClientLoginUser().getId();
      return CommonResult.data(this.memberCouponService.pageMyCoupons(memberNo, pageParam));
   }

   @Operation(
      summary = "可领取优惠券列表",
      description = "公开查询前台活动页可领取的优惠券模板，不要求会员登录"
   )
   @GetMapping({"/claimable-list"})
   public CommonResult<Page<CouponTemplateClaimResult>> claimableList() {
      return CommonResult.data(this.memberCouponService.pageClaimableTemplates(null));
   }

   @Operation(
      summary = "领取优惠券",
      description = "当前登录会员按模板领取一张优惠券，领取成功后下单仍使用 couponCode"
   )
   @PostMapping({"/claim"})
   public CommonResult<MemberCouponPageResult> claim(@Parameter(description = "模板ID",required = true) @RequestParam String templateId) {
      SaBaseClientLoginUser loginUser = StpClientLoginUserUtil.getClientLoginUser();
      return CommonResult.data(this.memberCouponService.claimCoupon(loginUser.getId(), loginUser.getName(), templateId));
   }
}
