package com.rxinyun.admin.payments.modular.payments.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.admin.payments.modular.payments.param.PaymentsRecordPageParam;

public interface PaymentsDetailsService {
   Page<PaymentsRecord> page(PaymentsRecordPageParam param);
}
