package com.rxinyun.sys.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SysPositionApi {
   String getNameById(String positionId);

   Page<JSONObject> positionSelector(String orgId, String searchKey);
}
