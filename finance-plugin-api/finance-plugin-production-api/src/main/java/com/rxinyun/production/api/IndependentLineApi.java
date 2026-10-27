package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseIndependentLine;

public interface IndependentLineApi {
   BaseIndependentLine selectByLineId(String LineId);
}
