package com.rxinyun.member.production.modular.domain.service;

import com.rxinyun.member.production.modular.domain.dto.DomainCheckResultVO;
import com.rxinyun.member.production.modular.domain.param.DomainCheckParam;

public interface MemberDomainCheckService {
   DomainCheckResultVO checkRegister(DomainCheckParam param);
}
