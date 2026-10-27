package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;

public interface MemberIndependentSystemVersionApi {
   BaseMemberIndependentSystemVersion selectByOsVersionId(String osVersionId);
}
