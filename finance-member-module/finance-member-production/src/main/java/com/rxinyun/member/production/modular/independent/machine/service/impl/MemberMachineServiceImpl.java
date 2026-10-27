package com.rxinyun.member.production.modular.independent.machine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.production.modular.independent.machine.entity.MemberMachine;
import com.rxinyun.member.production.modular.independent.machine.mapper.MemberMachineLineMapper;
import com.rxinyun.member.production.modular.independent.machine.mapper.MemberMachineMapper;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineIdParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineLineQueryParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachinePageParam;
import com.rxinyun.member.production.modular.independent.machine.param.MemberMachineVO;
import com.rxinyun.member.production.modular.independent.machine.service.MemberMachineService;
import com.rxinyun.member.production.modular.independent.region.service.IMemberRegionService;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MemberMachineServiceImpl extends ServiceImpl<MemberMachineMapper, MemberMachine> implements MemberMachineService {
   @Resource
   private IMemberRegionService memberRegionService;
   @Resource
   private MemberMachineLineMapper memberMachineLineMapper;
   @Resource
   private MemberMachineMapper memberMachineMapper;

   @Override
   public MemberMachineVO findMachine(MemberMachineIdParam machineIdParam) {
      MemberMachine machine = (MemberMachine)this.getById(machineIdParam.getId());
      if (null == machine) {
         throw new CommonException("该机型不可购买:{}", new Object[]{machineIdParam.getId()});
      } else {
         return (MemberMachineVO)BeanUtil.toBean(machine, MemberMachineVO.class);
      }
   }

   @Override
   public Page<MemberMachineVO> page(MemberMachinePageParam machinePageParam) {
      QueryWrapper<MemberMachine> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(machinePageParam.getKeyword())) {
         queryWrapper.lambda().and(q -> q.like(MemberMachine::getName, machinePageParam.getKeyword()));
      }

      if (ObjectUtil.isNotEmpty(machinePageParam.getType())) {
         queryWrapper.lambda().and(q -> q.eq(MemberMachine::getType, machinePageParam.getType()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{machinePageParam.getSortField(), machinePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(machinePageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            machinePageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(machinePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(MemberMachine::getId);
      }

      Page<MemberMachine> page = (Page<MemberMachine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<MemberMachineVO> list = page.getRecords().stream().map(this::convertToMachineVO).collect(Collectors.toList());
      Page<MemberMachineVO> pagere = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      pagere.setRecords(list);
      return pagere;
   }

   private MemberMachineVO convertToMachineVO(MemberMachine machine) {
      MemberMachineVO machineVO = new MemberMachineVO();
      machineVO.setId(machine.getId());
      machineVO.setName(machine.getName());
      machineVO.setType(machine.getType());
      machineVO.setInventory(machine.getInventory());
      machineVO.setAutomation(machine.getAutomation());
      machineVO.setStatus(machine.getStatus());
      machineVO.setUpgrades(machine.getUpgrades());
      machineVO.setWeight(machine.getWeight());
      return machineVO;
   }

   @Override
   public List<MemberMachine> findByLine(MemberMachineLineQueryParam queryParam) {
      if (queryParam == null) {
         return Collections.emptyList();
      } else {
         String lineId = queryParam.getLineId();
         if (ObjectUtil.isEmpty(lineId)) {
            return Collections.emptyList();
         } else {
            LambdaQueryWrapper<MemberMachine> queryWrapper = (LambdaQueryWrapper<MemberMachine>)new QueryWrapper()
               .lambda()
               .apply("id IN (SELECT machine_id FROM independent_line_machine WHERE line_id = {0})", new Object[]{lineId});
            if (StrUtil.isNotBlank(queryParam.getName())) {
               queryWrapper.like(MemberMachine::getName, queryParam.getName());
            }

            if (StrUtil.isNotBlank(queryParam.getCpuInfo())) {
               queryWrapper.eq(MemberMachine::getCpuInfo, queryParam.getCpuInfo());
            }

            if (StrUtil.isNotBlank(queryParam.getRamInfo())) {
               queryWrapper.eq(MemberMachine::getRamInfo, queryParam.getRamInfo());
            }

            queryWrapper.orderByAsc(MemberMachine::getWeight);
            return this.memberMachineMapper.selectList(queryWrapper);
         }
      }
   }
}
