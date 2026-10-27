package com.rxinyun.admin.production.modular.vhost.version.service.impl;

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
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.mapper.VhostLineMapper;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersion;
import com.rxinyun.admin.production.modular.vhost.version.mapper.VhostVersionMapper;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionAddParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionEditParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionIdParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionPageParam;
import com.rxinyun.admin.production.modular.vhost.version.service.VhostVersionLineService;
import com.rxinyun.admin.production.modular.vhost.version.service.VhostVersionService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostVersionServiceImpl extends ServiceImpl<VhostVersionMapper, VhostVersion> implements VhostVersionService {
   @Resource
   private VhostVersionLineService vhostVersionLineService;
   @Resource
   private VhostLineMapper vhostLineMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public Page<VhostVersion> page(VhostVersionPageParam pageParam) {
      QueryWrapper<VhostVersion> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(VhostVersion::getName, pageParam.getSearchKey());
      }

      if (pageParam.getStatus() != null) {
         queryWrapper.lambda().eq(VhostVersion::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         ((LambdaQueryWrapper<VhostVersion>)queryWrapper.lambda().orderByAsc(VhostVersion::getSortId)).orderByDesc(VhostVersion::getId);
      }

      Page<VhostVersion> page = (Page<VhostVersion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillLines(page.getRecords());
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(VhostVersionAddParam addParam) {
      this.checkStatus(addParam.getStatus());
      this.checkPrice(addParam.getMonthPrice(), addParam.getTrafficPrice());
      this.checkTraffic(addParam.getTrafficMinSize(), addParam.getTrafficMaxSize(), addParam.getTrafficFreeSize());
      this.checkName(addParam.getName(), null);
      List<String> lineIds = this.resolveLineIds(addParam.getLineIds());
      VhostVersion version = (VhostVersion)BeanUtil.toBean(addParam, VhostVersion.class);
      version.setPhpVersions(this.normalizeVersions(addParam.getPhpVersions()));
      version.setMysqlVersions(this.normalizeVersions(addParam.getMysqlVersions()));
      version.setSortId(this.defaultSort(addParam.getSortId()));
      version.setStatus(this.defaultStatus(addParam.getStatus()));
      this.save(version);
      this.vhostVersionLineService.replace(version.getId(), lineIds);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(VhostVersionEditParam editParam) {
      VhostVersion version = this.queryEntity(editParam.getId());
      this.checkStatus(editParam.getStatus());
      this.checkPrice(editParam.getMonthPrice(), editParam.getTrafficPrice());
      this.checkTraffic(editParam.getTrafficMinSize(), editParam.getTrafficMaxSize(), editParam.getTrafficFreeSize());
      this.checkName(editParam.getName(), editParam.getId());
      List<String> lineIds = this.resolveLineIds(editParam.getLineIds());
      BeanUtil.copyProperties(editParam, version, new String[0]);
      version.setPhpVersions(this.normalizeVersions(editParam.getPhpVersions()));
      version.setMysqlVersions(this.normalizeVersions(editParam.getMysqlVersions()));
      version.setDiscountTemplateId(StrUtil.trimToNull(editParam.getDiscountTemplateId()));
      version.setSortId(this.defaultSort(editParam.getSortId()));
      version.setStatus(this.defaultStatus(editParam.getStatus()));
      this.updateById(version);
      ((this.lambdaUpdate().eq(VhostVersion::getId, version.getId()))
            .set(VhostVersion::getDiscountTemplateId, version.getDiscountTemplateId()))
         .update();
      this.vhostVersionLineService.replace(version.getId(), lineIds);
   }

   @Override
   public VhostVersion detail(VhostVersionIdParam idParam) {
      VhostVersion version = this.queryEntity(idParam.getId());
      this.fillLines(List.of(version));
      return version;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<VhostVersionIdParam> idParamList) {
      List<String> ids = CollStreamUtil.toList(idParamList, VhostVersionIdParam::getId);
      if (!ObjectUtil.isEmpty(ids)) {
         this.vhostVersionLineService.physicalDeleteByVersionIds(ids);
         String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM VHOST_VERSION WHERE ID IN (" + placeholders + ")", ids.toArray());
      }
   }

   private void fillLines(List<VhostVersion> versions) {
      if (!CollUtil.isEmpty(versions)) {
         List<String> versionIds = versions.stream().map(VhostVersion::getId).toList();
         Map<String, List<String>> lineIdMap = this.vhostVersionLineService.groupLineIds(versionIds);
         Map<String, String> lineNameMap = this.loadLineNames(lineIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()));
         versions.forEach(version -> {
            List<String> lineIds = lineIdMap.getOrDefault(version.getId(), List.of());
            List<VhostLine.Bound> lines = this.toBoundLines(lineIds, lineNameMap);
            version.setLineIds(lineIds);
            version.setLines(lines);
            version.setLineNames(lines.stream().map(VhostLine.Bound::getName).filter(CharSequenceUtil::isNotBlank).collect(Collectors.joining(",")));
         });
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

   private VhostVersion queryEntity(String id) {
      VhostVersion version = (VhostVersion)this.getById(id);
      if (ObjectUtil.isEmpty(version)) {
         throw new CommonException("虚拟主机版本不存在，id值为：{}", new Object[]{id});
      } else {
         return version;
      }
   }

   private void checkName(String name, String excludeId) {
      LambdaQueryWrapper<VhostVersion> queryWrapper = (LambdaQueryWrapper<VhostVersion>)new LambdaQueryWrapper<VhostVersion>().eq(VhostVersion::getName, name);
      if (StrUtil.isNotBlank(excludeId)) {
         queryWrapper.ne(VhostVersion::getId, excludeId);
      }

      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("版本名称已存在", new Object[0]);
      }
   }

   private void checkPrice(BigDecimal monthPrice, BigDecimal trafficPrice) {
      if (monthPrice == null || monthPrice.compareTo(BigDecimal.ZERO) <= 0) {
         throw new CommonException("版本月价格必须大于0", new Object[0]);
      } else if (trafficPrice == null || trafficPrice.compareTo(BigDecimal.ZERO) <= 0) {
         throw new CommonException("流量价格必须大于0", new Object[0]);
      }
   }

   private void checkTraffic(Integer minSize, Integer maxSize, Integer freeSize) {
      if (minSize != null && maxSize != null && minSize > maxSize) {
         throw new CommonException("最小购买流量不能大于最大购买流量", new Object[0]);
      } else if (freeSize != null && minSize != null && freeSize > minSize) {
         throw new CommonException("赠送流量不能大于最小购买流量", new Object[0]);
      }
   }

   private String normalizeVersions(String versions) {
      if (StrUtil.isBlank(versions)) {
         return null;
      } else {
         String normalized = StrUtil.splitTrim(versions.replace('，', ','), ',')
            .stream()
            .filter(CharSequenceUtil::isNotBlank)
            .distinct()
            .collect(Collectors.joining(","));
         return StrUtil.isBlank(normalized) ? null : normalized;
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
