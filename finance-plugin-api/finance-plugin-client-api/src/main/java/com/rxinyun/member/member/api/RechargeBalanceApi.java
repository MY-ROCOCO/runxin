package com.rxinyun.member.member.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

public interface RechargeBalanceApi {
   BigDecimal getRechargeMoneyByDate(HashMap<String, LocalDateTime> map, Long userId);

   int expireUnpaidRecharges(Date expireBefore);
}
