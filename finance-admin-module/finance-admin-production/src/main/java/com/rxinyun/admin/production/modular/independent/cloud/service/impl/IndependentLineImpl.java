package com.rxinyun.admin.production.modular.independent.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.independent.cloud.entity.IndependentLine;
import com.rxinyun.admin.production.modular.independent.cloud.mapper.IndependentLineMapper;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineAddParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineEditParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLinePageParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLinePageVO;
import com.rxinyun.admin.production.modular.independent.cloud.param.ServerLocalLineUpdateDiscountTemplateParam;
import com.rxinyun.admin.production.modular.independent.cloud.service.IndependentLineService;
import com.rxinyun.admin.production.modular.independent.machine.service.MachineLineService;
import com.rxinyun.admin.production.modular.independent.os.service.OsVersionService;
import com.rxinyun.admin.production.modular.independent.region.service.IRegionService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.marketing.api.MemberDiscountTemplateApi;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndependentLineImpl extends ServiceImpl<IndependentLineMapper, IndependentLine> implements IndependentLineService {
   @Resource
   private IndependentLineMapper independentLineMapper;
   @Resource
   private MachineLineService machineLineService;
   @Resource
   private IRegionService iRegionService;
   @Resource
   private OsVersionService osVersionService;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private MemberDiscountTemplateApi memberDiscountTemplateApi;
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(IndependentLineAddParam independentLineAddParam) {
      IndependentLine independentLine = (IndependentLine)BeanUtil.toBean(independentLineAddParam, IndependentLine.class);
      this.save(independentLine);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(String id) {
      if (!this.machineLineService.findLineBindingMache(id) && !this.osVersionService.isBindLine(id)) {
         this.jdbcTemplate.update("DELETE FROM INDEPENDENT_LINE WHERE id = ?", new Object[]{id});
      } else if (this.machineLineService.findLineBindingMache(id)) {
         throw new CommonException("该线路已绑定机器，请先解除绑定", new Object[0]);
      } else {
         throw new CommonException("该线路已绑定操作系统版本，请先解除绑定", new Object[0]);
      }
   }

   @Override
   public Page<IndependentLinePageVO> page(IndependentLinePageParam independentLinePageParam) {
      QueryWrapper<IndependentLine> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(independentLinePageParam.getId())) {
         queryWrapper.lambda().eq(IndependentLine::getId, independentLinePageParam.getId());
      }

      if (StrUtil.isNotBlank(independentLinePageParam.getRegionId())) {
         queryWrapper.lambda().eq(IndependentLine::getRegionId, independentLinePageParam.getRegionId());
      }

      if (StrUtil.isNotBlank(independentLinePageParam.getName())) {
         queryWrapper.lambda().like(IndependentLine::getName, independentLinePageParam.getName());
      }

      if (ObjectUtil.isNotEmpty(independentLinePageParam.getWeight())) {
         queryWrapper.lambda().eq(IndependentLine::getWeight, independentLinePageParam.getWeight());
      }

      if (ObjectUtil.isNotEmpty(independentLinePageParam.getYearNumber())) {
         queryWrapper.lambda().eq(IndependentLine::getYearNumber, independentLinePageParam.getYearNumber());
      }

      if (StrUtil.isNotBlank(independentLinePageParam.getRemark())) {
         queryWrapper.lambda().like(IndependentLine::getRemark, independentLinePageParam.getRemark());
      }

      if (StrUtil.isNotBlank(independentLinePageParam.getStashIp())) {
         queryWrapper.lambda().like(IndependentLine::getStashIp, independentLinePageParam.getStashIp());
      }

      if (independentLinePageParam.getStatus() != null && (independentLinePageParam.getStatus().equals(1) || independentLinePageParam.getStatus().equals(0))) {
         queryWrapper.lambda().eq(IndependentLine::getStatus, independentLinePageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{independentLinePageParam.getSortField(), independentLinePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(independentLinePageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            independentLinePageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(independentLinePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(IndependentLine::getId);
      }

      Page<IndependentLine> pages = (Page<IndependentLine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<IndependentLinePageVO> page = new Page(pages.getCurrent(), pages.getSize(), pages.getTotal());
      List<IndependentLinePageVO> list = new ArrayList<>();
      IndependentLinePageVO independentLinePageVO = null;

      for (IndependentLine independentLine : pages.getRecords()) {
         independentLinePageVO = (IndependentLinePageVO)BeanUtil.toBean(independentLine, IndependentLinePageVO.class);
         if (StrUtil.isNotBlank(independentLine.getDiscountTemplateId())) {
            independentLinePageVO.setDiscountTemplateName(this.memberDiscountTemplateApi.selectNameById(independentLine.getDiscountTemplateId()));
         }

         if (StrUtil.isNotBlank(independentLine.getRegionId()) && this.iRegionService.searchRegionById(independentLine.getRegionId()) != null) {
            independentLinePageVO.setRegionName(this.iRegionService.searchRegionById(independentLine.getRegionId()).getName());
         }

         if (ObjectUtil.isNotEmpty(independentLinePageVO.getResourcePoolId())) {
            BaseResourcePool resourcePool = this.resourcePoolApi.getById(independentLinePageVO.getResourcePoolId());
            if (ObjectUtil.isNotEmpty(resourcePool)) {
               independentLinePageVO.setResourcePoolType(resourcePool.getPoolType());
               independentLinePageVO.setResourcePoolName(resourcePool.getName());
            }
         }

         list.add(independentLinePageVO);
      }

      page.setRecords(list);
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void update(IndependentLineEditParam independentLineEditParam) {
      IndependentLine independentLine1 = this.detail(independentLineEditParam.getId());
      BeanUtil.copyProperties(independentLineEditParam, independentLine1, new String[0]);
      this.updateById(independentLine1);
   }

   @Override
   public List<IndependentLine> getLineName() {
      return this.independentLineMapper
         .selectList(
            new QueryWrapper<IndependentLine>()
               .lambda()
               .select(IndependentLine::getId, IndependentLine::getName, IndependentLine::getType, IndependentLine::getResourcePoolId)
         );
   }

   @Override
   public IndependentLine detail(String id) {
      IndependentLine independentLine = (IndependentLine)this.getById(id);
      if (ObjectUtil.isEmpty(independentLine)) {
         throw new CommonException("可用线路不存在，id值为:{}", new Object[]{id});
      } else {
         return independentLine;
      }
   }

   @Override
   public boolean isBindingRegion(String id) {
      QueryWrapper<IndependentLine> queryWrapper = (QueryWrapper<IndependentLine>)new QueryWrapper().eq("region_id", id);
      List<IndependentLine> list = this.independentLineMapper.selectList(queryWrapper);
      return list.size() > 0;
   }

   @Override
   public void updateDiscountTemplate(ServerLocalLineUpdateDiscountTemplateParam serverLocalLineUpdateTemplateParam) {
      IndependentLine serverLocalLine = this.queryEntity(serverLocalLineUpdateTemplateParam.getLineId());
      serverLocalLine.setDiscountTemplateId(serverLocalLineUpdateTemplateParam.getDiscountTemplateId());
      this.updateById(serverLocalLine);
   }

   @Override
   public IndependentLine queryEntity(String id) {
      return (IndependentLine)this.getById(id);
   }
}
