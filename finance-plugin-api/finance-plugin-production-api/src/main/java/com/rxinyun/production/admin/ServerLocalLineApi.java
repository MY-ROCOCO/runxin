package com.rxinyun.production.admin;

import com.rxinyun.production.admin.core.pojo.BaseLocalLine;
import java.util.List;
import java.util.Map;

public interface ServerLocalLineApi {
   BaseLocalLine selectByLineId(String LineId);

   Map<String, BaseLocalLine> selectByLineIds(List<String> lineIds);
}
