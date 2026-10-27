package com.rxinyun.admin.payments.modular.recharge.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.payments.modular.recharge.entity.Recharge;
import com.rxinyun.admin.payments.modular.recharge.param.RechargeIdParam;
import com.rxinyun.admin.payments.modular.recharge.param.RechargePageParam;

public interface RechargeRecordsService {
   Page<Recharge> page(RechargePageParam rechargePageParam);

   Recharge detail(RechargeIdParam rechargeIdParam);

   Recharge queryEntity(String id);
}
