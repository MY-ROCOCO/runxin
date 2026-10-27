package com.rxinyun.member.order.modular.domain.service.impl;

import com.rxinyun.member.order.modular.domain.entity.DomainRegistrationRequest;
import com.rxinyun.member.order.modular.domain.mapper.DomainRegistrationRequestMapper;
import com.rxinyun.member.order.modular.domain.service.DomainRegistrationRequestService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service("memberDomainRegistrationRequestService")
public class DomainRegistrationRequestServiceImpl implements DomainRegistrationRequestService {
   public static final int STATUS_PENDING = 0;
   public static final int STATUS_SUCCESS = 1;
   public static final int STATUS_FAILED = 2;
   @Resource
   private DomainRegistrationRequestMapper domainRegistrationRequestMapper;

   @Override
   public void save(DomainRegistrationRequest request) {
      this.domainRegistrationRequestMapper.insert(request);
   }
}
