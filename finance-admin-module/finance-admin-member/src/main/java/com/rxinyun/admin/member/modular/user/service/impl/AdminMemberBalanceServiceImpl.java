package com.rxinyun.admin.member.modular.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.user.entity.AdminMemberBalance;
import com.rxinyun.admin.member.modular.user.mapper.AdminMemberBalanceMapper;
import com.rxinyun.admin.member.modular.user.service.AdminMemberBalanceService;
import java.math.BigDecimal;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminMemberBalanceServiceImpl extends ServiceImpl<AdminMemberBalanceMapper, AdminMemberBalance> implements AdminMemberBalanceService {
   private static final Logger log = LoggerFactory.getLogger(AdminMemberBalanceServiceImpl.class);

   @Override
   public AdminMemberBalance selectByMemberNo(Long memberNo) {
      return (AdminMemberBalance)this.getOne((Wrapper<AdminMemberBalance>)new LambdaQueryWrapper<AdminMemberBalance>().eq(AdminMemberBalance::getMemberNo, memberNo));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(AdminMemberBalance memberBalance) {
      this.updateById(memberBalance);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void initializeUserBalance(Long memberNo) {
      AdminMemberBalance memberBalance = new AdminMemberBalance();
      memberBalance.setMemberNo(memberNo);
      memberBalance.setBalance(BigDecimal.ZERO);
      Date now = new Date();
      memberBalance.setCreateTime(now);
      memberBalance.setUpdateTime(now);
      this.save(memberBalance);
   }
}
