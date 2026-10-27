package com.rxinyun.business.api;

import java.util.List;

public interface DomainTaskApi {
   void markExpiredDomains();

   List<String> listCreateWaitingDomainNames();

   void updateRegistrationStatus(String domainName, String registrationStatus);
}
