package com.rxinyun.member.production.modular.independent.machine.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.independent.machine.entity.MemberMachine;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineIdParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineLineQueryParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachinePageParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineVO;
import java.util.List;

public interface MemberMachineService {
   MemberMachineVO findMachine(MemberMachineIdParam machineIdParam);

   Page<MemberMachineVO> page(MemberMachinePageParam machinePageParam);

   List<MemberMachine> findByLine(MemberMachineLineQueryParam queryParam);
}
