package com.rxinyun.business.api;

import java.time.LocalDateTime;

public interface BusinessDomainApi {
   void addRegisteredDomain(String memberId, String domainName, String tld, int years, String templateId, String registrar);

   Long getDomainCountByCreateTime(LocalDateTime startTime, LocalDateTime endTime);
}
