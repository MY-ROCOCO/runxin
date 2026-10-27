package com.rxinyun.admin.member.modular.real.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.real.entity.CompanyRealInfo;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoIdParam;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoPageParam;

public interface CompanyRealInfoService {
   Page<CompanyRealInfo> page(CompanyRealInfoPageParam pageParam);

   CompanyRealInfo detail(CompanyRealInfoIdParam serverLocalLineIdParam);

   CompanyRealInfo queryEntity(String id);

   CompanyRealInfo seleteByMemberNo(Long memberNo);
}
