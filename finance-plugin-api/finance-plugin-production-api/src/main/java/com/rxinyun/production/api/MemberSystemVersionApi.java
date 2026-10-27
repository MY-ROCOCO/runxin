package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberSystemVersion;

public interface MemberSystemVersionApi {
   BaseMemberSystemVersion selectByOsVersionId(String osVersionId);
}
