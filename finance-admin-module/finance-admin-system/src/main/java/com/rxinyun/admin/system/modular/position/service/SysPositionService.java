package com.rxinyun.admin.system.modular.position.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.system.modular.position.entity.SysPosition;
import com.rxinyun.admin.system.modular.position.param.SysPositionAddParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionEditParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionIdParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionPageParam;
import com.rxinyun.admin.system.modular.position.param.SysPositionSelectorPositionParam;
import java.util.List;

public interface SysPositionService extends IService<SysPosition> {
   Page<SysPosition> page(SysPositionPageParam sysPositionPageParam);

   void add(SysPositionAddParam sysPositionAddParam);

   void edit(SysPositionEditParam sysPositionEditParam);

   void delete(List<SysPositionIdParam> sysPositionIdParamList);

   SysPosition detail(SysPositionIdParam sysPositionIdParam);

   SysPosition queryEntity(String id);

   SysPosition getById(List<SysPosition> originDataList, String id);

   String getPositionIdByPositionNameWithCreate(String orgId, String positionName);

   List<Tree<String>> orgTreeSelector();

   Page<SysPosition> positionSelector(SysPositionSelectorPositionParam sysPositionSelectorPositionParam);
}
