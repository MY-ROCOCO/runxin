package com.rxinyun.admin.biz.modular.position.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.position.param.BizPositionAddParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionEditParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionIdParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionPageParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionSelectorPositionParam;
import java.util.List;

public interface BizPositionService extends IService<BizPosition> {
   Page<BizPosition> page(BizPositionPageParam bizPositionPageParam);

   void add(BizPositionAddParam bizPositionAddParam);

   void edit(BizPositionEditParam bizPositionEditParam);

   void delete(List<BizPositionIdParam> bizPositionIdParamList);

   BizPosition detail(BizPositionIdParam bizPositionIdParam);

   BizPosition queryEntity(String id);

   String getPositionIdByPositionNameWithCreate(String orgId, String positionName);

   List<Tree<String>> orgTreeSelector();

   Page<BizPosition> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam);
}
