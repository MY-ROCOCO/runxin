package com.rxinyun.member.auth.modular.real.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.real.entity.CompanyRealInfo;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoAddOrUpdateParam;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoIdParam;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoPageParam;
import java.util.List;

public interface MemberCompanyRealInfoService {
   Page<CompanyRealInfo> page(CompanyRealInfoPageParam pageParam);

   CommonResult addOrUpdate(CompanyRealInfoAddOrUpdateParam companyRealInfoAddParam);

   CompanyRealInfo detail(CompanyRealInfoIdParam serverLocalLineIdParam);

   CompanyRealInfo queryEntity(String id);

   void delete(List<CompanyRealInfoIdParam> serverLocalLineIdParamList);

   CommonResult updateByStatus(Long id, String key);

   CommonResult updateByStatus(Long memberNo, String status, String reason);

   CompanyRealInfo seleteByMemberNo(Long memberNo);
}
