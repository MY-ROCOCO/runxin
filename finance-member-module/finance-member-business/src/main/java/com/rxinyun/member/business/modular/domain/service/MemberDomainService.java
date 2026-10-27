package com.rxinyun.member.business.modular.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.member.business.modular.domain.param.MemberDomainPageParam;

public interface MemberDomainService {
   Page<BusinessDomain> page(MemberDomainPageParam pageParam);

   void updateRegistrationStatus(String memberId, String domainName, String registrationStatus);

   void updateRegistrationStatusForCurrentUser(String domainName, String registrationStatus);

   void assertOwnDomain(String domainName);
}
