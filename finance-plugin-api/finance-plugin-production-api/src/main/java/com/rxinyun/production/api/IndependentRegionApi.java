package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseIndependentRegion;

public interface IndependentRegionApi {
   BaseIndependentRegion selectByRegionId(String LineId);
}
