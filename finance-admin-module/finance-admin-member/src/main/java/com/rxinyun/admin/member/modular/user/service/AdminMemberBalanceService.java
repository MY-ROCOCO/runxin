package com.rxinyun.admin.member.modular.user.service;

import com.rxinyun.admin.member.modular.user.entity.AdminMemberBalance;

public interface AdminMemberBalanceService {
   AdminMemberBalance selectByMemberNo(Long memberNo);

   void update(AdminMemberBalance memberBalance);

   void initializeUserBalance(Long memberNo);
}
