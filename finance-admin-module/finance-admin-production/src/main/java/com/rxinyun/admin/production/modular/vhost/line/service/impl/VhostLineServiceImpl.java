package com.rxinyun.admin.production.modular.vhost.line.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.mapper.VhostLineMapper;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineAddParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineEditParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLineIdParam;
import com.rxinyun.admin.production.modular.vhost.line.param.VhostLinePageParam;
import com.rxinyun.admin.production.modular.vhost.line.service.VhostLineService;
import com.rxinyun.admin.production.modular.vhost.master.service.VhostMasterLineService;
import com.rxinyun.admin.production.modular.vhost.region.entity.VhostRegion;
import com.rxinyun.admin.production.modular.vhost.region.mapper.VhostRegionMapper;
import com.rxinyun.admin.production.modular.vhost.version.service.VhostVersionLineService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostLineServiceImpl extends ServiceImpl<VhostLineMapper, VhostLine> implements VhostLineService {
   @Resource
   private VhostRegionMapper vhostRegionMapper;
   @Resource
   private VhostVersionLineService vhostVersionLineService;
   @Resource
   private VhostMasterLineService vhostMasterLineService;
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public Page<VhostLine> page(VhostLinePageParam pageParam) {
      QueryWrapper<VhostLine> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getRegionId())) {
         queryWrapper.lambda().eq(VhostLine::getRegionId, pageParam.getRegionId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(VhostLine::getName, pageParam.getSearchKey());
      }

      if (pageParam.getStatus() != null) {
         queryWrapper.lambda().eq(VhostLine::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         ((LambdaQueryWrapper<VhostLine>)queryWrapper.lambda().orderByAsc(VhostLine::getSortId)).orderByDesc(VhostLine::getId);
      }

      Page<VhostLine> page = (Page<VhostLine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillRegionName(page.getRecords());
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(VhostLineAddParam addParam) {
      this.checkRegion(addParam.getRegionId());
      this.checkStatus(addParam.getStatus());
      this.checkName(addParam.getRegionId(), addParam.getName(), null);
      VhostLine line = (VhostLine)BeanUtil.toBean(addParam, VhostLine.class);
      line.setSortId(this.defaultSort(addParam.getSortId()));
      line.setStatus(this.defaultStatus(addParam.getStatus()));
      this.save(line);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(VhostLineEditParam editParam) {
      VhostLine line = this.queryEntity(editParam.getId());
      this.checkRegion(editParam.getRegionId());
      this.checkStatus(editParam.getStatus());
      this.checkName(editParam.getRegionId(), editParam.getName(), editParam.getId());
      BeanUtil.copyProperties(editParam, line, new String[0]);
      line.setSortId(this.defaultSort(editParam.getSortId()));
      line.setStatus(this.defaultStatus(editParam.getStatus()));
      this.updateById(line);
   }

   @Override
   public VhostLine detail(VhostLineIdParam idParam) {
      VhostLine line = this.queryEntity(idParam.getId());
      this.fillRegionName(List.of(line));
      return line;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<VhostLineIdParam> idParamList) {
      List<String> ids = CollStreamUtil.toList(idParamList, VhostLineIdParam::getId);
      if (!ObjectUtil.isEmpty(ids)) {
         boolean bound = ids.stream().anyMatch(id -> this.vhostVersionLineService.existsByLineId(id) || this.vhostMasterLineService.existsByLineId(id));
         if (bound) {
            throw new CommonException("线路已被版本或主控绑定，无法删除", new Object[0]);
         } else {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            this.jdbcTemplate.update("DELETE FROM VHOST_LINE WHERE ID IN (" + placeholders + ")", ids.toArray());
         }
      }
   }

   @Override
   public List<VhostLine> selector(String regionId) {
      LambdaQueryWrapper<VhostLine> queryWrapper = (LambdaQueryWrapper<VhostLine>)((LambdaQueryWrapper<VhostLine>)new LambdaQueryWrapper<VhostLine>().eq(VhostLine::getStatus, 1))
         .orderByAsc(VhostLine::getSortId);
      if (StrUtil.isNotBlank(regionId)) {
         queryWrapper.eq(VhostLine::getRegionId, regionId);
      }

      List<VhostLine> lines = this.list(queryWrapper);
      this.fillRegionName(lines);
      return lines;
   }

   private void fillRegionName(List<VhostLine> lines) {
      if (!CollUtil.isEmpty(lines)) {
         Set<String> regionIds = lines.stream().map(VhostLine::getRegionId).filter(CharSequenceUtil::isNotBlank).collect(Collectors.toSet());
         if (!CollUtil.isEmpty(regionIds)) {
            Map<String, String> regionNameMap = this.vhostRegionMapper
               .selectBatchIds(regionIds)
               .stream()
               .collect(Collectors.toMap(VhostRegion::getId, VhostRegion::getName, (left, right) -> left));
            lines.forEach(line -> line.setRegionName(regionNameMap.get(line.getRegionId())));
         }
      }
   }

   private VhostLine queryEntity(String id) {
      VhostLine line = (VhostLine)this.getById(id);
      if (ObjectUtil.isEmpty(line)) {
         throw new CommonException("虚拟主机线路不存在，id值为：{}", new Object[]{id});
      } else {
         return line;
      }
   }

   private void checkRegion(String regionId) {
      VhostRegion region = (VhostRegion)this.vhostRegionMapper.selectById(regionId);
      if (region == null) {
         throw new CommonException("虚拟主机地域不存在", new Object[0]);
      }
   }

   private void checkName(String regionId, String name, String excludeId) {
      LambdaQueryWrapper<VhostLine> queryWrapper = (LambdaQueryWrapper<VhostLine>)((LambdaQueryWrapper<VhostLine>)new LambdaQueryWrapper<VhostLine>()
            .eq(VhostLine::getRegionId, regionId))
         .eq(VhostLine::getName, name);
      if (StrUtil.isNotBlank(excludeId)) {
         queryWrapper.ne(VhostLine::getId, excludeId);
      }

      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("同一地域下线路名称已存在", new Object[0]);
      }
   }

   private void checkStatus(Integer status) {
      if (status != null && status != 0 && status != 1) {
         throw new CommonException("状态只能是启用或禁用", new Object[0]);
      }
   }

   private Integer defaultSort(Integer sortId) {
      return sortId == null ? 0 : sortId;
   }

   private Integer defaultStatus(Integer status) {
      return status == null ? 1 : status;
   }
}
