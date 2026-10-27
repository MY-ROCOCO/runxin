package com.rxinyun.admin.workorder.modular.workorder.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrderType;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.TypeRelevanceParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.TypeStaticParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypeAddParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypeEditParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypePageParam;
import com.rxinyun.common.pojo.CommonResult;

public interface WorkOrderTypeService {
   Page<WorkOrderType> page(WorkOrderTypePageParam workOrderTypePageParam);

   void save(WorkOrderTypeAddParam workOrderTypeParam);

   CommonResult isRelevnce(TypeRelevanceParam typeRelevanceParam);

   CommonResult isVisible(TypeStaticParam typeStaticParam);

   CommonResult update(WorkOrderTypeEditParam workOrderTypeParam);

   CommonResult delete(String id);
}
