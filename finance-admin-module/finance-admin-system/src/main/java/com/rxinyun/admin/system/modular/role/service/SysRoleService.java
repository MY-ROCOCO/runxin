package com.rxinyun.admin.system.modular.role.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.role.param.SysRoleAddParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleEditParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantMobileMenuParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantPermissionParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantResourceParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantUserParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleIdParam;
import com.rxinyun.admin.system.modular.role.param.SysRolePageParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorRoleParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorUserParam;
import com.rxinyun.admin.system.modular.role.result.SysRoleGrantResourceTreeResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnMobileMenuResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnPermissionResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnResourceResult;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {
   Page<SysRole> page(SysRolePageParam sysRolePageParam);

   void add(SysRoleAddParam sysRoleAddParam);

   void edit(SysRoleEditParam sysRoleEditParam);

   void delete(List<SysRoleIdParam> sysRoleIdParamList);

   SysRole detail(SysRoleIdParam sysRoleIdParam);

   SysRole queryEntity(String id);

   SysRoleOwnResourceResult ownResource(SysRoleIdParam SysRoleIdParam);

   void grantResource(SysRoleGrantResourceParam sysRoleGrantResourceParam);

   SysRoleOwnMobileMenuResult ownMobileMenu(SysRoleIdParam sysRoleIdParam);

   void grantMobileMenu(SysRoleGrantMobileMenuParam sysRoleGrantMobileMenuParam);

   SysRoleOwnPermissionResult ownPermission(SysRoleIdParam SysRoleIdParam);

   void grantPermission(SysRoleGrantPermissionParam sysRoleGrantPermissionParam);

   List<String> ownUser(SysRoleIdParam sysRoleIdParam);

   void grantUser(SysRoleGrantUserParam sysRoleGrantUserParam);

   List<SysRoleGrantResourceTreeResult> resourceTreeSelector();

   List<String> permissionTreeSelector();

   Page<SysRole> roleSelector(SysRoleSelectorRoleParam sysRoleSelectorRoleParam);

   Page<SysUser> userSelector(SysRoleSelectorUserParam sysRoleSelectorUserParam);
}
