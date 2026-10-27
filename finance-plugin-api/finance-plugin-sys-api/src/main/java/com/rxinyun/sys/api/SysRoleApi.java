package com.rxinyun.sys.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SysRoleApi {
   boolean orgHasRole(List<String> orgIdList);

   Page<JSONObject> roleSelector(String orgId, String category, String searchKey, List<String> dataScopeList, boolean excludeSuperAdmin);

   void grantForGenMenuAndButton(String menuId);
}
