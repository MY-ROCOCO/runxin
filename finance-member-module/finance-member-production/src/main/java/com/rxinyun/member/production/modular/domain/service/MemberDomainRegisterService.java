package com.rxinyun.member.production.modular.domain.service;

import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient;

public interface MemberDomainRegisterService {
   XinwangDomainApiClient.DomainCreateResult registerDomain(String domainName, String templateId, int years);
}
