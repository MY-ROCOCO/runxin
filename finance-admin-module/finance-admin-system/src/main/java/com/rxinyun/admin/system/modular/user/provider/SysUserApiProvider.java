package com.rxinyun.admin.system.modular.user.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorUserParam;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.sys.api.SysUserApi;
import com.rxinyun.sys.core.pojo.BaseSysUser;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SysUserApiProvider implements SysUserApi {
   @Resource
   private SysUserService sysUserService;

   public JSONObject getUserByIdWithoutException(String userId) {
      SysUser sysUser = (SysUser)this.sysUserService.getById(userId);
      return ObjectUtil.isNotEmpty(sysUser) ? JSONUtil.parseObj(sysUser) : null;
   }

   public List<JSONObject> getUserListByIdListWithoutException(List<String> userIdList) {
      return this.sysUserService.listByIds(userIdList).stream().<JSONObject>map(JSONUtil::parseObj).collect(Collectors.toList());
   }

   public JSONObject getUserByIdWithException(String userId) {
      return JSONUtil.parseObj(this.sysUserService.queryEntity(userId));
   }

   public List<JSONObject> getUserListByIdWithException(List<String> userIdList) {
      HashSet<String> userIdSet = CollectionUtil.newHashSet(userIdList);
      List<SysUser> sysUserList = this.sysUserService.listByIds(userIdSet);
      if (sysUserList.size() != userIdSet.size()) {
         throw new CommonException("某用户不存在，id值集合为：{}", new Object[]{userIdSet});
      } else {
         return sysUserList.stream().<JSONObject>map(JSONUtil::parseObj).collect(Collectors.toList());
      }
   }

   public List<String> ownRole(String userId) {
      SysUserIdParam sysUserIdParam = new SysUserIdParam();
      sysUserIdParam.setId(userId);
      return this.sysUserService.ownRole(sysUserIdParam);
   }

   public void grantRole(String userId, List<String> roleIdList) {
      SysUserGrantRoleParam sysUserGrantRoleParam = new SysUserGrantRoleParam();
      sysUserGrantRoleParam.setId(userId);
      sysUserGrantRoleParam.setRoleIdList(roleIdList);
      this.sysUserService.grantRole(sysUserGrantRoleParam);
   }

   public List<String> getUserIdListByOrgIdList(List<String> orgIdList) {
      return CollectionUtil.newArrayList(new String[0]);
   }

   public List<String> getUserIdListByPositionIdList(List<String> positionIdList) {
      return CollectionUtil.newArrayList(new String[0]);
   }

   public JSONObject getSupervisorIdBySupervisorLevel(List<String> userIdList, String userId, String orgId, String supervisorLevel) {
      return JSONUtil.createObj().set("id", null).set("idList", CollectionUtil.newArrayList(new Object[0]));
   }

   public List<String> getMulSupervisorIdListByEndLevel(String userId, String orgId, String endLevel) {
      List<String> resultList = CollectionUtil.newArrayList(new String[0]);
      if (endLevel.equals("-1")) {
         List<String> idList = this.getSupervisorIdBySupervisorLevel(CollectionUtil.newArrayList(new String[0]), userId, orgId, endLevel)
            .getBeanList("idList", String.class);
         if (ObjectUtil.isNotEmpty(idList)) {
            resultList.addAll(idList);
         }
      } else {
         Integer levelValue = Convert.toInt(endLevel);

         for (int i = 1; i < levelValue; i++) {
            String supervisorId = this.getSupervisorIdBySupervisorLevel(CollectionUtil.newArrayList(new String[0]), userId, orgId, Convert.toStr(i))
               .getStr("id");
            if (!ObjectUtil.isNotEmpty(supervisorId)) {
               break;
            }

            resultList.add(supervisorId);
         }
      }

      return resultList;
   }

   public Page<JSONObject> userSelector(String orgId, String searchKey) {
      SysUserSelectorUserParam sysUserSelectorUserParam = new SysUserSelectorUserParam();
      sysUserSelectorUserParam.setSearchKey(searchKey);
      return (Page<JSONObject>)BeanUtil.toBean(this.sysUserService.userSelector(sysUserSelectorUserParam), Page.class);
   }

   public BaseSysUser getUserById(String userId) {
      SysUser sysUser = this.sysUserService.queryEntity(userId);
      return ObjectUtil.isNotEmpty(sysUser) ? (BaseSysUser)BeanUtil.toBean(sysUser, BaseSysUser.class) : null;
   }

   public List<BaseSysUser> selectAllUser() {
      return this.sysUserService.list().stream().map(item -> (BaseSysUser)BeanUtil.toBean(item, BaseSysUser.class)).collect(Collectors.toList());
   }
}
