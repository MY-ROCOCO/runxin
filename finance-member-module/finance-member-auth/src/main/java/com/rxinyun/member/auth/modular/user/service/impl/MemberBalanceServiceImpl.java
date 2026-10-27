package com.rxinyun.member.auth.modular.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.auth.modular.user.entity.MemberBalance;
import com.rxinyun.member.auth.modular.user.mapper.MemberBalanceMapper;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import java.math.BigDecimal;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberBalanceServiceImpl extends ServiceImpl<MemberBalanceMapper, MemberBalance> implements MemberBalanceService {
   private static final Logger log = LoggerFactory.getLogger(MemberBalanceServiceImpl.class);

   @Override
   public void initializeUserBalance(Long memberNo) {
      MemberBalance memberBalance = new MemberBalance();
      memberBalance.setMemberNo(memberNo);
      memberBalance.setBalance(BigDecimal.ZERO);
      memberBalance.setCreateTime(new Date());
      memberBalance.setCreateTime(new Date());
      this.save(memberBalance);
   }

   @Override
   public MemberBalance selectByMemberNo(Long memberNo) {
      return (MemberBalance)this.getOne((Wrapper<MemberBalance>)new LambdaQueryWrapper<MemberBalance>().eq(MemberBalance::getMemberNo, memberNo));
   }

   @Override
   public void edit(MemberBalance memberBalance) {
      this.updateById(memberBalance);
   }

   @Override
   public MemberBalance select() {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      return (MemberBalance)this.getOne((Wrapper<MemberBalance>)new LambdaQueryWrapper<MemberBalance>().eq(MemberBalance::getMemberNo, clientLoginUser.getId()));
   }
}
