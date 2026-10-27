package com.rxinyun.admin.production.modular.vhost.master.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.mapper.VhostLineMapper;
import com.rxinyun.admin.production.modular.vhost.master.entity.VhostMaster;
import com.rxinyun.admin.production.modular.vhost.master.enums.VhostControlledVersionEnum;
import com.rxinyun.admin.production.modular.vhost.master.mapper.VhostMasterMapper;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostControlledVersionResult;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterAddParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterEditParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterIdParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterPageParam;
import com.rxinyun.admin.production.modular.vhost.master.service.VhostMasterLineService;
import com.rxinyun.admin.production.modular.vhost.master.service.VhostMasterService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.vhost.VhostPanelRouter;
import com.rxinyun.common.vhost.result.VhostNetWork;
import com.rxinyun.common.vhost.result.VhostSystemTotal;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostMasterServiceImpl extends ServiceImpl<VhostMasterMapper, VhostMaster> implements VhostMasterService {
   @Resource
   private VhostMasterLineService vhostMasterLineService;
   @Resource
   private VhostLineMapper vhostLineMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private VhostPanelRouter vhostPanelRouter;

   @Override
   public Page<VhostMaster> page(VhostMasterPageParam pageParam) {
      QueryWrapper<VhostMaster> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(VhostMaster::getName, pageParam.getSearchKey());
      }

      if (pageParam.getStatus() != null) {
         queryWrapper.lambda().eq(VhostMaster::getStatus, pageParam.getStatus());
      }

      if (StrUtil.isNotBlank(pageParam.getControlledVersion())) {
         queryWrapper.lambda().eq(VhostMaster::getControlledVersion, pageParam.getControlledVersion());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(VhostMaster::getId);
      }

      Page<VhostMaster> page = (Page<VhostMaster>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillLines(page.getRecords());
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(VhostMasterAddParam addParam) {
      VhostControlledVersionEnum.of(addParam.getControlledVersion());
      this.checkStatus(addParam.getStatus());
      this.checkName(addParam.getName(), null);
      List<String> lineIds = this.resolveLineIds(addParam.getLineIds());
      VhostMaster master = (VhostMaster)BeanUtil.toBean(addParam, VhostMaster.class);
      master.setWeight(this.defaultWeight(addParam.getWeight()));
      master.setStatus(this.defaultStatus(addParam.getStatus()));
      this.save(master);
      this.vhostMasterLineService.replace(master.getId(), lineIds);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(VhostMasterEditParam editParam) {
      VhostMaster master = this.queryEntity(editParam.getId());
      VhostControlledVersionEnum.of(editParam.getControlledVersion());
      this.checkStatus(editParam.getStatus());
      this.checkName(editParam.getName(), editParam.getId());
      List<String> lineIds = this.resolveLineIds(editParam.getLineIds());
      String apiKey = StrUtil.isBlank(editParam.getApiKey()) ? master.getApiKey() : editParam.getApiKey();
      BeanUtil.copyProperties(editParam, master, new String[0]);
      master.setApiKey(apiKey);
      master.setWeight(this.defaultWeight(editParam.getWeight()));
      master.setStatus(this.defaultStatus(editParam.getStatus()));
      this.updateById(master);
      this.vhostMasterLineService.replace(master.getId(), lineIds);
   }

   @Override
   public VhostMaster detail(VhostMasterIdParam idParam) {
      VhostMaster master = this.queryEntity(idParam.getId());
      this.fillLines(List.of(master));
      return master;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<VhostMasterIdParam> idParamList) {
      List<String> ids = CollStreamUtil.toList(idParamList, VhostMasterIdParam::getId);
      if (!ObjectUtil.isEmpty(ids)) {
         this.vhostMasterLineService.physicalDeleteByMasterIds(ids);
         String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM VHOST_MASTER WHERE ID IN (" + placeholders + ")", ids.toArray());
      }
   }

   @Override
   public List<VhostControlledVersionResult> controlledVersions() {
      return Arrays.stream(VhostControlledVersionEnum.values()).map(item -> {
         VhostControlledVersionResult result = new VhostControlledVersionResult();
         result.setValue(item.name());
         result.setLabel(item.getLabel());
         return result;
      }).toList();
   }

   @Override
   public VhostSystemTotal systemTotal(VhostMasterIdParam idParam) {
      VhostMaster master = this.requirePanelMaster(idParam);
      return this.vhostPanelRouter.getAdapter(master.getControlledVersion()).getSystemTotal(master.getPanelUrl(), master.getApiKey());
   }

   @Override
   public VhostNetWork netWork(VhostMasterIdParam idParam) {
      VhostMaster master = this.requirePanelMaster(idParam);
      return this.vhostPanelRouter.getAdapter(master.getControlledVersion()).getNetWork(master.getPanelUrl(), master.getApiKey());
   }

   private void fillLines(List<VhostMaster> masters) {
      if (!CollUtil.isEmpty(masters)) {
         List<String> masterIds = masters.stream().map(VhostMaster::getId).toList();
         Map<String, List<String>> lineIdMap = this.vhostMasterLineService.groupLineIds(masterIds);
         Map<String, String> lineNameMap = this.loadLineNames(lineIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
         masters.forEach(master -> {
            this.fillControlledVersionName(master);
            List<String> lineIds = lineIdMap.getOrDefault(master.getId(), List.of());
            List<VhostLine.Bound> lines = this.toBoundLines(lineIds, lineNameMap);
            master.setLineIds(lineIds);
            master.setLines(lines);
            master.setLineNames(lines.stream().map(VhostLine.Bound::getName).filter(CharSequenceUtil::isNotBlank).collect(Collectors.joining(",")));
         });
      }
   }

   private void fillControlledVersionName(VhostMaster master) {
      if (!StrUtil.isBlank(master.getControlledVersion())) {
         for (VhostControlledVersionEnum item : VhostControlledVersionEnum.values()) {
            if (item.name().equals(master.getControlledVersion())) {
               master.setControlledVersionName(item.getLabel());
               return;
            }
         }
      }
   }

   private Map<String, String> loadLineNames(Collection<String> lineIds) {
      return CollUtil.isEmpty(lineIds)
         ? Map.of()
         : this.vhostLineMapper
            .selectList((Wrapper<VhostLine>)new LambdaQueryWrapper<VhostLine>().select(VhostLine::getId, VhostLine::getName).in(VhostLine::getId, lineIds))
            .stream()
            .collect(Collectors.toMap(VhostLine::getId, VhostLine::getName, (left, right) -> left));
   }

   private List<VhostLine.Bound> toBoundLines(List<String> lineIds, Map<String, String> lineNameMap) {
      return lineIds.stream().map(lineId -> {
         VhostLine.Bound bound = new VhostLine.Bound();
         bound.setId(lineId);
         bound.setName(lineNameMap.get(lineId));
         return bound;
      }).toList();
   }

   private List<String> resolveLineIds(List<String> lineIds) {
      if (CollUtil.isEmpty(lineIds)) {
         return List.of();
      } else {
         List<String> result = lineIds.stream().filter(CharSequenceUtil::isNotBlank).distinct().toList();
         if (CollUtil.isEmpty(result)) {
            return List.of();
         } else {
            Long count = this.vhostLineMapper.selectCount((Wrapper<VhostLine>)new LambdaQueryWrapper<VhostLine>().in(VhostLine::getId, result));
            if (count != null && count.intValue() == result.size()) {
               return result;
            } else {
               throw new CommonException("存在无效的虚拟主机线路", new Object[0]);
            }
         }
      }
   }

   private VhostMaster queryEntity(String id) {
      VhostMaster master = (VhostMaster)this.getById(id);
      if (ObjectUtil.isEmpty(master)) {
         throw new CommonException("虚拟主机主控不存在，id值为：{}", new Object[]{id});
      } else {
         return master;
      }
   }

   private VhostMaster requirePanelMaster(VhostMasterIdParam idParam) {
      VhostMaster master = this.queryEntity(idParam.getId());
      if (!StrUtil.isBlank(master.getPanelUrl()) && !StrUtil.isBlank(master.getApiKey())) {
         return master;
      } else {
         throw new CommonException("主控面板地址或密钥为空", new Object[0]);
      }
   }

   private void checkName(String name, String excludeId) {
      LambdaQueryWrapper<VhostMaster> queryWrapper = (LambdaQueryWrapper<VhostMaster>)new LambdaQueryWrapper<VhostMaster>().eq(VhostMaster::getName, name);
      if (StrUtil.isNotBlank(excludeId)) {
         queryWrapper.ne(VhostMaster::getId, excludeId);
      }

      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("主控名称已存在", new Object[0]);
      }
   }

   private void checkStatus(Integer status) {
      if (status != null && status != 0 && status != 1) {
         throw new CommonException("状态只能是启用或禁用", new Object[0]);
      }
   }

   private Integer defaultStatus(Integer status) {
      return status == null ? 1 : status;
   }

   private Integer defaultWeight(Integer weight) {
      return weight == null ? 1 : weight;
   }
}
