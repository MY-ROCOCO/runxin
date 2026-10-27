package com.rxinyun.admin.member.modular.user.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.admin.member.modular.user.entity.AdminMemberBalance;
import com.rxinyun.admin.member.modular.user.service.AdminMemberBalanceService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.member.admin.api.BalanceApi;
import com.rxinyun.member.admin.pojo.BaseBalance;
import com.rxinyun.payments.api.PaymentsRecordApi;
import com.rxinyun.payments.api.pojo.PaymentsRecordAddParam;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BalanceApiProvider implements BalanceApi {
   @Resource
   private AdminMemberBalanceService adminMemberBalanceService;
   @Resource
   private PaymentsRecordApi paymentsRecordApi;

   public BaseBalance selectByMemberNo(Long memberNo) {
      AdminMemberBalance memberBalance = this.adminMemberBalanceService.selectByMemberNo(memberNo);
      return null == memberBalance ? null : (BaseBalance)BeanUtil.toBean(memberBalance, BaseBalance.class);
   }

   public void update(BaseBalance balance) {
      AdminMemberBalance memberBalance = (AdminMemberBalance)BeanUtil.toBean(balance, AdminMemberBalance.class);
      this.adminMemberBalanceService.update(memberBalance);
   }

   public void deductAndRecord(Long memberNo, BigDecimal amount, String orderNo, String businessType, String businessParams, Date payTime) {
      if (memberNo != null && amount != null && orderNo != null) {
         AdminMemberBalance memberBalance = this.adminMemberBalanceService.selectByMemberNo(memberNo);
         if (memberBalance != null) {
            memberBalance.setBalance(memberBalance.getBalance().subtract(amount));
            this.adminMemberBalanceService.update(memberBalance);
            PaymentsRecordAddParam addParam = new PaymentsRecordAddParam();
            addParam.setMemberNo(memberNo);
            addParam.setOrderNo(orderNo);
            addParam.setTradeNo(UUID.randomUUID().toString().replace("-", ""));
            addParam.setBusinessType(businessType);
            addParam.setPayTime(payTime == null ? new Date() : payTime);
            addParam.setPayAmount(amount);
            addParam.setPayType(4);
            addParam.setMemberBalanceAfter(memberBalance.getBalance());
            addParam.setRemark(businessType);
            addParam.setBusinessParams(businessParams);
            this.paymentsRecordApi.addOrderPaymentRecord(addParam);
         }
      }
   }

   public void creditAndRecord(Long memberNo, BigDecimal amount, String orderNo, String businessType, String businessParams, Date payTime, String remark) {
      if (memberNo != null && amount != null && orderNo != null) {
         AdminMemberBalance memberBalance = this.adminMemberBalanceService.selectByMemberNo(memberNo);
         if (memberBalance == null) {
            throw new CommonException("会员余额账户不存在，无法退款入账", new Object[0]);
         } else {
            memberBalance.setBalance(memberBalance.getBalance().add(amount));
            this.adminMemberBalanceService.update(memberBalance);
            PaymentsRecordAddParam addParam = new PaymentsRecordAddParam();
            addParam.setMemberNo(memberNo);
            addParam.setOrderNo(orderNo);
            addParam.setTradeNo(UUID.randomUUID().toString().replace("-", ""));
            addParam.setBusinessType(businessType);
            addParam.setPayTime(payTime == null ? new Date() : payTime);
            addParam.setPayAmount(amount);
            addParam.setPayType(4);
            addParam.setMemberBalanceAfter(memberBalance.getBalance());
            addParam.setRemark(StrUtil.blankToDefault(remark, businessType));
            addParam.setBusinessParams(businessParams);
            this.paymentsRecordApi.addOrderPaymentRecord(addParam);
         }
      } else {
         throw new CommonException("退款入账参数不完整", new Object[0]);
      }
   }
}
