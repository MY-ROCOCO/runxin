package com.rxinyun.admin.business.modular.domain.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.domain.entity.DomainRegistrationRequest;
import com.rxinyun.admin.business.modular.domain.param.DomainRegistrationRequestQueryParam;

public interface DomainRegistrationRequestService {
   Page<DomainRegistrationRequest> page(DomainRegistrationRequestQueryParam param);
}
