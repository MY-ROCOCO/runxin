package com.rxinyun.dev.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.dev.core.pojo.BaseIndependentLogPageParam;
import com.rxinyun.dev.core.pojo.BaseServerLogPageParam;
import com.rxinyun.dev.core.pojo.BaseServerOperationLogResult;
import java.util.List;

public interface DevLogApi {
   void executeLoginLog(String userName);

   void executeLogoutLog(String userName);

   List<JSONObject> currentUserVisLogList();

   List<JSONObject> currentUserOpLogList();

   Page<BaseServerOperationLogResult> cloudServerLogPage(BaseServerLogPageParam param);

   Page<BaseServerOperationLogResult> independentServerLogPage(BaseIndependentLogPageParam param);
}
