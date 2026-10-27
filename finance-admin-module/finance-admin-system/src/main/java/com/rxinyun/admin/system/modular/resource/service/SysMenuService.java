package com.rxinyun.admin.system.modular.resource.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.entity.SysModule;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuAddParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuChangeModuleParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuEditParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuIdParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuPageParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuSelectorMenuParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuSelectorModuleParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuTreeParam;
import java.util.List;

public interface SysMenuService extends IService<SysMenu> {
   Page<SysMenu> page(SysMenuPageParam sysMenuPageParam);

   List<Tree<String>> tree(SysMenuTreeParam sysMenuTreeParam);

   void add(SysMenuAddParam sysMenuAddParam);

   String addForGenMenu(String parentId, String busName, String title, String module, String path);

   void edit(SysMenuEditParam sysMenuEditParam);

   void changeModule(SysMenuChangeModuleParam sysMenuChangeModuleParam);

   void delete(List<SysMenuIdParam> sysMenuIdParamList);

   SysMenu detail(SysMenuIdParam sysMenuIdParam);

   SysMenu queryEntity(String id);

   List<SysMenu> getChildListById(List<SysMenu> originDataList, String id, boolean includeSelf);

   List<SysMenu> getParentListById(List<SysMenu> originDataList, String id, boolean includeSelf);

   List<SysModule> moduleSelector(SysMenuSelectorModuleParam sysMenuSelectorModuleParam);

   List<Tree<String>> menuTreeSelector(SysMenuSelectorMenuParam sysMenuSelectorMenuParam);
}
