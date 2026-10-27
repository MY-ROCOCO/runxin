package com.rxinyun.sys.api;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.sys.core.pojo.BaseSysUser;
import java.util.List;

public interface SysUserApi {
   JSONObject getUserByIdWithoutException(String userId);

   List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList);

   JSONObject getUserByIdWithException(String userId);

   List<JSONObject> getUserListByIdWithException(List<String> userIdList);

   List<String> ownRole(String userId);

   void grantRole(String userId, List<String> roleIdList);

   List<String> getUserIdListByOrgIdList(List<String> orgIdList);

   List<String> getUserIdListByPositionIdList(List<String> positionIdList);

   JSONObject getSupervisorIdBySupervisorLevel(List<String> userIdList, String userId, String orgId, String supervisorLevel);

   List<String> getMulSupervisorIdListByEndLevel(String userId, String orgId, String endLevel);

   Page<JSONObject> userSelector(String orgId, String searchKey);

   BaseSysUser getUserById(String userId);

   List<BaseSysUser> selectAllUser();
}
