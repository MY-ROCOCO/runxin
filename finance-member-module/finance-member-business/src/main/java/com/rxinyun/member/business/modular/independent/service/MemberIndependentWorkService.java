package com.rxinyun.member.business.modular.independent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentWork;
import com.rxinyun.member.business.modular.independent.param.IndependentWorkAddParam;

public interface MemberIndependentWorkService extends IService<MemberIndependentWork> {
   void add(IndependentWorkAddParam independentWorkAddParam);

   boolean isExistWorkingTask(String independentId);

   void closePendingExpireStopWorks(String independentId);
}
