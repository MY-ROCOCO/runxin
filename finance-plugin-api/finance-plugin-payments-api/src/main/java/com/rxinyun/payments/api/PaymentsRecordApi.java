package com.rxinyun.payments.api;

import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;
import com.rxinyun.payments.api.pojo.PaymentsRecordAddParam;

public interface PaymentsRecordApi {
   void adminAdjustAddRecord(PaymentsAdminBalancePram paymentsAdminBalancePram, String adminId);

   void addOrderPaymentRecord(PaymentsRecordAddParam addParam);
}
