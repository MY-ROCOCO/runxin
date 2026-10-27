package com.rxinyun.member.marketing.modular.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.marketing.modular.marketing.entity.MemberDiscountDetail;
import java.math.BigDecimal;
import java.util.List;

public interface MemberDiscountDetailService extends IService<MemberDiscountDetail> {
   List<MemberDiscountDetail> listByTemplateId(String templateId);

   BigDecimal queryDiscount(String groupId, String discountTemplateId);
}
