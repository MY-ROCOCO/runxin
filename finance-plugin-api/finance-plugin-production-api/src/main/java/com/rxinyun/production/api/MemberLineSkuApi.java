package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberLineSku;

public interface MemberLineSkuApi {
   BaseMemberLineSku selectByLineSku(String lineId, int cpu, int ram);
}
