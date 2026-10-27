package com.rxinyun.order.api;

import com.rxinyun.order.core.pojo.BaseOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public interface OrderApi {
   Long OrderCountByCreateTimeStatus(HashMap<String, LocalDateTime> hashMap);

   BigDecimal OrderPayByCreateTime(HashMap<String, LocalDateTime> hashMap, Long userId);

   Long OrderCountByCreateTime(HashMap<String, LocalDateTime> hashMap);

   BigDecimal OrderPayByCreateTimeAndStatus(HashMap<String, LocalDateTime> hashMap);

   Long CountByPayStatus(HashMap<String, LocalDateTime> hashMap, int type);

   Long CountByPayStatus(HashMap<String, LocalDateTime> hashMap, int type, Long userId);

   List<BaseOrder> queryUnpaidOrder();

   void updateById(BaseOrder order);

   BaseOrder getByOrderNo(String orderNo);

   void updateExpectedUsdtAmount(String orderNo, BigDecimal amountUsdt);

   Long countPendingInvoice();

   BigDecimal sumPendingInvoiceAmount();

   Long countInvoicableOrders(Long memberNo);

   BigDecimal sumInvoicableOrderAmount(Long memberNo);
}
