package com.rxinyun.member.marketing.modular.marketing.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.marketing.modular.marketing.entity.MemberDiscountTemplate;
import com.rxinyun.member.marketing.modular.marketing.param.MemberDiscountTemplatePageParam;

public interface MemberDiscountTemplateService extends IService<MemberDiscountTemplate> {
   Page<MemberDiscountTemplate> page(MemberDiscountTemplatePageParam discountTemplatePageParam);

   MemberDiscountTemplate detail(String id);
}
