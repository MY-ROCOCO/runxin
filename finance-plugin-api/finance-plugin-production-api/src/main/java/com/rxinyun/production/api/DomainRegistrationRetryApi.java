package com.rxinyun.production.api;

public interface DomainRegistrationRetryApi {
   void retry(String requestId);
}
