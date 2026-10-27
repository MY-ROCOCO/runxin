package com.rxinyun.admin.system.modular.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.resource.entity.SysButton;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonAddParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonEditParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonIdParam;
import com.rxinyun.admin.system.modular.resource.param.button.SysButtonPageParam;
import java.util.List;

public interface SysButtonService extends IService<SysButton> {
   Page<SysButton> page(SysButtonPageParam sysButtonPageParam);

   void add(SysButtonAddParam sysButtonAddParam);

   void addForGenButton(String menuId, String className, String functionName);

   void edit(SysButtonEditParam sysButtonEditParam);

   void delete(List<SysButtonIdParam> sysButtonIdParamList);

   SysButton detail(SysButtonIdParam sysButtonIdParam);

   SysButton queryEntity(String id);
}
