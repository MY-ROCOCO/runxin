package com.rxinyun.production.api;

import com.rxinyun.production.api.dto.DomainCreateStatusVO;

public interface DomainCreateStatusApi {
   DomainCreateStatusVO getCreateStatus(String domainName);
}
