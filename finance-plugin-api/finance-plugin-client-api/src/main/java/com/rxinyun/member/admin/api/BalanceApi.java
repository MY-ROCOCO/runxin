package com.rxinyun.member.admin.api;

import com.rxinyun.member.admin.pojo.BaseBalance;
import java.math.BigDecimal;
import java.util.Date;

public interface BalanceApi {
   BaseBalance selectByMemberNo(Long memberNo);

   void update(BaseBalance balance);

   void deductAndRecord(Long memberNo, BigDecimal amount, String orderNo, String businessType, String businessParams, Date payTime);

   default void creditAndRecord(Long memberNo, BigDecimal amount, String orderNo, String businessType, String businessParams, Date payTime) {
      this.creditAndRecord(memberNo, amount, orderNo, businessType, businessParams, payTime, null);
   }

   void creditAndRecord(Long memberNo, BigDecimal amount, String orderNo, String businessType, String businessParams, Date payTime, String remark);
}
