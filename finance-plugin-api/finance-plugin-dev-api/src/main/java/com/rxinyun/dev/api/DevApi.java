package com.rxinyun.dev.api;

import cn.hutool.json.JSONObject;

public interface DevApi {
   JSONObject getDevOpCount();

   JSONObject getToolDataCount();
}
