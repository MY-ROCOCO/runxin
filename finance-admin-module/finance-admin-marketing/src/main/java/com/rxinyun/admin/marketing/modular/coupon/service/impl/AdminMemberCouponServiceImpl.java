package com.rxinyun.admin.marketing.modular.coupon.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.marketing.modular.coupon.entity.CouponTemplate;
import com.rxinyun.admin.marketing.modular.coupon.entity.MemberCoupon;
import com.rxinyun.admin.marketing.modular.coupon.mapper.AdminCouponTemplateMapper;
import com.rxinyun.admin.marketing.modular.coupon.mapper.AdminMemberCouponMapper;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponIssueParam;
import com.rxinyun.admin.marketing.modular.coupon.param.MemberCouponPageParam;
import com.rxinyun.admin.marketing.modular.coupon.service.AdminMemberCouponService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.EnableDisableEnum;
import com.rxinyun.common.enums.marketing.MemberCouponStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminMemberCouponServiceImpl implements AdminMemberCouponService {
   private static final String CODE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
   @Resource
   private AdminCouponTemplateMapper adminCouponTemplateMapper;
   @Resource
   private AdminMemberCouponMapper adminMemberCouponMapper;
   @Resource
   private MemberApi memberApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void issueCoupons(CouponIssueParam issueParam) {
      if (issueParam != null && !StrUtil.isBlank(issueParam.getTemplateId())) {
         CouponTemplate couponTemplate = this.queryTemplate(issueParam.getTemplateId());
         if (EnableDisableEnum.ENABLE.getCode() != couponTemplate.getStatus()) {
            throw new CommonException("优惠券模板未启用，不能发券", new Object[0]);
         } else {
            List<Long> memberNos = this.resolveIssueMemberNos(issueParam);
            int issueCount = memberNos.size();
            if ((Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssueTotalCount(), 0) > 0
               && (Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssuedCount(), 0) + issueCount > couponTemplate.getIssueTotalCount()) {
               throw new CommonException("可发放数量不足", new Object[0]);
            } else {
               for (Long memberNo : memberNos) {
                  BaseMember baseMember = this.memberApi.detail(memberNo);
                  if (baseMember == null) {
                     throw new CommonException("会员不存在，memberNo={}", new Object[]{memberNo});
                  }

                  MemberCoupon memberCoupon = new MemberCoupon();
                  memberCoupon.setTemplateId(couponTemplate.getId());
                  memberCoupon.setTemplateName(couponTemplate.getCouponName());
                  memberCoupon.setMemberNo(memberNo);
                  memberCoupon.setMemberName(baseMember.getName());
                  memberCoupon.setCouponCode(this.generateCouponCode());
                  memberCoupon.setStatus(MemberCouponStatusEnum.UNUSED.getCode());
                  memberCoupon.setUsedCount(0);
                  memberCoupon.setRemainingUseCount(couponTemplate.getUseLimit());
                  memberCoupon.setEffectiveTime(couponTemplate.getStartTime());
                  memberCoupon.setExpireTime(couponTemplate.getEndTime());
                  this.adminMemberCouponMapper.insert(memberCoupon);
               }

               couponTemplate.setIssuedCount((Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssuedCount(), 0) + issueCount);
               this.adminCouponTemplateMapper.updateById(couponTemplate);
            }
         }
      } else {
         throw new CommonException("模板ID不能为空", new Object[0]);
      }
   }

   private List<Long> resolveIssueMemberNos(CouponIssueParam issueParam) {
      if (Boolean.TRUE.equals(issueParam.getIssueToAll())) {
         List<Long> memberNos = this.memberApi
            .listEnabledMembers()
            .stream()
            .<Long>map(BaseMember::getId)
            .filter(ObjectUtil::isNotNull)
            .distinct()
            .collect(Collectors.toList());
         if (ObjectUtil.isEmpty(memberNos)) {
            throw new CommonException("暂无可发券的启用会员", new Object[0]);
         } else {
            return memberNos;
         }
      } else {
         List<Long> memberNos = issueParam.getMemberNos();
         if (ObjectUtil.isEmpty(memberNos)) {
            throw new CommonException("请选择会员", new Object[0]);
         } else {
            return memberNos.stream().filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
         }
      }
   }

   @Override
   public Page<MemberCoupon> page(MemberCouponPageParam pageParam) {
      QueryWrapper<MemberCoupon> queryWrapper = new QueryWrapper();
      if (StrUtil.isNotBlank(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)q.like(
                              MemberCoupon::getCouponCode, pageParam.getSearchKey()
                           ))
                           .or())
                        .like(MemberCoupon::getTemplateName, pageParam.getSearchKey()))
                     .or())
                  .like(MemberCoupon::getMemberName, pageParam.getSearchKey())
            );
      }

      if (StrUtil.isNotBlank(pageParam.getTemplateId())) {
         queryWrapper.lambda().eq(MemberCoupon::getTemplateId, pageParam.getTemplateId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().eq(MemberCoupon::getMemberNo, pageParam.getMemberNo());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(MemberCoupon::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberCoupon>)this.adminMemberCouponMapper.selectPage(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberCoupon detail(String id) {
      MemberCoupon memberCoupon = (MemberCoupon)this.adminMemberCouponMapper.selectById(id);
      if (memberCoupon == null) {
         throw new CommonException("会员优惠券不存在", new Object[0]);
      } else {
         return memberCoupon;
      }
   }

   private CouponTemplate queryTemplate(String id) {
      CouponTemplate couponTemplate = (CouponTemplate)this.adminCouponTemplateMapper.selectById(id);
      if (couponTemplate == null) {
         throw new CommonException("优惠券模板不存在", new Object[0]);
      } else {
         return couponTemplate;
      }
   }

   private String generateCouponCode() {
      for (int i = 0; i < 20; i++) {
         String couponCode = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789", 9);
         long count = this.adminMemberCouponMapper.selectCount((Wrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponCode, couponCode));
         if (count == 0L) {
            return couponCode;
         }
      }

      throw new CommonException("生成优惠券编码失败，请稍后重试", new Object[0]);
   }
}
