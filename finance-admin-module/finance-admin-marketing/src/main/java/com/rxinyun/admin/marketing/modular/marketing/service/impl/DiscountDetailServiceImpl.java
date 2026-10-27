package com.rxinyun.admin.marketing.modular.marketing.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.marketing.modular.marketing.entity.DiscountDetail;
import com.rxinyun.admin.marketing.modular.marketing.mapper.DiscountDetailMapper;
import com.rxinyun.admin.marketing.modular.marketing.service.DiscountDetailService;
import com.rxinyun.member.member.api.MemberGroupsApi;
import com.rxinyun.member.member.pojo.BaseMemberGroups;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DiscountDetailServiceImpl extends ServiceImpl<DiscountDetailMapper, DiscountDetail> implements DiscountDetailService {
   @Resource
   private MemberGroupsApi memberGroupsApi;

   @Override
   public List<DiscountDetail> listByTemplateId(String templateId) {
      List<BaseMemberGroups> memberGroups = this.memberGroupsApi.list();
      if (CollectionUtil.isEmpty(memberGroups)) {
         return new ArrayList<>();
      } else {
         List<DiscountDetail> historyDiscountDetails = this.list((Wrapper<DiscountDetail>)new LambdaQueryWrapper<DiscountDetail>().eq(DiscountDetail::getTemplateId, templateId));
         Map<String, DiscountDetail> historyDiscountDetailMap = historyDiscountDetails.stream()
            .collect(Collectors.toMap(DiscountDetail::getGroupId, Function.identity(), (existing, replacement) -> existing));
         return memberGroups.stream().map(memberGroup -> {
            DiscountDetail discountDetail = new DiscountDetail();
            DiscountDetail historyDiscountDetail = historyDiscountDetailMap.get(memberGroup.getId());
            discountDetail.setGroupId(memberGroup.getId());
            discountDetail.setGroupName(memberGroup.getGroupName());
            discountDetail.setTemplateId(templateId);
            discountDetail.setDiscountValue(historyDiscountDetail != null ? historyDiscountDetail.getDiscountValue() : BigDecimal.ONE);
            return discountDetail;
         }).collect(Collectors.toList());
      }
   }

   @Override
   public void batchAdd(List<DiscountDetail> discountDetails) {
      this.remove((Wrapper<DiscountDetail>)new LambdaQueryWrapper<DiscountDetail>().eq(DiscountDetail::getTemplateId, discountDetails.get(0).getTemplateId()));
      this.saveBatch(discountDetails);
   }

   @Override
   public void deleteByTemplateId(String templateId) {
      this.remove((Wrapper<DiscountDetail>)new LambdaQueryWrapper<DiscountDetail>().eq(DiscountDetail::getTemplateId, templateId));
   }
}
