package com.rxinyun.sys.api;

import java.util.List;

public interface SysRelationApi {
   List<String> getUserIdListByRoleIdList(List<String> roleIdList);

   void removeRoleHasMobileMenuRelation(List<String> targetIdList);

   void removeRoleHasMobileButtonRelation(List<String> targetIdList, List<String> buttonIdList);
}
