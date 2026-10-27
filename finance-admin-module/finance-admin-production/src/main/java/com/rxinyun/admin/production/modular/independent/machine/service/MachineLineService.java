package com.rxinyun.admin.production.modular.independent.machine.service;

import com.rxinyun.admin.production.modular.independent.machine.entity.MachineLine;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public interface MachineLineService {
   List<MachineLine> findLineMachine(@NotBlank String id);

   boolean findLineBindingMache(String id);
}
