package com.rxinyun.production.api;

import com.rxinyun.production.api.dto.DomainManagementPwdVO;

public interface DomainManagementPwdApi {
   DomainManagementPwdVO getManagementPwd(String domainName);
}
