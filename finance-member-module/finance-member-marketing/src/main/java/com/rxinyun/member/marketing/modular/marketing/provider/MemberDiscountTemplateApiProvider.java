package com.rxinyun.member.marketing.modular.marketing.provider;

import com.rxinyun.marketing.api.MemberDiscountTemplateApi;
import com.rxinyun.member.marketing.modular.marketing.entity.MemberDiscountTemplate;
import com.rxinyun.member.marketing.modular.marketing.service.MemberDiscountTemplateService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberDiscountTemplateApiProvider implements MemberDiscountTemplateApi {
   private static final Logger log = LoggerFactory.getLogger(MemberDiscountTemplateApiProvider.class);
   @Resource
   private MemberDiscountTemplateService memberDiscountTemplateService;

   public String selectNameById(String discountTemplateId) {
      MemberDiscountTemplate memberDiscountTemplate = (MemberDiscountTemplate)this.memberDiscountTemplateService.getById(discountTemplateId);
      return memberDiscountTemplate == null ? null : memberDiscountTemplate.getTemplateName();
   }
}
