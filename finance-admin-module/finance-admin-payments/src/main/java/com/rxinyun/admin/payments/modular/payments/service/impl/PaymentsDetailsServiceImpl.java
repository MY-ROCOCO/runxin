package com.rxinyun.admin.payments.modular.payments.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.payments.modular.payments.constant.PayRecordConstant;
import com.rxinyun.admin.payments.modular.payments.entity.PaymentsRecord;
import com.rxinyun.admin.payments.modular.payments.mapper.PaymentsDetailsMapper;
import com.rxinyun.admin.payments.modular.payments.param.PaymentsRecordPageParam;
import com.rxinyun.admin.payments.modular.payments.service.PaymentsDetailsService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.payments.api.PaymentsRecordApi;
import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;
import com.rxinyun.payments.api.pojo.PaymentsRecordAddParam;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentsDetailsServiceImpl extends ServiceImpl<PaymentsDetailsMapper, PaymentsRecord> implements PaymentsDetailsService, PaymentsRecordApi {
   private static final Logger log = LoggerFactory.getLogger(PaymentsDetailsServiceImpl.class);
   @Resource
   private MemberSaBaseLoginUserApi memberSaBaseLoginUserApi;
   @Resource
   private BalanceApi balanceApi;

   @Override
   public Page<PaymentsRecord> page(PaymentsRecordPageParam param) {
      QueryWrapper<PaymentsRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(param.getMemberNo())) {
         queryWrapper.lambda().eq(PaymentsRecord::getMemberNo, param.getMemberNo());
      }

      if (ObjectUtil.isNotEmpty(param.getPayType())) {
         queryWrapper.lambda().eq(PaymentsRecord::getPayType, param.getPayType());
      }

      if (ObjectUtil.isNotEmpty(param.getOrderNo())) {
         queryWrapper.lambda().eq(PaymentsRecord::getOrderNo, param.getOrderNo());
      }

      if (ObjectUtil.isNotEmpty(param.getTradeNo())) {
         queryWrapper.lambda().eq(PaymentsRecord::getTradeNo, param.getTradeNo());
      }

      queryWrapper.lambda().orderByDesc(PaymentsRecord::getCreateTime);
      return (Page<PaymentsRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void adminAdjustAddRecord(PaymentsAdminBalancePram paymentsAdminBalancePram, String adminId) {
      SaBaseClientLoginUser saBaseClientLoginUser = this.memberSaBaseLoginUserApi.getClientUserById(Long.valueOf(paymentsAdminBalancePram.getMemberNo()));
      if (saBaseClientLoginUser == null) {
         throw new CommonException("该会员不存在", new Object[0]);
      } else if (!paymentsAdminBalancePram.getMemberAccount().equals(saBaseClientLoginUser.getAccount())) {
         throw new CommonException("要增加的会员账号与会员实际账号不匹配，请核对", new Object[0]);
      } else if (paymentsAdminBalancePram.getPayAmount().longValue() < 0L) {
         throw new CommonException("要调整的余额不能小于等于0", new Object[0]);
      } else {
         PaymentsRecord paymentsRecord = (PaymentsRecord)BeanUtil.toBean(paymentsAdminBalancePram, PaymentsRecord.class);
         paymentsRecord.setMemberNo(saBaseClientLoginUser.getId().toString());
         paymentsRecord.setOrderNo(PayRecordConstant.getOrderId());
         paymentsRecord.setTradeNo(paymentsRecord.getOrderNo());
         paymentsRecord.setBusinessType(PayRecordConstant.get(paymentsAdminBalancePram.getIsAdd() == 1, adminId, paymentsRecord.getPayAmount()));
         paymentsRecord.setPayTime(Date.from(Instant.now()));
         paymentsRecord.setCreateTime(Date.from(Instant.now()));
         if (paymentsAdminBalancePram.getIsAdd() == 1) {
            paymentsRecord.setPayType(PayRecordConstant.AddBalance);
         } else if (paymentsAdminBalancePram.getIsAdd() == 0) {
            paymentsRecord.setPayType(PayRecordConstant.SubBalance);
         }

         BaseBalance bal = this.balanceApi.selectByMemberNo(Long.valueOf(paymentsAdminBalancePram.getMemberNo()));
         paymentsRecord.setMemberBalanceAfter(bal != null ? bal.getBalance() : null);
         this.save(paymentsRecord);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void addOrderPaymentRecord(PaymentsRecordAddParam addParam) {
      if (addParam != null && addParam.getMemberNo() != null && addParam.getOrderNo() != null && addParam.getPayAmount() != null) {
         PaymentsRecord paymentsRecord = new PaymentsRecord();
         paymentsRecord.setMemberNo(String.valueOf(addParam.getMemberNo()));
         paymentsRecord.setOrderNo(addParam.getOrderNo());
         paymentsRecord.setTradeNo(addParam.getTradeNo() == null ? UUID.randomUUID().toString().replace("-", "") : addParam.getTradeNo());
         paymentsRecord.setBusinessType(addParam.getBusinessType());
         paymentsRecord.setPayTime(addParam.getPayTime() == null ? new Date() : addParam.getPayTime());
         paymentsRecord.setPayAmount(addParam.getPayAmount());
         paymentsRecord.setPayType(addParam.getPayType());
         paymentsRecord.setRemark(addParam.getRemark());
         paymentsRecord.setBusinessParams(addParam.getBusinessParams());
         paymentsRecord.setMemberBalanceAfter(addParam.getMemberBalanceAfter());
         paymentsRecord.setCreateTime(new Date());
         this.save(paymentsRecord);
      } else {
         throw new CommonException("收支明细参数不完整", new Object[0]);
      }
   }
}
