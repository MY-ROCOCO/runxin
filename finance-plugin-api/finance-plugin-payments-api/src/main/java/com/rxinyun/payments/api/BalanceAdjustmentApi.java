package com.rxinyun.payments.api;

import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;

public interface BalanceAdjustmentApi {
   void adminAdjustAdd(PaymentsAdminBalancePram paymentsAdminAddBalancePram);
}
