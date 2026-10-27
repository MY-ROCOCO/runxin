package com.rxinyun.admin.system.modular.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.resource.entity.SysModule;
import com.rxinyun.admin.system.modular.resource.param.module.SysModuleAddParam;
import com.rxinyun.admin.system.modular.resource.param.module.SysModuleEditParam;
import com.rxinyun.admin.system.modular.resource.param.module.SysModuleIdParam;
import com.rxinyun.admin.system.modular.resource.param.module.SysModulePageParam;
import java.util.List;

public interface SysModuleService extends IService<SysModule> {
   Page<SysModule> page(SysModulePageParam sysModulePageParam);

   void add(SysModuleAddParam sysModuleAddParam);

   void edit(SysModuleEditParam sysModuleEditParam);

   void delete(List<SysModuleIdParam> sysModuleIdParamList);

   SysModule detail(SysModuleIdParam sysModuleIdParam);

   SysModule queryEntity(String id);
}
