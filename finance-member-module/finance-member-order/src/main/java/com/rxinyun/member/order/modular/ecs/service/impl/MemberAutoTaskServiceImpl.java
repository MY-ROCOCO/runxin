package com.rxinyun.member.order.modular.ecs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.member.order.modular.ecs.entity.AutoTask;
import com.rxinyun.member.order.modular.ecs.mapper.MemberAutoTaskMapper;
import com.rxinyun.member.order.modular.ecs.param.MemberAutoTaskAddParam;
import com.rxinyun.member.order.modular.ecs.service.MemberAutoTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberAutoTaskServiceImpl extends ServiceImpl<MemberAutoTaskMapper, AutoTask> implements MemberAutoTaskService {
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberAutoTaskAddParam orderAsyncTaskAddParam) {
      AutoTask orderAsyncTask = (AutoTask)BeanUtil.toBean(orderAsyncTaskAddParam, AutoTask.class);
      this.save(orderAsyncTask);
   }
}
