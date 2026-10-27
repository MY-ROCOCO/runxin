package com.rxinyun.admin.production.modular.independent.machine.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.independent.cloud.entity.IndependentLine;
import com.rxinyun.admin.production.modular.independent.cloud.mapper.IndependentLineMapper;
import com.rxinyun.admin.production.modular.independent.machine.entity.Machine;
import com.rxinyun.admin.production.modular.independent.machine.entity.MachineLine;
import com.rxinyun.admin.production.modular.independent.machine.mapper.MachineLineMapper;
import com.rxinyun.admin.production.modular.independent.machine.mapper.MachineMapper;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineAddParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineEditParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachinePageParam;
import com.rxinyun.admin.production.modular.independent.machine.param.MachineVO;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineLineService;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineService;
import com.rxinyun.admin.production.modular.independent.region.model.entity.Region;
import com.rxinyun.admin.production.modular.independent.region.service.IRegionService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MachineServiceImpl extends ServiceImpl<MachineMapper, Machine> implements MachineService, AdminMachineApi {
   @Resource
   MachineLineMapper machineLineMapper;
   @Resource
   MachineMapper machineMapper;
   @Resource
   MachineLineService machineLineService;
   @Resource
   IndependentLineMapper independentLineMapper;
   @Resource
   IRegionService regionService;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private JdbcTemplate jdbcTemplate;
   private static final int MONTH_DAY_COUNT = 31;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void addMachine(MachineAddParam machineAddParam) {
      Machine machine = (Machine)BeanUtil.toBean(machineAddParam, Machine.class);
      if (machine.getDockingStatus() == null) {
         machine.setDockingStatus(1);
      }

      machine.setMachineDayPrice(machine.getMachineMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
      machine.setBandwidthDayPrice(machine.getBandwidthMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
      machine.setDefenseDayPrice(machine.getDefenseMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
      machine.setIpDayPrice(machine.getIpMonthPrice().divide(new BigDecimal(31), 2, RoundingMode.HALF_UP));
      if (machineAddParam.getBandwidthMinSize() < machineAddParam.getBandwidthFreeSize()) {
         throw new CommonException("不能低于赠送带宽", new Object[0]);
      } else if (machineAddParam.getIpMinSize() < machineAddParam.getIpFreeSize()) {
         throw new CommonException("不能低于赠送IP数量", new Object[0]);
      } else {
         this.save(machine);
         MachineLine machineLine = new MachineLine();

         for (String lineId : machineAddParam.getLineId()) {
            machineLine.setId(null);
            machineLine.setMachineId(machine.getId());
            machineLine.setLineId(lineId);
            this.machineLineMapper.insert(machineLine);
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteMachine(String id) {
      Machine machine = (Machine)this.machineMapper.selectById(id);
      if (machine == null) {
         throw new CommonException("要查询的机型不存在", new Object[0]);
      } else {
         this.machineLineMapper.delete((Wrapper)Wrappers.lambdaQuery(MachineLine.class).eq(MachineLine::getMachineId, machine.getId()));
         this.jdbcTemplate.update("DELETE FROM INDEPENDENT_MACHINE WHERE id = ?", new Object[]{id});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void editMachine(MachineEditParam machineEditParam) {
      if (machineEditParam.getBandwidthMinSize() < machineEditParam.getBandwidthFreeSize()) {
         throw new CommonException("不能低于赠送带宽", new Object[0]);
      } else if (machineEditParam.getIpMinSize() < machineEditParam.getIpFreeSize()) {
         throw new CommonException("不能低于赠送IP数量", new Object[0]);
      } else {
         Machine machine = (Machine)this.machineMapper.selectById(machineEditParam.getId());
         if (null == machine) {
            throw new CommonException("该机型不存在:{}", new Object[]{machineEditParam.getId()});
         } else {
            LambdaUpdateWrapper<Machine> lambdaUpdateWrapper = (LambdaUpdateWrapper<Machine>)Wrappers.lambdaUpdate(Machine.class)
               .eq(Machine::getId, machineEditParam.getId());
            if (machine.getType() == 0) {
               machine = (Machine)BeanUtil.toBean(machineEditParam, Machine.class);
               if (ObjectUtil.isNotEmpty(machineEditParam.getLineId())) {
                  this.machineLineMapper.delete((Wrapper)Wrappers.lambdaQuery(MachineLine.class).eq(MachineLine::getMachineId, machineEditParam.getId()));
                  MachineLine machineLine = new MachineLine();

                  for (String lineId : machineEditParam.getLineId()) {
                     machineLine.setId(null);
                     machineLine.setMachineId(machine.getId());
                     machineLine.setLineId(lineId);
                     this.machineLineMapper.insert(machineLine);
                  }
               }

               machine.setType(0);
               this.machineMapper.update(machine, lambdaUpdateWrapper);
            } else if (machine.getType() == 1) {
               if (ObjectUtil.isNotEmpty(machineEditParam.getCpuInfo()) && !ObjectUtil.equal(machineEditParam.getCpuInfo(), machine.getCpuInfo())) {
                  throw new CommonException("资源池类型机型不允许修改CPU型号", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getRamInfo()) && !ObjectUtil.equal(machineEditParam.getRamInfo(), machine.getRamInfo())) {
                  throw new CommonException("资源池类型机型不允许修改内存配置", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getDisksInfo()) && !ObjectUtil.equal(machineEditParam.getDisksInfo(), machine.getDisksInfo())) {
                  throw new CommonException("资源池类型机型不允许修改硬盘配置", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getIpMinSize()) && !ObjectUtil.equal(machineEditParam.getIpMinSize(), machine.getIpMinSize())) {
                  throw new CommonException("资源池类型机型不允许修改IP数量最小值", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getIpMaxSize()) && !ObjectUtil.equal(machineEditParam.getIpMaxSize(), machine.getIpMaxSize())) {
                  throw new CommonException("资源池类型机型不允许修改IP数量最大值", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getIpFreeSize()) && !ObjectUtil.equal(machineEditParam.getIpFreeSize(), machine.getIpFreeSize())) {
                  throw new CommonException("资源池类型机型不允许修改赠送IP数量", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getBandwidthMinSize())
                  && !ObjectUtil.equal(machineEditParam.getBandwidthMinSize(), machine.getBandwidthMinSize())) {
                  throw new CommonException("资源池类型机型不允许修改带宽最小值", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getBandwidthMaxSize())
                  && !ObjectUtil.equal(machineEditParam.getBandwidthMaxSize(), machine.getBandwidthMaxSize())) {
                  throw new CommonException("资源池类型机型不允许修改带宽最大值", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getBandwidthFreeSize())
                  && !ObjectUtil.equal(machineEditParam.getBandwidthFreeSize(), machine.getBandwidthFreeSize())) {
                  throw new CommonException("资源池类型机型不允许修改赠送带宽", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getDefenseMaxSize())
                  && !ObjectUtil.equal(machineEditParam.getDefenseMaxSize(), machine.getDefenseMaxSize())) {
                  throw new CommonException("资源池类型机型不允许修改防御峰值", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getDefenseFreeSize())
                  && !ObjectUtil.equal(machineEditParam.getDefenseFreeSize(), machine.getDefenseFreeSize())) {
                  throw new CommonException("资源池类型机型不允许修改赠送防御", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getAutomation()) && !ObjectUtil.equal(machineEditParam.getAutomation(), machine.getAutomation())) {
                  throw new CommonException("资源池类型机型不允许修改自动化参数", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getUpgrades()) && !ObjectUtil.equal(machineEditParam.getUpgrades(), machine.getUpgrades())) {
                  throw new CommonException("资源池类型机型不允许修改升级开关选项", new Object[0]);
               }

               if (ObjectUtil.isNotEmpty(machineEditParam.getInventory()) && !ObjectUtil.equal(machineEditParam.getInventory(), machine.getInventory())) {
                  throw new CommonException("资源池类型机型不允许修改库存数量", new Object[0]);
               }

               ((LambdaUpdateWrapper<Machine>)((LambdaUpdateWrapper<Machine>)((LambdaUpdateWrapper<Machine>)((LambdaUpdateWrapper<Machine>)((LambdaUpdateWrapper<Machine>)lambdaUpdateWrapper.eq(
                                 Machine::getId, machineEditParam.getId()
                              ))
                              .set(Machine::getMachineMonthPrice, machineEditParam.getMachineMonthPrice()))
                           .set(Machine::getBandwidthMonthPrice, machineEditParam.getBandwidthMonthPrice()))
                        .set(Machine::getDefenseMonthPrice, machineEditParam.getDefenseMonthPrice()))
                     .set(Machine::getIpMonthPrice, machineEditParam.getIpMonthPrice()))
                  .set(
                     Machine::getDockingStatus, machineEditParam.getDockingStatus() != null ? machineEditParam.getDockingStatus() : machine.getDockingStatus()
                  );
               this.update(lambdaUpdateWrapper);
               if (ObjectUtil.isNotEmpty(machineEditParam.getLineId())) {
                  this.machineLineMapper.delete((Wrapper)Wrappers.lambdaQuery(MachineLine.class).eq(MachineLine::getMachineId, machineEditParam.getId()));
                  MachineLine machineLine = new MachineLine();

                  for (String lineId : machineEditParam.getLineId()) {
                     machineLine.setId(null);
                     machineLine.setMachineId(machine.getId());
                     machineLine.setLineId(lineId);
                     this.machineLineMapper.insert(machineLine);
                  }
               }
            }
         }
      }
   }

   @Override
   public MachineVO findMachine(String id) {
      Machine machine = (Machine)this.getById(id);
      if (null == machine) {
         throw new CommonException("该机型不存在:{}", new Object[]{id});
      } else {
         MachineVO machineVO = (MachineVO)BeanUtil.toBean(machine, MachineVO.class);
         return this.getLineOfMachineVO(machineVO);
      }
   }

   @Override
   public Page<MachineVO> page(MachinePageParam machinePageParam) {
      QueryWrapper<Machine> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(machinePageParam.getKeyword())) {
         queryWrapper.lambda().and(q -> q.like(Machine::getName, machinePageParam.getKeyword()));
      }

      if (ObjectUtil.isNotEmpty(machinePageParam.getName())) {
         queryWrapper.lambda().and(q -> q.like(Machine::getName, machinePageParam.getName()));
      }

      if (ObjectUtil.isNotEmpty(machinePageParam.getAutomation())) {
         queryWrapper.lambda().and(q -> q.eq(Machine::getAutomation, machinePageParam.getAutomation()));
      }

      if (ObjectUtil.isNotEmpty(machinePageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Machine::getStatus, machinePageParam.getStatus()));
      }

      if (ObjectUtil.isNotEmpty(machinePageParam.getType())) {
         queryWrapper.lambda().and(q -> q.eq(Machine::getType, machinePageParam.getType()));
      }

      if (ObjectUtil.isNotEmpty(machinePageParam.getLineId())) {
         queryWrapper.apply("id IN (SELECT machine_id FROM independent_line_machine WHERE line_id = {0})", new Object[]{machinePageParam.getLineId()});
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{machinePageParam.getSortField(), machinePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(machinePageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            machinePageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(machinePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(Machine::getId);
      }

      Page<Machine> page = (Page<Machine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<MachineVO> list = page.getRecords().stream().map(this::convertToMachineVO).collect(Collectors.toList());

      for (int i = 0; i < list.size(); i++) {
         MachineVO machineVO = this.getLineOfMachineVO(list.get(i));
         if (ObjectUtil.isNotEmpty(machineVO.getResourcePoolId())) {
            BaseResourcePool resourcePool = this.resourcePoolApi.getById(machineVO.getResourcePoolId());
            if (ObjectUtil.isNotEmpty(resourcePool)) {
               machineVO.setResourcePoolType(resourcePool.getPoolType());
               machineVO.setResourcePoolName(resourcePool.getName());
            }
         }

         list.set(i, machineVO);
      }

      Page<MachineVO> pagere = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      pagere.setRecords(list);
      return pagere;
   }

   @Override
   public void editMachineWeight(String id, String weight) {
      Machine machine = (Machine)this.machineMapper.selectById(id);
      machine.setWeight(Integer.parseInt(weight));
      this.machineMapper.updateById(machine);
   }

   private MachineVO convertToMachineVO(Machine machine) {
      return (MachineVO)BeanUtil.toBean(machine, MachineVO.class);
   }

   private MachineVO getLineOfMachineVO(MachineVO machineVO) {
      List<MachineLine> list = this.machineLineService.findLineMachine(machineVO.getId());
      List<String> lineName = new ArrayList<>();
      Set<String> regionName = new HashSet<>();

      for (MachineLine machineLine : list) {
         IndependentLine independentLine = (IndependentLine)this.independentLineMapper.selectById(machineLine.getLineId());
         if (independentLine != null) {
            if (ObjectUtil.isNotEmpty(independentLine.getName())) {
               lineName.add(independentLine.getName());
            }

            if (ObjectUtil.isNotEmpty(independentLine.getRegionId())) {
               Region region = this.regionService.searchRegionById(independentLine.getRegionId());
               if (region != null && ObjectUtil.isNotEmpty(region.getName())) {
                  regionName.add(region.getName());
               }
            }
         }
      }

      machineVO.setRegionName(regionName.stream().toList());
      machineVO.setLineName(lineName);
      return machineVO;
   }

   public BaseAdminMachine findMachineById(String id) {
      Machine machine = (Machine)this.getById(id);
      if (null == machine) {
         throw new CommonException("该机型不存在:{}", new Object[]{id});
      } else {
         return (BaseAdminMachine)BeanUtil.toBean(machine, BaseAdminMachine.class);
      }
   }
}
