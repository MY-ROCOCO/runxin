package com.rxinyun.member.auth.modular.user.service;

import com.rxinyun.member.auth.modular.user.entity.MemberBalance;

public interface MemberBalanceService {
   void initializeUserBalance(Long memberNo);

   MemberBalance selectByMemberNo(Long memberNo);

   void edit(MemberBalance memberBalance);

   MemberBalance select();
}
