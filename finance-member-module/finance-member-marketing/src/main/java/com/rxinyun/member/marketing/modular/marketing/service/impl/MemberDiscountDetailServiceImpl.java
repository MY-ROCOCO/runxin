package com.rxinyun.member.marketing.modular.marketing.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.marketing.modular.marketing.entity.MemberDiscountDetail;
import com.rxinyun.member.marketing.modular.marketing.mapper.MemberDiscountDetailMapper;
import com.rxinyun.member.marketing.modular.marketing.service.MemberDiscountDetailService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberDiscountDetailServiceImpl extends ServiceImpl<MemberDiscountDetailMapper, MemberDiscountDetail> implements MemberDiscountDetailService {
   @Override
   public List<MemberDiscountDetail> listByTemplateId(String templateId) {
      return this.list((Wrapper<MemberDiscountDetail>)new LambdaQueryWrapper<MemberDiscountDetail>().eq(MemberDiscountDetail::getTemplateId, templateId));
   }

   @Override
   public BigDecimal queryDiscount(String groupId, String discountTemplateId) {
      MemberDiscountDetail memberDiscountDetail = (MemberDiscountDetail)this.getOne(
         (Wrapper<MemberDiscountDetail>)((LambdaQueryWrapper<MemberDiscountDetail>)new LambdaQueryWrapper<MemberDiscountDetail>().eq(MemberDiscountDetail::getTemplateId, discountTemplateId))
            .eq(MemberDiscountDetail::getGroupId, groupId)
      );
      return memberDiscountDetail == null ? BigDecimal.ONE : memberDiscountDetail.getDiscountValue();
   }
}
