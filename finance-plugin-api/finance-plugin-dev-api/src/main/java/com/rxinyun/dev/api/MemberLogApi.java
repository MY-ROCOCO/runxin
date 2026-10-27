package com.rxinyun.dev.api;

import cn.hutool.json.JSONObject;
import java.util.List;

public interface MemberLogApi {
   void executeLoginLog(String memberNo, String userName);

   void executeLogoutLog(String memberNo, String userName);

   List<JSONObject> currentUserVisLogList();

   List<JSONObject> currentUserOpLogList();
}
