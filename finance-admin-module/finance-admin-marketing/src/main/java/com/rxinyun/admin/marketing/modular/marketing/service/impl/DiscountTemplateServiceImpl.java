package com.rxinyun.admin.marketing.modular.marketing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountDetail;
import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountTemplate;
import com.rxinyun.admin.marketing.modular.marketing.mapper.DiscountTemplateMapper;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplateAddParam;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplateEditParam;
import com.rxinyun.admin.marketing.modular.marketing.param.DiscountTemplatePageParam;
import com.rxinyun.admin.marketing.modular.marketing.service.DiscountDetailService;
import com.rxinyun.admin.marketing.modular.marketing.service.DiscountTemplateService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscountTemplateServiceImpl extends ServiceImpl<DiscountTemplateMapper, DiscountTemplate> implements DiscountTemplateService {
   @Resource
   private DiscountDetailService discountDetailService;

   @Override
   public Page<DiscountTemplate> page(DiscountTemplatePageParam discountTemplatePageParam) {
      QueryWrapper<DiscountTemplate> queryWrapper = new QueryWrapper();
      if (ObjectUtil.isNotEmpty(discountTemplatePageParam.getSearchKey())) {
         queryWrapper.lambda().like(DiscountTemplate::getTemplateName, discountTemplatePageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(discountTemplatePageParam.getStatus())) {
         queryWrapper.lambda().eq(DiscountTemplate::getStatus, discountTemplatePageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{discountTemplatePageParam.getSortField(), discountTemplatePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(discountTemplatePageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            discountTemplatePageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(discountTemplatePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<DiscountTemplate>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(DiscountTemplateAddParam discountTemplateAddParam) {
      LambdaQueryWrapper<DiscountTemplate> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.eq(DiscountTemplate::getTemplateName, discountTemplateAddParam.getTemplateName());
      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("折扣模板名称已存在，请重新输入", new Object[0]);
      } else {
         DiscountTemplate discountTemplate = new DiscountTemplate();
         BeanUtil.copyProperties(discountTemplateAddParam, discountTemplate, new String[0]);
         this.save(discountTemplate);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(DiscountTemplateEditParam discountTemplateEditParam) {
      LambdaQueryWrapper<DiscountTemplate> queryWrapper = new LambdaQueryWrapper();
      queryWrapper.eq(DiscountTemplate::getTemplateName, discountTemplateEditParam.getTemplateName());
      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("折扣模板名称已存在，请重新输入", new Object[0]);
      } else {
         DiscountTemplate discountTemplate = this.queryEntity(discountTemplateEditParam.getId());
         BeanUtil.copyProperties(discountTemplateEditParam, discountTemplate, new String[0]);
         this.updateById(discountTemplate);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<String> ids) {
      this.removeByIds(ids);
      ids.forEach(id -> this.discountDetailService.deleteByTemplateId(id));
   }

   @Override
   public DiscountTemplate detail(String id) {
      DiscountTemplate discountTemplate = this.queryEntity(id);
      List<DiscountDetail> discountDetailList = this.discountDetailService.listByTemplateId(id);
      return discountTemplate;
   }

   private DiscountTemplate queryEntity(String id) {
      DiscountTemplate discountTemplate = (DiscountTemplate)this.getById(id);
      if (ObjectUtil.isEmpty(discountTemplate)) {
         throw new CommonException("折扣模板不存在，id值为：{}", new Object[]{id});
      } else {
         return discountTemplate;
      }
   }
}
