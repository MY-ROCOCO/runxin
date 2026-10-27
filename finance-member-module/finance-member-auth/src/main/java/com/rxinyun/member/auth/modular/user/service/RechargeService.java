package com.rxinyun.member.auth.modular.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.auth.modular.user.entity.Recharge;
import com.rxinyun.member.auth.modular.user.param.RechargeAddParam;
import com.rxinyun.member.auth.modular.user.param.RechargeEditParam;
import com.rxinyun.member.auth.modular.user.param.RechargeIdParam;
import com.rxinyun.member.auth.modular.user.param.RechargePageParam;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface RechargeService {
   Page<Recharge> page(RechargePageParam rechargePageParam);

   Recharge detailForCurrentMember(RechargeIdParam rechargeIdParam);

   String add(RechargeAddParam rechargeAddParam);

   void edit(RechargeEditParam rechargeEditParam);

   Recharge detail(RechargeIdParam rechargeIdParam);

   Recharge queryEntity(String id);

   List<Recharge> listByStatusAndPayType(Integer status, Integer payType);

   List<Recharge> listByStatusAndPayTypeAndCreateTimeAfter(Integer status, Integer payType, Date afterTime);

   void updateExpectedUsdtAmount(String id, BigDecimal amountUsdt);

   int expireUnpaidRecharges(Date expireBefore);
}
