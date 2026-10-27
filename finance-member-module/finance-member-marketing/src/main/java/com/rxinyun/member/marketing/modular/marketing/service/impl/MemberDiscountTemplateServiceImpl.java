package com.rxinyun.member.marketing.modular.marketing.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.marketing.modular.marketing.entity.MemberDiscountDetail;
import com.rxinyun.member.marketing.modular.marketing.entity.MemberDiscountTemplate;
import com.rxinyun.member.marketing.modular.marketing.mapper.MemberDiscountTemplateMapper;
import com.rxinyun.member.marketing.modular.marketing.param.MemberDiscountTemplatePageParam;
import com.rxinyun.member.marketing.modular.marketing.service.MemberDiscountDetailService;
import com.rxinyun.member.marketing.modular.marketing.service.MemberDiscountTemplateService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberDiscountTemplateServiceImpl
   extends ServiceImpl<MemberDiscountTemplateMapper, MemberDiscountTemplate>
   implements MemberDiscountTemplateService {
   @Resource
   private MemberDiscountDetailService discountDetailService;

   @Override
   public Page<MemberDiscountTemplate> page(MemberDiscountTemplatePageParam discountTemplatePageParam) {
      QueryWrapper<MemberDiscountTemplate> queryWrapper = new QueryWrapper();
      if (ObjectUtil.isNotEmpty(discountTemplatePageParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberDiscountTemplate::getTemplateName, discountTemplatePageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(discountTemplatePageParam.getStatus())) {
         queryWrapper.lambda().eq(MemberDiscountTemplate::getStatus, discountTemplatePageParam.getStatus());
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

      return (Page<MemberDiscountTemplate>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberDiscountTemplate detail(String id) {
      MemberDiscountTemplate discountTemplate = this.queryEntity(id);
      List<MemberDiscountDetail> discountDetailList = this.discountDetailService.listByTemplateId(id);
      return discountTemplate;
   }

   private MemberDiscountTemplate queryEntity(String id) {
      MemberDiscountTemplate discountTemplate = (MemberDiscountTemplate)this.getById(id);
      if (ObjectUtil.isEmpty(discountTemplate)) {
         throw new CommonException("折扣模板不存在，id值为：{}", new Object[]{id});
      } else {
         return discountTemplate;
      }
   }
}
