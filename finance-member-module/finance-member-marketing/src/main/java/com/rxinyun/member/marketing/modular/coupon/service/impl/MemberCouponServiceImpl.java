package com.rxinyun.member.marketing.modular.coupon.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.EnableDisableEnum;
import com.rxinyun.common.enums.marketing.CouponOrderRelationStatusEnum;
import com.rxinyun.common.enums.marketing.CouponTypeEnum;
import com.rxinyun.common.enums.marketing.MemberCouponStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.marketing.api.dto.CouponCalculateRequest;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.marketing.api.dto.CouponLockRequest;
import com.rxinyun.marketing.api.dto.MemberCouponQueryRequest;
import com.rxinyun.marketing.api.dto.MemberCouponSimpleResult;
import com.rxinyun.member.marketing.modular.coupon.entity.CouponOrderRelation;
import com.rxinyun.member.marketing.modular.coupon.entity.CouponTemplate;
import com.rxinyun.member.marketing.modular.coupon.entity.MemberCoupon;
import com.rxinyun.member.marketing.modular.coupon.mapper.CouponOrderRelationMapper;
import com.rxinyun.member.marketing.modular.coupon.mapper.CouponTemplateMapper;
import com.rxinyun.member.marketing.modular.coupon.mapper.MemberCouponMapper;
import com.rxinyun.member.marketing.modular.coupon.param.MemberCouponPageParam;
import com.rxinyun.member.marketing.modular.coupon.result.CouponTemplateClaimResult;
import com.rxinyun.member.marketing.modular.coupon.result.MemberCouponPageResult;
import com.rxinyun.member.marketing.modular.coupon.service.MemberCouponService;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberCouponServiceImpl implements MemberCouponService {
   private static final String CODE_CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
   @Resource
   private CouponTemplateMapper couponTemplateMapper;
   @Resource
   private MemberCouponMapper memberCouponMapper;
   @Resource
   private CouponOrderRelationMapper couponOrderRelationMapper;
   @Resource
   private MemberLocalLineApi memberLocalLineApi;
   @Resource
   private IndependentLineApi independentLineApi;

   @Override
   public Page<MemberCouponPageResult> pageMyCoupons(Long memberNo, MemberCouponPageParam pageParam) {
      if (memberNo == null) {
         throw new CommonException("会员编号不能为空", new Object[0]);
      } else {
         MemberCouponPageParam realPageParam = pageParam == null ? new MemberCouponPageParam() : pageParam;
         this.refreshExpiredCoupons(memberNo);
         QueryWrapper<MemberCoupon> queryWrapper = new QueryWrapper();
         queryWrapper.lambda().eq(MemberCoupon::getMemberNo, memberNo);
         if (ObjectUtil.isNotEmpty(realPageParam.getSearchKey())) {
            queryWrapper.lambda()
               .and(
                  q -> ((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)q.like(MemberCoupon::getTemplateName, realPageParam.getSearchKey())).or())
                     .like(MemberCoupon::getCouponCode, realPageParam.getSearchKey())
               );
         }

         if (ObjectUtil.isNotEmpty(realPageParam.getStatus())) {
            queryWrapper.lambda().eq(MemberCoupon::getStatus, realPageParam.getStatus());
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{realPageParam.getSortField(), realPageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(realPageParam.getSortOrder());
            queryWrapper.orderBy(
               true, CommonSortOrderEnum.ASC.getValue().equals(realPageParam.getSortOrder()), StrUtil.toUnderlineCase(realPageParam.getSortField())
            );
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         Page<MemberCoupon> memberCouponPage = (Page<MemberCoupon>)this.memberCouponMapper.selectPage(CommonPageRequest.defaultPage(), queryWrapper);
         Page<MemberCouponPageResult> resultPage = new Page(memberCouponPage.getCurrent(), memberCouponPage.getSize(), memberCouponPage.getTotal());
         if (CollUtil.isEmpty(memberCouponPage.getRecords())) {
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
         } else {
            Set<String> templateIds = memberCouponPage.getRecords()
               .stream()
               .map(MemberCoupon::getTemplateId)
               .filter(CharSequenceUtil::isNotBlank)
               .collect(Collectors.toSet());
            Map<String, CouponTemplate> templateMap = this.couponTemplateMapper
               .selectBatchIds(templateIds)
               .stream()
               .collect(Collectors.toMap(CouponTemplate::getId, item -> (CouponTemplate)item, (left, right) -> left));
            List<MemberCouponPageResult> results = memberCouponPage.getRecords()
               .stream()
               .map(memberCoupon -> this.buildPageResult(memberCoupon, templateMap.get(memberCoupon.getTemplateId())))
               .collect(Collectors.toList());
            resultPage.setRecords(results);
            return resultPage;
         }
      }
   }

   @Override
   public Page<CouponTemplateClaimResult> pageClaimableTemplates(Long memberNo) {
      Date now = new Date();
      Page<CouponTemplate> couponTemplatePage = (Page<CouponTemplate>)this.couponTemplateMapper
         .selectPage(
            CommonPageRequest.defaultPage(),
            (Wrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)new LambdaQueryWrapper<CouponTemplate>()
                                 .eq(CouponTemplate::getStatus, EnableDisableEnum.ENABLE.getCode()))
                              .eq(CouponTemplate::getClaimEnabled, 1))
                           .and(
                              wrapper -> ((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)wrapper.isNull(CouponTemplate::getStartTime)).or())
                                 .le(CouponTemplate::getStartTime, now)
                           ))
                        .and(
                           wrapper -> ((LambdaQueryWrapper<CouponTemplate>)((LambdaQueryWrapper<CouponTemplate>)wrapper.isNull(CouponTemplate::getEndTime)).or())
                              .ge(CouponTemplate::getEndTime, now)
                        ))
                     .apply("(ISSUE_TOTAL_COUNT = 0 OR IFNULL(ISSUED_COUNT, 0) < ISSUE_TOTAL_COUNT)", new Object[0]))
                  .orderByAsc(CouponTemplate::getSortCode))
               .orderByDesc(CommonEntity::getCreateTime)
         );
      Page<CouponTemplateClaimResult> resultPage = new Page(couponTemplatePage.getCurrent(), couponTemplatePage.getSize(), couponTemplatePage.getTotal());
      List<CouponTemplate> couponTemplates = couponTemplatePage.getRecords();
      if (CollUtil.isEmpty(couponTemplates)) {
         resultPage.setRecords(Collections.emptyList());
         return resultPage;
      } else {
         Set<String> templateIds = couponTemplates.stream().map(CouponTemplate::getId).collect(Collectors.toSet());
         Set<String> claimedTemplateIds = memberNo == null
            ? Collections.emptySet()
            : this.memberCouponMapper
               .selectList(
                  (Wrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getMemberNo, memberNo)).in(MemberCoupon::getTemplateId, templateIds)
               )
               .stream()
               .map(MemberCoupon::getTemplateId)
               .collect(Collectors.toSet());
         List<CouponTemplateClaimResult> results = couponTemplates.stream()
            .map(couponTemplate -> this.buildClaimResult(couponTemplate, claimedTemplateIds.contains(couponTemplate.getId())))
            .collect(Collectors.toList());
         resultPage.setRecords(results);
         return resultPage;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public MemberCouponPageResult claimCoupon(Long memberNo, String memberName, String templateId) {
      if (memberNo == null) {
         throw new CommonException("会员编号不能为空", new Object[0]);
      } else if (StrUtil.isBlank(templateId)) {
         throw new CommonException("优惠券模板ID不能为空", new Object[0]);
      } else {
         CouponTemplate couponTemplate = this.queryCouponTemplate(templateId);
         this.validateClaimableTemplate(couponTemplate);
         long claimedCount = this.memberCouponMapper
            .selectCount(
               (Wrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getMemberNo, memberNo)).eq(MemberCoupon::getTemplateId, templateId)
            );
         if (claimedCount > 0L) {
            throw new CommonException("您已领取过该优惠券", new Object[0]);
         } else {
            int updatedCount = this.couponTemplateMapper
               .update(
                  null,
                  (Wrapper<CouponTemplate>)((LambdaUpdateWrapper<CouponTemplate>)((LambdaUpdateWrapper<CouponTemplate>)((LambdaUpdateWrapper<CouponTemplate>)((LambdaUpdateWrapper<CouponTemplate>)new LambdaUpdateWrapper<CouponTemplate>()
                                 .setSql("ISSUED_COUNT = IFNULL(ISSUED_COUNT, 0) + 1", new Object[0]))
                              .eq(CouponTemplate::getId, templateId))
                           .eq(CouponTemplate::getStatus, EnableDisableEnum.ENABLE.getCode()))
                        .eq(CouponTemplate::getClaimEnabled, 1))
                     .apply("(ISSUE_TOTAL_COUNT = 0 OR IFNULL(ISSUED_COUNT, 0) < ISSUE_TOTAL_COUNT)", new Object[0])
               );
            if (updatedCount <= 0) {
               throw new CommonException("优惠券已领完", new Object[0]);
            } else {
               MemberCoupon memberCoupon = this.createMemberCoupon(couponTemplate, memberNo, memberName);
               this.memberCouponMapper.insert(memberCoupon);
               return this.buildPageResult(memberCoupon, couponTemplate);
            }
         }
      }
   }

   @Override
   public CouponCalculateResponse calculateCoupon(CouponCalculateRequest request) {
      if (request != null && !StrUtil.isBlank(request.getCouponCode())) {
         MemberCouponServiceImpl.CouponContext couponContext = this.buildCouponContext(
            request.getMemberNo(),
            request.getProductType(),
            request.getOrderType(),
            request.getLineId(),
            request.getCouponCode(),
            request.getOrderAmount(),
            request.getUseCount(),
            null
         );
         return this.buildCalculateResponse(couponContext.memberCoupon, couponContext.couponTemplate, couponContext.orderAmount, couponContext.useCount);
      } else {
         return null;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CouponCalculateResponse lockCoupon(CouponLockRequest request) {
      if (request == null || StrUtil.isBlank(request.getCouponCode())) {
         return null;
      } else if (StrUtil.isBlank(request.getOrderNo())) {
         throw new CommonException("订单号不能为空", new Object[0]);
      } else {
         CouponOrderRelation existingRelation = this.queryRelationByOrderNo(request.getOrderNo());
         if (existingRelation != null) {
            if (CouponOrderRelationStatusEnum.LOCKED.getCode() != existingRelation.getStatus()
               && CouponOrderRelationStatusEnum.USED.getCode() != existingRelation.getStatus()) {
               throw new CommonException("该订单的优惠券状态异常，无法重复锁定", new Object[0]);
            } else {
               return this.buildRelationResponse(existingRelation);
            }
         } else {
            MemberCouponServiceImpl.CouponContext couponContext = this.buildCouponContext(
               request.getMemberNo(),
               request.getProductType(),
               request.getOrderType(),
               request.getLineId(),
               request.getCouponCode(),
               request.getOrderAmount(),
               request.getUseCount(),
               request.getOrderNo()
            );
            CouponCalculateResponse response = this.buildCalculateResponse(
               couponContext.memberCoupon, couponContext.couponTemplate, couponContext.orderAmount, couponContext.useCount
            );
            Date now = new Date();
            MemberCoupon memberCoupon = couponContext.memberCoupon;
            memberCoupon.setStatus(MemberCouponStatusEnum.LOCKED.getCode());
            memberCoupon.setLockOrderNo(request.getOrderNo());
            memberCoupon.setLockTime(now);
            this.memberCouponMapper.updateById(memberCoupon);
            CouponOrderRelation relation = new CouponOrderRelation();
            relation.setOrderNo(request.getOrderNo());
            relation.setMemberNo(request.getMemberNo());
            relation.setProductType(request.getProductType());
            relation.setTemplateId(couponContext.couponTemplate.getId());
            relation.setTemplateName(couponContext.couponTemplate.getCouponName());
            relation.setMemberCouponId(memberCoupon.getId());
            relation.setCouponCode(memberCoupon.getCouponCode());
            relation.setCouponPrice(response.getCouponPrice());
            relation.setUseCount(response.getUseCount());
            relation.setStatus(CouponOrderRelationStatusEnum.LOCKED.getCode());
            relation.setLockTime(now);
            relation.setSnapshotJson(JSONUtil.toJsonStr(couponContext.couponTemplate));
            this.couponOrderRelationMapper.insert(relation);
            return response;
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void useCoupon(String orderNo) {
      CouponOrderRelation relation = this.queryRelationByOrderNo(orderNo);
      if (relation != null && relation.getStatus() != null) {
         if (CouponOrderRelationStatusEnum.USED.getCode() != relation.getStatus()) {
            if (CouponOrderRelationStatusEnum.LOCKED.getCode() == relation.getStatus()) {
               MemberCoupon memberCoupon = this.queryMemberCouponById(relation.getMemberCouponId());
               int useCount = (Integer)ObjectUtil.defaultIfNull(relation.getUseCount(), 1);
               if (!ObjectUtil.isNull(memberCoupon.getRemainingUseCount()) && memberCoupon.getRemainingUseCount() >= useCount) {
                  Date now = new Date();
                  memberCoupon.setUsedCount((Integer)ObjectUtil.defaultIfNull(memberCoupon.getUsedCount(), 0) + useCount);
                  memberCoupon.setRemainingUseCount(memberCoupon.getRemainingUseCount() - useCount);
                  memberCoupon.setLastUseTime(now);
                  memberCoupon.setLockOrderNo(null);
                  memberCoupon.setLockTime(null);
                  if (memberCoupon.getRemainingUseCount() <= 0) {
                     memberCoupon.setRemainingUseCount(0);
                     memberCoupon.setStatus(MemberCouponStatusEnum.USED_UP.getCode());
                  } else {
                     memberCoupon.setStatus(this.resolveRestoredStatus(memberCoupon, now));
                  }

                  this.memberCouponMapper.updateById(memberCoupon);
                  relation.setStatus(CouponOrderRelationStatusEnum.USED.getCode());
                  relation.setUseTime(now);
                  this.couponOrderRelationMapper.updateById(relation);
               } else {
                  throw new CommonException("优惠券剩余可用次数不足", new Object[0]);
               }
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void releaseCoupon(String orderNo) {
      CouponOrderRelation relation = this.queryRelationByOrderNo(orderNo);
      if (relation != null && relation.getStatus() != null) {
         if (CouponOrderRelationStatusEnum.LOCKED.getCode() == relation.getStatus()) {
            MemberCoupon memberCoupon = this.queryMemberCouponById(relation.getMemberCouponId());
            Date now = new Date();
            memberCoupon.setLockOrderNo(null);
            memberCoupon.setLockTime(null);
            memberCoupon.setStatus(this.resolveRestoredStatus(memberCoupon, now));
            this.memberCouponMapper.updateById(memberCoupon);
            relation.setStatus(CouponOrderRelationStatusEnum.RELEASED.getCode());
            relation.setReleaseTime(now);
            this.couponOrderRelationMapper.updateById(relation);
         }
      }
   }

   @Override
   public String getLockOrderNo(String couponCode) {
      if (StrUtil.isBlank(couponCode)) {
         return null;
      } else {
         MemberCoupon memberCoupon = (MemberCoupon)this.memberCouponMapper
            .selectOne((Wrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponCode, couponCode)).last("LIMIT 1"));
         return memberCoupon != null && Integer.valueOf(MemberCouponStatusEnum.LOCKED.getCode()).equals(memberCoupon.getStatus())
            ? memberCoupon.getLockOrderNo()
            : null;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void rollbackCoupon(String orderNo) {
      CouponOrderRelation relation = this.queryRelationByOrderNo(orderNo);
      if (relation != null && relation.getStatus() != null) {
         if (CouponOrderRelationStatusEnum.LOCKED.getCode() == relation.getStatus()) {
            this.releaseCoupon(orderNo);
         } else if (CouponOrderRelationStatusEnum.USED.getCode() == relation.getStatus()) {
            MemberCoupon memberCoupon = this.queryMemberCouponById(relation.getMemberCouponId());
            int useCount = (Integer)ObjectUtil.defaultIfNull(relation.getUseCount(), 1);
            Date now = new Date();
            memberCoupon.setUsedCount(Math.max((Integer)ObjectUtil.defaultIfNull(memberCoupon.getUsedCount(), 0) - useCount, 0));
            memberCoupon.setRemainingUseCount((Integer)ObjectUtil.defaultIfNull(memberCoupon.getRemainingUseCount(), 0) + useCount);
            memberCoupon.setLockOrderNo(null);
            memberCoupon.setLockTime(null);
            memberCoupon.setStatus(this.resolveRestoredStatus(memberCoupon, now));
            this.memberCouponMapper.updateById(memberCoupon);
            relation.setStatus(CouponOrderRelationStatusEnum.REFUNDED.getCode());
            relation.setReleaseTime(now);
            this.couponOrderRelationMapper.updateById(relation);
         }
      }
   }

   @Override
   public List<MemberCouponSimpleResult> listAvailableCoupons(MemberCouponQueryRequest request) {
      if (request != null && request.getMemberNo() != null) {
         List<MemberCoupon> memberCoupons = this.memberCouponMapper
            .selectList(
               (Wrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getMemberNo, request.getMemberNo()))
                     .eq(MemberCoupon::getStatus, MemberCouponStatusEnum.UNUSED.getCode()))
                  .orderByDesc(CommonEntity::getCreateTime)
            );
         if (CollUtil.isEmpty(memberCoupons)) {
            return new ArrayList<>();
         } else {
            List<MemberCouponSimpleResult> results = new ArrayList<>();

            for (MemberCoupon memberCoupon : memberCoupons) {
               CouponTemplate couponTemplate = (CouponTemplate)this.couponTemplateMapper.selectById(memberCoupon.getTemplateId());
               if (couponTemplate != null) {
                  try {
                     MemberCouponServiceImpl.CouponContext couponContext = this.buildCouponContext(
                        request.getMemberNo(),
                        request.getProductType(),
                        request.getOrderType(),
                        request.getLineId(),
                        memberCoupon.getCouponCode(),
                        request.getOrderAmount(),
                        1,
                        null
                     );
                     CouponCalculateResponse response = this.buildCalculateResponse(
                        couponContext.memberCoupon, couponContext.couponTemplate, couponContext.orderAmount, couponContext.useCount
                     );
                     MemberCouponSimpleResult result = new MemberCouponSimpleResult();
                     result.setMemberCouponId(memberCoupon.getId());
                     result.setTemplateId(couponTemplate.getId());
                     result.setCouponName(couponTemplate.getCouponName());
                     result.setCouponCode(memberCoupon.getCouponCode());
                     result.setCouponType(couponTemplate.getCouponType());
                     result.setThresholdAmount(couponTemplate.getThresholdAmount());
                     result.setReduceAmount(couponTemplate.getReduceAmount());
                     result.setDiscountRate(couponTemplate.getDiscountRate());
                     result.setMaxDiscountAmount(couponTemplate.getMaxDiscountAmount());
                     result.setEstimatedCouponPrice(response.getCouponPrice());
                     result.setRemainingUseCount(memberCoupon.getRemainingUseCount());
                     result.setEndTime(memberCoupon.getExpireTime());
                     results.add(result);
                  } catch (Exception var10) {
                  }
               }
            }

            return results;
         }
      } else {
         return new ArrayList<>();
      }
   }

   private MemberCouponServiceImpl.CouponContext buildCouponContext(
      Long memberNo, Integer productType, Integer orderType, String lineId, String couponCode, BigDecimal orderAmount, Integer useCount, String orderNo
   ) {
      if (memberNo == null) {
         throw new CommonException("会员编号不能为空", new Object[0]);
      } else if (productType == null) {
         throw new CommonException("产品类型不能为空", new Object[0]);
      } else if (orderAmount != null && orderAmount.compareTo(BigDecimal.ZERO) > 0) {
         MemberCoupon memberCoupon = this.queryMemberCouponByCode(couponCode);
         if (!memberNo.equals(memberCoupon.getMemberNo())) {
            throw new CommonException("该优惠券不属于当前会员", new Object[0]);
         } else {
            CouponTemplate couponTemplate = this.queryCouponTemplate(memberCoupon.getTemplateId());
            int realUseCount = (Integer)ObjectUtil.defaultIfNull(useCount, 1);
            this.validateCoupon(memberCoupon, couponTemplate, productType, orderType, lineId, orderAmount, realUseCount, orderNo);
            MemberCouponServiceImpl.CouponContext couponContext = new MemberCouponServiceImpl.CouponContext();
            couponContext.memberCoupon = memberCoupon;
            couponContext.couponTemplate = couponTemplate;
            couponContext.orderAmount = orderAmount.setScale(2, RoundingMode.HALF_UP);
            couponContext.useCount = realUseCount;
            return couponContext;
         }
      } else {
         throw new CommonException("订单金额异常", new Object[0]);
      }
   }

   private MemberCouponPageResult buildPageResult(MemberCoupon memberCoupon, CouponTemplate couponTemplate) {
      MemberCouponPageResult result = new MemberCouponPageResult();
      result.setId(memberCoupon.getId());
      result.setTemplateId(memberCoupon.getTemplateId());
      result.setCouponName(couponTemplate == null ? memberCoupon.getTemplateName() : couponTemplate.getCouponName());
      result.setCouponDescription(couponTemplate == null ? null : couponTemplate.getCouponDescription());
      result.setCouponCode(memberCoupon.getCouponCode());
      result.setCouponType(couponTemplate == null ? null : couponTemplate.getCouponType());
      result.setThresholdAmount(couponTemplate == null ? null : couponTemplate.getThresholdAmount());
      result.setReduceAmount(couponTemplate == null ? null : couponTemplate.getReduceAmount());
      result.setDiscountRate(couponTemplate == null ? null : couponTemplate.getDiscountRate());
      result.setMaxDiscountAmount(couponTemplate == null ? null : couponTemplate.getMaxDiscountAmount());
      result.setApplyProductTypes(couponTemplate == null ? null : couponTemplate.getApplyProductTypes());
      result.setApplyCloudLineIds(couponTemplate == null ? null : couponTemplate.getApplyCloudLineIds());
      result.setApplyCloudLineNames(couponTemplate == null ? null : this.resolveCloudLineNames(couponTemplate.getApplyCloudLineIds()));
      result.setApplyIndependentLineIds(couponTemplate == null ? null : couponTemplate.getApplyIndependentLineIds());
      result.setApplyIndependentLineNames(couponTemplate == null ? null : this.resolveIndependentLineNames(couponTemplate.getApplyIndependentLineIds()));
      result.setApplyOrderTypes(couponTemplate == null ? null : couponTemplate.getApplyOrderTypes());
      result.setStatus(memberCoupon.getStatus());
      result.setUsedCount(memberCoupon.getUsedCount());
      result.setRemainingUseCount(memberCoupon.getRemainingUseCount());
      result.setLockOrderNo(memberCoupon.getLockOrderNo());
      result.setEffectiveTime(memberCoupon.getEffectiveTime());
      result.setExpireTime(memberCoupon.getExpireTime());
      result.setLastUseTime(memberCoupon.getLastUseTime());
      result.setCreateTime(memberCoupon.getCreateTime());
      return result;
   }

   private CouponTemplateClaimResult buildClaimResult(CouponTemplate couponTemplate, boolean claimed) {
      CouponTemplateClaimResult result = new CouponTemplateClaimResult();
      result.setTemplateId(couponTemplate.getId());
      result.setCouponName(couponTemplate.getCouponName());
      result.setCouponDescription(couponTemplate.getCouponDescription());
      result.setCouponType(couponTemplate.getCouponType());
      result.setThresholdAmount(couponTemplate.getThresholdAmount());
      result.setReduceAmount(couponTemplate.getReduceAmount());
      result.setDiscountRate(couponTemplate.getDiscountRate());
      result.setMaxDiscountAmount(couponTemplate.getMaxDiscountAmount());
      result.setApplyProductTypes(couponTemplate.getApplyProductTypes());
      result.setApplyCloudLineIds(couponTemplate.getApplyCloudLineIds());
      result.setApplyCloudLineNames(this.resolveCloudLineNames(couponTemplate.getApplyCloudLineIds()));
      result.setApplyIndependentLineIds(couponTemplate.getApplyIndependentLineIds());
      result.setApplyIndependentLineNames(this.resolveIndependentLineNames(couponTemplate.getApplyIndependentLineIds()));
      result.setApplyOrderTypes(couponTemplate.getApplyOrderTypes());
      result.setStartTime(couponTemplate.getStartTime());
      result.setEndTime(couponTemplate.getEndTime());
      result.setRemainingClaimCount(this.calculateRemainingClaimCount(couponTemplate));
      result.setClaimed(claimed);
      result.setSortCode(couponTemplate.getSortCode());
      return result;
   }

   private Integer calculateRemainingClaimCount(CouponTemplate couponTemplate) {
      int issueTotalCount = (Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssueTotalCount(), 0);
      return issueTotalCount <= 0 ? -1 : Math.max(issueTotalCount - (Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssuedCount(), 0), 0);
   }

   private String resolveCloudLineNames(String lineIds) {
      List<String> ids = this.splitScopeValues(lineIds);
      if (CollUtil.isEmpty(ids)) {
         return null;
      } else {
         Map<String, BaseMemberLocalLine> lineMap = this.memberLocalLineApi.selectByLineIds(ids);
         if (CollUtil.isEmpty(lineMap)) {
            return null;
         } else {
            List<String> names = ids.stream()
               .map(lineMap::get)
               .filter(ObjectUtil::isNotNull)
               .<String>map(BaseMemberLocalLine::getLineName)
               .filter(CharSequenceUtil::isNotBlank)
               .collect(Collectors.toList());
            return CollUtil.isEmpty(names) ? null : String.join(",", names);
         }
      }
   }

   private String resolveIndependentLineNames(String lineIds) {
      List<String> ids = this.splitScopeValues(lineIds);
      if (CollUtil.isEmpty(ids)) {
         return null;
      } else {
         List<String> names = new ArrayList<>();

         for (String id : ids) {
            try {
               BaseIndependentLine line = this.independentLineApi.selectByLineId(id);
               if (line != null && StrUtil.isNotBlank(line.getName())) {
                  names.add(line.getName());
               }
            } catch (Exception var7) {
            }
         }

         return CollUtil.isEmpty(names) ? null : String.join(",", names);
      }
   }

   private List<String> splitScopeValues(String scopeValues) {
      return StrUtil.isBlank(scopeValues)
         ? Collections.emptyList()
         : Arrays.stream(scopeValues.split(",")).map(String::trim).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
   }

   private void validateClaimableTemplate(CouponTemplate couponTemplate) {
      Date now = new Date();
      if (EnableDisableEnum.ENABLE.getCode() != couponTemplate.getStatus()) {
         throw new CommonException("优惠券模板未启用", new Object[0]);
      } else if (!Integer.valueOf(1).equals(couponTemplate.getClaimEnabled())) {
         throw new CommonException("该优惠券暂不支持前台领取", new Object[0]);
      } else if (couponTemplate.getStartTime() != null && couponTemplate.getStartTime().after(now)) {
         throw new CommonException("优惠券尚未开始领取", new Object[0]);
      } else if (couponTemplate.getEndTime() != null && couponTemplate.getEndTime().before(now)) {
         throw new CommonException("优惠券已结束领取", new Object[0]);
      } else if ((Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssueTotalCount(), 0) > 0
         && (Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssuedCount(), 0) >= couponTemplate.getIssueTotalCount()) {
         throw new CommonException("优惠券已领完", new Object[0]);
      }
   }

   private MemberCoupon createMemberCoupon(CouponTemplate couponTemplate, Long memberNo, String memberName) {
      MemberCoupon memberCoupon = new MemberCoupon();
      memberCoupon.setTemplateId(couponTemplate.getId());
      memberCoupon.setTemplateName(couponTemplate.getCouponName());
      memberCoupon.setMemberNo(memberNo);
      memberCoupon.setMemberName(memberName);
      memberCoupon.setCouponCode(this.generateCouponCode());
      memberCoupon.setStatus(MemberCouponStatusEnum.UNUSED.getCode());
      memberCoupon.setUsedCount(0);
      memberCoupon.setRemainingUseCount(couponTemplate.getUseLimit());
      memberCoupon.setEffectiveTime(couponTemplate.getStartTime());
      memberCoupon.setExpireTime(couponTemplate.getEndTime());
      return memberCoupon;
   }

   private String generateCouponCode() {
      for (int i = 0; i < 20; i++) {
         String couponCode = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz0123456789", 9);
         long count = this.memberCouponMapper.selectCount((Wrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponCode, couponCode));
         if (count == 0L) {
            return couponCode;
         }
      }

      throw new CommonException("生成优惠券编码失败，请稍后重试", new Object[0]);
   }

   private void refreshExpiredCoupons(Long memberNo) {
      Date now = new Date();

      for (MemberCoupon expiredCoupon : this.memberCouponMapper
         .selectList(
            (Wrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)((LambdaQueryWrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getMemberNo, memberNo))
                     .in(MemberCoupon::getStatus, new Object[]{MemberCouponStatusEnum.UNUSED.getCode(), MemberCouponStatusEnum.LOCKED.getCode()}))
                  .isNotNull(MemberCoupon::getExpireTime))
               .lt(MemberCoupon::getExpireTime, now)
         )) {
         expiredCoupon.setStatus(MemberCouponStatusEnum.EXPIRED.getCode());
         expiredCoupon.setLockOrderNo(null);
         expiredCoupon.setLockTime(null);
         this.memberCouponMapper.updateById(expiredCoupon);
      }
   }

   private void validateCoupon(
      MemberCoupon memberCoupon,
      CouponTemplate couponTemplate,
      Integer productType,
      Integer orderType,
      String lineId,
      BigDecimal orderAmount,
      Integer useCount,
      String orderNo
   ) {
      Date now = new Date();
      if (EnableDisableEnum.ENABLE.getCode() != couponTemplate.getStatus()) {
         throw new CommonException("优惠券模板已禁用", new Object[0]);
      } else if (couponTemplate.getStartTime() != null && couponTemplate.getStartTime().after(now)) {
         throw new CommonException("优惠券尚未生效", new Object[0]);
      } else if (couponTemplate.getEndTime() != null && couponTemplate.getEndTime().before(now)) {
         throw new CommonException("优惠券已过期", new Object[0]);
      } else if (memberCoupon.getEffectiveTime() != null && memberCoupon.getEffectiveTime().after(now)) {
         throw new CommonException("优惠券尚未生效", new Object[0]);
      } else if (memberCoupon.getExpireTime() != null && memberCoupon.getExpireTime().before(now)) {
         memberCoupon.setStatus(MemberCouponStatusEnum.EXPIRED.getCode());
         this.memberCouponMapper.updateById(memberCoupon);
         throw new CommonException("优惠券已过期", new Object[0]);
      } else if (memberCoupon.getStatus() == null) {
         throw new CommonException("优惠券状态异常", new Object[0]);
      } else {
         if (MemberCouponStatusEnum.LOCKED.getCode() == memberCoupon.getStatus()) {
            if (StrUtil.isBlank(orderNo) || !StrUtil.equals(orderNo, memberCoupon.getLockOrderNo())) {
               throw new CommonException("优惠券已被其他订单占用", new Object[0]);
            }
         } else if (MemberCouponStatusEnum.UNUSED.getCode() != memberCoupon.getStatus()) {
            throw new CommonException("优惠券当前不可用", new Object[0]);
         }

         if (!ObjectUtil.isNull(memberCoupon.getRemainingUseCount()) && memberCoupon.getRemainingUseCount() >= useCount) {
            if (StrUtil.isNotBlank(couponTemplate.getApplyProductTypes())) {
               String wrappedValue = "," + couponTemplate.getApplyProductTypes() + ",";
               if (!wrappedValue.contains("," + productType + ",")) {
                  throw new CommonException("该优惠券不适用于当前产品类型", new Object[0]);
               }
            }

            if (StrUtil.isNotBlank(couponTemplate.getApplyOrderTypes())) {
               if (orderType == null) {
                  throw new CommonException("订单类型不能为空", new Object[0]);
               }

               if (!this.containsScopeValue(couponTemplate.getApplyOrderTypes(), orderType)) {
                  throw new CommonException("该优惠券不适用于当前订单类型", new Object[0]);
               }
            }

            if (ProductTypeEnum.CLOUD_SERVER.getCode().equals(productType) && StrUtil.isNotBlank(couponTemplate.getApplyCloudLineIds())) {
               if (StrUtil.isBlank(lineId)) {
                  throw new CommonException("当前订单缺少云服务器线路信息", new Object[0]);
               }

               if (!this.containsScopeValue(couponTemplate.getApplyCloudLineIds(), lineId)) {
                  throw new CommonException("该优惠券不适用于当前云服务器线路", new Object[0]);
               }
            }

            if (ProductTypeEnum.INDEPENDENT_SERVER.getCode().equals(productType) && StrUtil.isNotBlank(couponTemplate.getApplyIndependentLineIds())) {
               if (StrUtil.isBlank(lineId)) {
                  throw new CommonException("当前订单缺少独立服务器线路信息", new Object[0]);
               }

               if (!this.containsScopeValue(couponTemplate.getApplyIndependentLineIds(), lineId)) {
                  throw new CommonException("该优惠券不适用于当前独立服务器线路", new Object[0]);
               }
            }

            BigDecimal thresholdAmount = (BigDecimal)ObjectUtil.defaultIfNull(couponTemplate.getThresholdAmount(), BigDecimal.ZERO);
            if (thresholdAmount.compareTo(BigDecimal.ZERO) > 0 && orderAmount.compareTo(thresholdAmount) < 0) {
               throw new CommonException("未满足优惠券使用门槛", new Object[0]);
            }
         } else {
            throw new CommonException("优惠券剩余可用次数不足", new Object[0]);
         }
      }
   }

   private boolean containsScopeValue(String scopeValues, Object targetValue) {
      if (targetValue != null && !StrUtil.isBlank(scopeValues)) {
         String wrappedValue = "," + scopeValues + ",";
         return wrappedValue.contains("," + targetValue + ",");
      } else {
         return false;
      }
   }

   private CouponCalculateResponse buildCalculateResponse(MemberCoupon memberCoupon, CouponTemplate couponTemplate, BigDecimal orderAmount, Integer useCount) {
      BigDecimal couponPrice = this.calculateCouponPrice(couponTemplate, orderAmount);
      BigDecimal payPrice = orderAmount.subtract(couponPrice);
      if (payPrice.compareTo(BigDecimal.ZERO) < 0) {
         payPrice = BigDecimal.ZERO;
      }

      CouponCalculateResponse response = new CouponCalculateResponse();
      response.setMemberCouponId(memberCoupon.getId());
      response.setTemplateId(couponTemplate.getId());
      response.setCouponCode(memberCoupon.getCouponCode());
      response.setCouponPrice(couponPrice);
      response.setPayPrice(payPrice.setScale(2, RoundingMode.HALF_UP));
      response.setUseCount(useCount);
      response.setRemainingUseCount(memberCoupon.getRemainingUseCount());
      response.setCouponName(couponTemplate.getCouponName());
      return response;
   }

   private CouponCalculateResponse buildRelationResponse(CouponOrderRelation relation) {
      MemberCoupon memberCoupon = this.queryMemberCouponById(relation.getMemberCouponId());
      CouponCalculateResponse response = new CouponCalculateResponse();
      response.setMemberCouponId(relation.getMemberCouponId());
      response.setTemplateId(relation.getTemplateId());
      response.setCouponCode(relation.getCouponCode());
      response.setCouponPrice((BigDecimal)ObjectUtil.defaultIfNull(relation.getCouponPrice(), BigDecimal.ZERO));
      response.setPayPrice(BigDecimal.ZERO);
      response.setUseCount((Integer)ObjectUtil.defaultIfNull(relation.getUseCount(), 1));
      response.setRemainingUseCount(memberCoupon.getRemainingUseCount());
      response.setCouponName(relation.getTemplateName());
      return response;
   }

   private BigDecimal calculateCouponPrice(CouponTemplate couponTemplate, BigDecimal orderAmount) {
      BigDecimal couponPrice;
      if (CouponTypeEnum.CASH.getCode() != couponTemplate.getCouponType() && CouponTypeEnum.FULL_REDUCTION.getCode() != couponTemplate.getCouponType()) {
         if (CouponTypeEnum.DISCOUNT.getCode() != couponTemplate.getCouponType()) {
            throw new CommonException("不支持的优惠券类型", new Object[0]);
         }

         BigDecimal discountRate = (BigDecimal)ObjectUtil.defaultIfNull(couponTemplate.getDiscountRate(), BigDecimal.ONE);
         BigDecimal discountedAmount = orderAmount.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
         couponPrice = orderAmount.subtract(discountedAmount);
      } else {
         couponPrice = (BigDecimal)ObjectUtil.defaultIfNull(couponTemplate.getReduceAmount(), BigDecimal.ZERO);
      }

      BigDecimal maxDiscountAmount = couponTemplate.getMaxDiscountAmount();
      if (maxDiscountAmount != null && maxDiscountAmount.compareTo(BigDecimal.ZERO) > 0 && couponPrice.compareTo(maxDiscountAmount) > 0) {
         couponPrice = maxDiscountAmount;
      }

      if (couponPrice.compareTo(orderAmount) > 0) {
         couponPrice = orderAmount;
      }

      return couponPrice.setScale(2, RoundingMode.HALF_UP);
   }

   private int resolveRestoredStatus(MemberCoupon memberCoupon, Date now) {
      return memberCoupon.getExpireTime() != null && memberCoupon.getExpireTime().before(now)
         ? MemberCouponStatusEnum.EXPIRED.getCode()
         : MemberCouponStatusEnum.UNUSED.getCode();
   }

   private MemberCoupon queryMemberCouponByCode(String couponCode) {
      MemberCoupon memberCoupon = (MemberCoupon)this.memberCouponMapper
         .selectOne((Wrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getCouponCode, couponCode));
      if (memberCoupon == null) {
         throw new CommonException("优惠券不存在", new Object[0]);
      } else {
         return memberCoupon;
      }
   }

   private MemberCoupon queryMemberCouponById(String id) {
      MemberCoupon memberCoupon = (MemberCoupon)this.memberCouponMapper.selectById(id);
      if (memberCoupon == null) {
         throw new CommonException("会员优惠券不存在", new Object[0]);
      } else {
         return memberCoupon;
      }
   }

   private CouponTemplate queryCouponTemplate(String id) {
      CouponTemplate couponTemplate = (CouponTemplate)this.couponTemplateMapper.selectById(id);
      if (couponTemplate == null) {
         throw new CommonException("优惠券模板不存在", new Object[0]);
      } else {
         return couponTemplate;
      }
   }

   private CouponOrderRelation queryRelationByOrderNo(String orderNo) {
      return (CouponOrderRelation)this.couponOrderRelationMapper.selectOne((Wrapper<CouponOrderRelation>)new LambdaQueryWrapper<CouponOrderRelation>().eq(CouponOrderRelation::getOrderNo, orderNo));
   }

   private static class CouponContext {
      private MemberCoupon memberCoupon;
      private CouponTemplate couponTemplate;
      private BigDecimal orderAmount;
      private Integer useCount;
   }
}
