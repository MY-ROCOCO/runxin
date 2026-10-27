package com.rxinyun.admin.business.modular.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.admin.business.modular.domain.param.DomainPageParam;

public interface BusinessDomainService {
   Page<BusinessDomain> page(DomainPageParam pageParam);

   void updateRegistrationStatus(String domainName, String registrationStatus);
}
