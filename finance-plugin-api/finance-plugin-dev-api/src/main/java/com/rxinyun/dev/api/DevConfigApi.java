package com.rxinyun.dev.api;

import com.rxinyun.dev.core.pojo.BaseDevConfig;
import java.util.List;

public interface DevConfigApi {
   String getValueByKey(String key);


   List<BaseDevConfig> sysBaseList();
}
