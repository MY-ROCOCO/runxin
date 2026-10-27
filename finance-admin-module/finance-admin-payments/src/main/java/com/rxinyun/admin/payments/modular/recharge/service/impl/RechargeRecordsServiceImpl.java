package com.rxinyun.admin.payments.modular.recharge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.payments.modular.recharge.entity.Recharge;
import com.rxinyun.admin.payments.modular.recharge.mapper.RechargeRecordsMapper;
import com.rxinyun.admin.payments.modular.recharge.param.RechargeIdParam;
import com.rxinyun.admin.payments.modular.recharge.param.RechargePageParam;
import com.rxinyun.admin.payments.modular.recharge.service.RechargeRecordsService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RechargeRecordsServiceImpl extends ServiceImpl<RechargeRecordsMapper, Recharge> implements RechargeRecordsService {
   private static final Logger log = LoggerFactory.getLogger(RechargeRecordsServiceImpl.class);
   @Autowired
   private RechargeRecordsMapper rechargeRecordsMapper;

   @Override
   public Page<Recharge> page(RechargePageParam rechargePageParam) {
      QueryWrapper<Recharge> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(rechargePageParam.getMemberNo())) {
         queryWrapper.lambda().eq(Recharge::getMemberNo, rechargePageParam.getMemberNo());
      }

      if (ObjectUtil.isNotEmpty(rechargePageParam.getStatus())) {
         queryWrapper.lambda().eq(Recharge::getStatus, rechargePageParam.getStatus());
      }

      if (ObjectUtil.isNotEmpty(rechargePageParam.getPayType())) {
         queryWrapper.lambda().eq(Recharge::getPayType, rechargePageParam.getPayType());
      }

      queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      return (Page<Recharge>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public Recharge detail(RechargeIdParam rechargeIdParam) {
      return this.queryEntity(rechargeIdParam.getId());
   }

   @Override
   public Recharge queryEntity(String id) {
      Recharge recharge = (Recharge)this.getById(id);
      if (ObjectUtil.isEmpty(recharge)) {
         throw new CommonException("充值记录不存在，id值为：{}", new Object[]{id});
      } else {
         return recharge;
      }
   }
}
