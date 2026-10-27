package com.rxinyun.sys.api;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SysOrgApi {
   String getNameById(String orgId);

   String getSupervisorIdByOrgId(String orgId);

   List<Tree<String>> orgTreeSelector();

   Page<JSONObject> orgListSelector(String parentId);
}
