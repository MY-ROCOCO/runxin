package com.rxinyun.admin.production.modular.vhost.region.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.vhost.line.entity.VhostLine;
import com.rxinyun.admin.production.modular.vhost.line.mapper.VhostLineMapper;
import com.rxinyun.admin.production.modular.vhost.region.entity.VhostRegion;
import com.rxinyun.admin.production.modular.vhost.region.mapper.VhostRegionMapper;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionAddParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionEditParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionIdParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionPageParam;
import com.rxinyun.admin.production.modular.vhost.region.service.VhostRegionService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostRegionServiceImpl extends ServiceImpl<VhostRegionMapper, VhostRegion> implements VhostRegionService {
   @Resource
   private VhostLineMapper vhostLineMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public Page<VhostRegion> page(VhostRegionPageParam pageParam) {
      QueryWrapper<VhostRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(VhostRegion::getName, pageParam.getSearchKey());
      }

      if (pageParam.getStatus() != null) {
         queryWrapper.lambda().eq(VhostRegion::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         ((LambdaQueryWrapper<VhostRegion>)queryWrapper.lambda().orderByAsc(VhostRegion::getSortId)).orderByDesc(VhostRegion::getId);
      }

      return (Page<VhostRegion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(VhostRegionAddParam addParam) {
      this.checkStatus(addParam.getStatus());
      this.checkName(addParam.getName(), null);
      VhostRegion region = (VhostRegion)BeanUtil.toBean(addParam, VhostRegion.class);
      region.setSortId(this.defaultSort(addParam.getSortId()));
      region.setStatus(this.defaultStatus(addParam.getStatus()));
      this.save(region);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(VhostRegionEditParam editParam) {
      VhostRegion region = this.queryEntity(editParam.getId());
      this.checkStatus(editParam.getStatus());
      this.checkName(editParam.getName(), editParam.getId());
      BeanUtil.copyProperties(editParam, region, new String[0]);
      region.setSortId(this.defaultSort(editParam.getSortId()));
      region.setStatus(this.defaultStatus(editParam.getStatus()));
      this.updateById(region);
   }

   @Override
   public VhostRegion detail(VhostRegionIdParam idParam) {
      return this.queryEntity(idParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<VhostRegionIdParam> idParamList) {
      List<String> ids = CollStreamUtil.toList(idParamList, VhostRegionIdParam::getId);
      if (!ObjectUtil.isEmpty(ids)) {
         Long lineCount = this.vhostLineMapper.selectCount((Wrapper<VhostLine>)new LambdaQueryWrapper<VhostLine>().in(VhostLine::getRegionId, ids));
         if (lineCount != null && lineCount > 0L) {
            throw new CommonException("有线路绑定本地域，无法删除", new Object[0]);
         } else {
            String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
            this.jdbcTemplate.update("DELETE FROM VHOST_REGION WHERE ID IN (" + placeholders + ")", ids.toArray());
         }
      }
   }

   @Override
   public List<VhostRegion> selector() {
      return ((this.lambdaQuery().eq(VhostRegion::getStatus, 1)).orderByAsc(VhostRegion::getSortId)).list();
   }

   private VhostRegion queryEntity(String id) {
      VhostRegion region = (VhostRegion)this.getById(id);
      if (ObjectUtil.isEmpty(region)) {
         throw new CommonException("虚拟主机地域不存在，id值为：{}", new Object[]{id});
      } else {
         return region;
      }
   }

   private void checkName(String name, String excludeId) {
      LambdaQueryWrapper<VhostRegion> queryWrapper = (LambdaQueryWrapper<VhostRegion>)new LambdaQueryWrapper<VhostRegion>().eq(VhostRegion::getName, name);
      if (StrUtil.isNotBlank(excludeId)) {
         queryWrapper.ne(VhostRegion::getId, excludeId);
      }

      if (this.count(queryWrapper) > 0L) {
         throw new CommonException("地域名称已存在", new Object[0]);
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
