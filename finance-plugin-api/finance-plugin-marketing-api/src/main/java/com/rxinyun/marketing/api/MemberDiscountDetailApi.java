package com.rxinyun.marketing.api;

import java.math.BigDecimal;

public interface MemberDiscountDetailApi {
   BigDecimal queryDiscount(String groupId, String discountTemplateId);
}
