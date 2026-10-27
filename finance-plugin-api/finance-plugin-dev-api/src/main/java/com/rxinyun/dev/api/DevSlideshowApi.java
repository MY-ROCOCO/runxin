package com.rxinyun.dev.api;

import cn.hutool.json.JSONObject;
import java.util.List;

public interface DevSlideshowApi {
   List<JSONObject> getListByPlace(String place);
}
