package com.rxinyun.admin.payments.modular.payments.constant;

import com.rxinyun.common.util.TimeUtil;
import java.math.BigDecimal;

public class PayRecordConstant {
   public static final Integer AddBalance = 6;
   public static final Integer SubBalance = 7;

   public static String getOrderId() {
      return "BZ" + TimeUtil.now();
   }

   public static final String get(Boolean isAdd, String adminId, BigDecimal payAmount) {
      return isAdd ? "财务调整，管理员ID: " + adminId + " 。增加余额为: " + payAmount : "财务调整，管理员ID: " + adminId + " 。扣减余额为: " + payAmount;
   }
}
