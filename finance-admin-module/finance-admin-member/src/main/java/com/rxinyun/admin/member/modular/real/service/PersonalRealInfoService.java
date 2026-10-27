package com.rxinyun.admin.member.modular.real.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.real.entity.PersonalRealInfo;
import com.rxinyun.admin.member.modular.real.param.PersonalRealInfoIdParam;
import com.rxinyun.admin.member.modular.real.param.PersonalRealInfoPageParam;

public interface PersonalRealInfoService {
   Page<PersonalRealInfo> page(PersonalRealInfoPageParam pageParam);

   PersonalRealInfo detail(PersonalRealInfoIdParam serverLocalLineIdParam);

   PersonalRealInfo queryEntity(String id);

   PersonalRealInfo seleteByMemberNo(Long id);
}
