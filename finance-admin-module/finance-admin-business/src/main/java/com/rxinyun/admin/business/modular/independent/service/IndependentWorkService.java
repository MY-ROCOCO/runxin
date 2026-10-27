package com.rxinyun.admin.business.modular.independent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.business.modular.independent.entity.IndependentWork;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkStatusUpdateParam;
import com.rxinyun.admin.business.modular.independent.response.IndependentWorkResponse;

public interface IndependentWorkService extends IService<IndependentWork> {
   Page<IndependentWorkResponse> page(IndependentWorkPageParam pageParam);

   IndependentWorkResponse detail(IndependentWorkIdParam idParam);

   void add(IndependentWorkAddParam independentWorkAddParam);

   IndependentWork queryEntity(String id);

   void updateStatus(IndependentWorkStatusUpdateParam updateParam);

   boolean isExistWorkingTask(String independentId);

   void closePendingExpireStopWorks(String independentId);
}
