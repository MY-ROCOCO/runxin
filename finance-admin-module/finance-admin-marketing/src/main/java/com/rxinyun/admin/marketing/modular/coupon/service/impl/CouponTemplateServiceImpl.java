package com.rxinyun.admin.marketing.modular.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.marketing.modular.coupon.entity.CouponTemplate;
import com.rxinyun.admin.marketing.modular.coupon.entity.MemberCoupon;
import com.rxinyun.admin.marketing.modular.coupon.mapper.AdminCouponTemplateMapper;
import com.rxinyun.admin.marketing.modular.coupon.mapper.AdminMemberCouponMapper;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplateAddParam;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplateEditParam;
import com.rxinyun.admin.marketing.modular.coupon.param.CouponTemplatePageParam;
import com.rxinyun.admin.marketing.modular.coupon.service.CouponTemplateService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CouponTemplateServiceImpl extends ServiceImpl<AdminCouponTemplateMapper, CouponTemplate> implements CouponTemplateService {
   @Resource
   private AdminMemberCouponMapper adminMemberCouponMapper;

   @Override
   public Page<CouponTemplate> page(CouponTemplatePageParam pageParam) {
      QueryWrapper<CouponTemplate> queryWrapper = new QueryWrapper();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(CouponTemplate::getCouponName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().eq(CouponTemplate::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getClaimEnabled())) {
         queryWrapper.lambda().eq(CouponTemplate::getClaimEnabled, pageParam.getClaimEnabled());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<CouponTemplate>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(CouponTemplateAddParam addParam) {
      this.validateCouponTemplate(
         null,
         addParam.getCouponName(),
         addParam.getIssueTotalCount(),
         addParam.getUseLimit(),
         addParam.getApplyProductTypes(),
         addParam.getApplyCloudLineIds(),
         addParam.getApplyIndependentLineIds(),
         addParam.getApplyOrderTypes()
      );
      CouponTemplate couponTemplate = (CouponTemplate)BeanUtil.toBean(addParam, CouponTemplate.class);
      couponTemplate.setClaimEnabled((Integer)ObjectUtil.defaultIfNull(addParam.getClaimEnabled(), 0));
      couponTemplate.setIssuedCount(0);
      this.save(couponTemplate);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(CouponTemplateEditParam editParam) {
      CouponTemplate couponTemplate = this.queryEntity(editParam.getId());
      this.validateCouponTemplate(
         editParam.getId(),
         editParam.getCouponName(),
         editParam.getIssueTotalCount(),
         editParam.getUseLimit(),
         editParam.getApplyProductTypes(),
         editParam.getApplyCloudLineIds(),
         editParam.getApplyIndependentLineIds(),
         editParam.getApplyOrderTypes()
      );
      if (ObjectUtil.isNotEmpty(editParam.getIssueTotalCount())
         && editParam.getIssueTotalCount() > 0
         && (Integer)ObjectUtil.defaultIfNull(couponTemplate.getIssuedCount(), 0) > editParam.getIssueTotalCount()) {
         throw new CommonException("计划发放总量不能小于已发放数量", new Object[0]);
      } else {
         BeanUtil.copyProperties(editParam, couponTemplate, new String[0]);
         this.updateById(couponTemplate);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<String> ids) {
      if (!ObjectUtil.isEmpty(ids)) {
         for (String id : ids) {
            long issuedCount = this.adminMemberCouponMapper.selectCount((Wrapper<MemberCoupon>)new LambdaQueryWrapper<MemberCoupon>().eq(MemberCoupon::getTemplateId, id));
            if (issuedCount > 0L) {
               throw new CommonException("模板已发券，不能删除", new Object[0]);
            }
         }

         this.removeByIds(ids);
      }
   }

   @Override
   public CouponTemplate detail(String id) {
      return this.queryEntity(id);
   }

   private void validateCouponTemplate(
      String id,
      String couponName,
      Integer issueTotalCount,
      Integer useLimit,
      String applyProductTypes,
      String applyCloudLineIds,
      String applyIndependentLineIds,
      String applyOrderTypes
   ) {
      if (StrUtil.isBlank(couponName)) {
         throw new CommonException("优惠券名称不能为空", new Object[0]);
      } else if (ObjectUtil.isEmpty(useLimit) || useLimit <= 0) {
         throw new CommonException("单张券可使用次数必须大于0", new Object[0]);
      } else if (ObjectUtil.isNotEmpty(issueTotalCount) && issueTotalCount < 0) {
         throw new CommonException("计划发放总量不能小于0", new Object[0]);
      } else {
         this.validateApplyScopes(applyProductTypes, applyCloudLineIds, applyIndependentLineIds, applyOrderTypes);
         long count = this.count(
            ((LambdaQueryWrapper<CouponTemplate>)new LambdaQueryWrapper<CouponTemplate>().eq(CouponTemplate::getCouponName, couponName)).ne(StrUtil.isNotBlank(id), CouponTemplate::getId, id)
         );
         if (count > 0L) {
            throw new CommonException("优惠券名称已存在", new Object[0]);
         }
      }
   }

   private void validateApplyScopes(String applyProductTypes, String applyCloudLineIds, String applyIndependentLineIds, String applyOrderTypes) {
      if (StrUtil.isBlank(applyOrderTypes)) {
         throw new CommonException("适用订单类型不能为空", new Object[0]);
      } else if (this.containsScopeValue(applyProductTypes, ProductTypeEnum.CLOUD_SERVER.getCode()) && StrUtil.isBlank(applyCloudLineIds)) {
         throw new CommonException("云服务器优惠券模板必须绑定云服务器线路", new Object[0]);
      } else if (this.containsScopeValue(applyProductTypes, ProductTypeEnum.INDEPENDENT_SERVER.getCode()) && StrUtil.isBlank(applyIndependentLineIds)) {
         throw new CommonException("独立服务器优惠券模板必须绑定独立服务器线路", new Object[0]);
      }
   }

   private boolean containsScopeValue(String scopeValues, Integer targetValue) {
      if (targetValue != null && !StrUtil.isBlank(scopeValues)) {
         String wrappedValue = "," + scopeValues + ",";
         return wrappedValue.contains("," + targetValue + ",");
      } else {
         return false;
      }
   }

   private CouponTemplate queryEntity(String id) {
      CouponTemplate couponTemplate = (CouponTemplate)this.getById(id);
      if (couponTemplate == null) {
         throw new CommonException("优惠券模板不存在", new Object[0]);
      } else {
         return couponTemplate;
      }
   }
}
