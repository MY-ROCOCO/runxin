package com.rxinyun.production.admin;

import com.rxinyun.production.admin.core.pojo.BaseIndependentSystemVersion;

public interface IndependentSystemVersionApi {
   BaseIndependentSystemVersion selectByOsVersionId(String osVersionId);
}
