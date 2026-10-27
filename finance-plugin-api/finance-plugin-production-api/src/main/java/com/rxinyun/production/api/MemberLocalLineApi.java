package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import java.util.List;
import java.util.Map;

public interface MemberLocalLineApi {
   BaseMemberLocalLine selectByLineId(String LineId);

   Map<String, BaseMemberLocalLine> selectByLineIds(List<String> lineIds);
}
