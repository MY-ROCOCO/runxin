package com.rxinyun.admin.production.modular.independent.machine.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.independent.machine.entity.MachineLine;
import com.rxinyun.admin.production.modular.independent.machine.mapper.MachineLineMapper;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineLineService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MachineLineServiceImpl extends ServiceImpl<MachineLineMapper, MachineLine> implements MachineLineService {
   @Resource
   MachineLineMapper machineLineMapper;

   @Override
   public List<MachineLine> findLineMachine(String id) {
      return this.machineLineMapper.selectList((Wrapper)Wrappers.lambdaQuery(MachineLine.class).eq(MachineLine::getMachineId, id));
   }

   @Override
   public boolean findLineBindingMache(String id) {
      QueryWrapper<MachineLine> queryWrapper = (QueryWrapper<MachineLine>)new QueryWrapper().eq("line_id", id);
      List<MachineLine> entity = this.machineLineMapper.selectList(queryWrapper);
      return entity.size() > 0;
   }
}
