package com.rxinyun.common.pojo;

import cn.hutool.json.JSONObject;

public interface CommonWrapperInterface<T> {
   JSONObject doWrap(T wrapperObject);
}
