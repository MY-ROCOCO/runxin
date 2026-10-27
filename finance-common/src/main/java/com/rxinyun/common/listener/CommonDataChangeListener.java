package com.rxinyun.common.listener;

import cn.hutool.json.JSONArray;
import java.util.List;

public interface CommonDataChangeListener {
   void doAddWithDataIdList(String dataType, List<String> dataIdList);

   void doAddWithDataList(String dataType, JSONArray jsonArray);

   void doUpdateWithDataIdList(String dataType, List<String> dataIdList);

   void doUpdateWithDataList(String dataType, JSONArray jsonArray);

   void doDeleteWithDataIdList(String dataType, List<String> dataIdList);
}
