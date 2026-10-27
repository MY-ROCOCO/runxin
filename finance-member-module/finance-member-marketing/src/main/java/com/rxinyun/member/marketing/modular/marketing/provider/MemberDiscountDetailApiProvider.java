package com.rxinyun.member.marketing.modular.marketing.provider;

import com.rxinyun.marketing.api.MemberDiscountDetailApi;
import com.rxinyun.member.marketing.modular.marketing.service.MemberDiscountDetailService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberDiscountDetailApiProvider implements MemberDiscountDetailApi {
   private static final Logger log = LoggerFactory.getLogger(MemberDiscountDetailApiProvider.class);
   @Resource
   private MemberDiscountDetailService memberDiscountDetailService;

   public BigDecimal queryDiscount(String groupId, String discountTemplateId) {
      return this.memberDiscountDetailService.queryDiscount(groupId, discountTemplateId);
   }
}
