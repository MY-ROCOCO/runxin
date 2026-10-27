package com.rxinyun.auth.api;

import cn.hutool.json.JSONObject;

public interface AuthApi {
   JSONObject getUserSessionCount();

   Long getThirdUserCount();
}
