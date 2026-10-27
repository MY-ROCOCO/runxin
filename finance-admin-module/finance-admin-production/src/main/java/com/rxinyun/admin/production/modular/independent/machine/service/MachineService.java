package com.rxinyun.admin.production.modular.independent.machine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineAddParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineEditParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachinePageParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineVO;
import jakarta.validation.constraints.NotBlank;

public interface MachineService {
   void addMachine(MachineAddParam machineAddParam);

   void deleteMachine(@NotBlank String id);

   void editMachine(MachineEditParam machineEditParam);

   MachineVO findMachine(String id);

   Page<MachineVO> page(MachinePageParam machinePageParam);

   void editMachineWeight(@NotBlank String id, @NotBlank String weight);
}
