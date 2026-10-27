package com.rxinyun.admin.order.modular.ecs.refund;

import com.rxinyun.common.enums.order.ServerRefundModeEnum;
import com.rxinyun.common.exception.CommonException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ServerRefundCalculator {
   private ServerRefundCalculator() {
   }

   public static BigDecimal computeRefundAmount(BigDecimal sumPayPrice, ServerRefundModeEnum mode, long totalMillis, long leftMillis) {
      BigDecimal s = sumPayPrice == null ? BigDecimal.ZERO : sumPayPrice;
      if (s.compareTo(BigDecimal.ZERO) <= 0) {
         return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
      } else if (mode == ServerRefundModeEnum.FULL) {
         return s.setScale(2, RoundingMode.HALF_UP);
      } else if (totalMillis <= 0L) {
         throw new CommonException("服务总时长无效，无法按剩余比例退款", new Object[0]);
      } else if (leftMillis <= 0L) {
         return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
      } else {
         BigDecimal ratio = BigDecimal.valueOf(leftMillis).divide(BigDecimal.valueOf(totalMillis), 8, RoundingMode.HALF_UP);
         return s.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
      }
   }
}
