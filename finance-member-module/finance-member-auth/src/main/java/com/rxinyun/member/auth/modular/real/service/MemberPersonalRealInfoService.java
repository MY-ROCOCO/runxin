package com.rxinyun.member.auth.modular.real.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.real.entity.PersonalRealInfo;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoAddOrUpdateParam;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoIdParam;
import com.rxinyun.member.auth.modular.real.param.PersonalRealInfoPageParam;
import java.util.List;

public interface MemberPersonalRealInfoService {
   Page<PersonalRealInfo> page(PersonalRealInfoPageParam pageParam);

   CommonResult addOrUpdate(PersonalRealInfoAddOrUpdateParam serverLocalLineAddParam);

   CommonResult updateByStatus(Long memberNo, String status);

   PersonalRealInfo detail(PersonalRealInfoIdParam serverLocalLineIdParam);

   PersonalRealInfo queryEntity(String id);

   void delete(List<PersonalRealInfoIdParam> personalRealInfoIdParamList);

   PersonalRealInfo seleteByMemberNo(Long id);
}
