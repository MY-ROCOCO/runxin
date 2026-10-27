package com.rxinyun.admin.production.modular.esc.region.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.available.service.ServerAvailableRegionService;
import com.rxinyun.admin.production.modular.esc.region.entity.ServerRegion;
import com.rxinyun.admin.production.modular.esc.region.mapper.ServerRegionMapper;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionAddParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionEditParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionEditSortParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionIdParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionPageParam;
import com.rxinyun.admin.production.modular.esc.region.service.ServerRegionService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerRegionServiceImpl extends ServiceImpl<ServerRegionMapper, ServerRegion> implements ServerRegionService {
   @Resource
   private ServerRegionMapper serverRegionMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   ServerAvailableRegionService serverAvailableRegionService;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Page<ServerRegion> page(ServerRegionPageParam pageParam) {
      QueryWrapper<ServerRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(ServerRegion::getRegionName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByAsc(ServerRegion::getSortId);
      }

      Page<ServerRegion> pageResult = (Page<ServerRegion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      if (ObjectUtil.isNotEmpty(pageResult.getRecords())) {
         for (ServerRegion region : pageResult.getRecords()) {
            if (ObjectUtil.isNotEmpty(region.getResourcePoolId())) {
               BaseResourcePool resourcePool = this.resourcePoolApi.getById(region.getResourcePoolId());
               if (ObjectUtil.isNotEmpty(resourcePool)) {
                  region.setResourcePoolType(resourcePool.getPoolType());
                  region.setResourcePoolName(resourcePool.getName());
               }
            }
         }
      }

      return pageResult;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ServerRegionAddParam serverRegionAddParam) {
      ServerRegion serverRegion = (ServerRegion)BeanUtil.toBean(serverRegionAddParam, ServerRegion.class);
      this.save(serverRegion);
   }

   @Override
   public void edit(ServerRegionEditParam serverRegionEditParam) {
      ServerRegion serverRegion = this.queryEntity(serverRegionEditParam.getId());
      BeanUtil.copyProperties(serverRegionEditParam, serverRegion, new String[0]);
      this.updateById(serverRegion);
   }

   @Override
   public ServerRegion detail(ServerRegionIdParam serverRegionIdParam) {
      return this.queryEntity(serverRegionIdParam.getId());
   }

   @Override
   public ServerRegion queryEntity(String id) {
      ServerRegion serverRegion = (ServerRegion)this.getById(id);
      if (ObjectUtil.isEmpty(serverRegion)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverRegion;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerRegionIdParam> serverRegionIdParamList) {
      List<String> serverRegionIds = CollStreamUtil.toList(serverRegionIdParamList, ServerRegionIdParam::getId);
      if (ObjectUtil.isNotEmpty(serverRegionIds)) {
         boolean anyBoundToHost = serverRegionIds.stream().anyMatch(id -> this.serverAvailableRegionService.isBindingRegion(id));
         if (anyBoundToHost) {
            throw new CommonException("有可用区绑定本地域，无法删除", new Object[0]);
         }

         String placeholders = String.join(",", Collections.nCopies(serverRegionIds.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM SERVER_REGION WHERE id IN (" + placeholders + ")", serverRegionIds.toArray());
      }
   }

   @Override
   public void updateSort(ServerRegionEditSortParam serverRegionEditSortParam) {
      ServerRegion serverRegion = this.queryEntity(serverRegionEditSortParam.getId());
      serverRegion.setSortId(serverRegionEditSortParam.getSortId());
      this.updateById(serverRegion);
   }
}
