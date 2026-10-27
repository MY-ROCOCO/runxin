package com.rxinyun.admin.production.modular.esc.available.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.available.entity.ServerAvailableRegion;
import com.rxinyun.admin.production.modular.esc.available.mapper.ServerAvailableRegionMapper;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionAddParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionEditParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionEditSortParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionIdParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionPageParam;
import com.rxinyun.admin.production.modular.esc.available.service.ServerAvailableRegionService;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLocalLineService;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerAvailableRegionServiceImpl extends ServiceImpl<ServerAvailableRegionMapper, ServerAvailableRegion> implements ServerAvailableRegionService {
   @Resource
   private ServerAvailableRegionMapper serverAvailableRegionMapper;
   @Resource
   private ServerLocalLineService serverLocalLineService;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public Page<ServerAvailableRegion> page(ServerAvailableRegionPageParam pageParam) {
      Page<ServerAvailableRegion> page = new Page(pageParam.getCurrent().intValue(), pageParam.getSize().intValue());
      Page<ServerAvailableRegion> pageResult = this.serverAvailableRegionMapper.selectPage(page, pageParam);
      if (ObjectUtil.isNotEmpty(pageResult.getRecords())) {
         for (ServerAvailableRegion availableRegion : pageResult.getRecords()) {
            if (ObjectUtil.isNotEmpty(availableRegion.getResourcePoolId())) {
               BaseResourcePool resourcePool = this.resourcePoolApi.getById(availableRegion.getResourcePoolId());
               if (ObjectUtil.isNotEmpty(resourcePool)) {
                  availableRegion.setResourcePoolType(resourcePool.getPoolType());
                  availableRegion.setResourcePoolName(resourcePool.getName());
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
   public void add(ServerAvailableRegionAddParam serverAvailableRegionAddParam) {
      ServerAvailableRegion serverAvailableRegion = (ServerAvailableRegion)BeanUtil.toBean(serverAvailableRegionAddParam, ServerAvailableRegion.class);
      this.save(serverAvailableRegion);
   }

   @Override
   public void edit(ServerAvailableRegionEditParam serverAvailableRegionEditParam) {
      ServerAvailableRegion serverAvailableRegion = this.queryEntity(serverAvailableRegionEditParam.getId());
      BeanUtil.copyProperties(serverAvailableRegionEditParam, serverAvailableRegion, new String[0]);
      this.updateById(serverAvailableRegion);
   }

   @Override
   public ServerAvailableRegion detail(ServerAvailableRegionIdParam serverAvailableRegionIdParam) {
      return this.queryEntity(serverAvailableRegionIdParam.getId());
   }

   @Override
   public ServerAvailableRegion queryEntity(String id) {
      ServerAvailableRegion serverAvailableRegion = (ServerAvailableRegion)this.getById(id);
      if (ObjectUtil.isEmpty(serverAvailableRegion)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverAvailableRegion;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ServerAvailableRegionIdParam> serverAvailableRegionIdParamList) {
      List<String> serverAvailableRegionIds = CollStreamUtil.toList(serverAvailableRegionIdParamList, ServerAvailableRegionIdParam::getId);
      if (!ObjectUtil.isEmpty(serverAvailableRegionIds)) {
         List<String> bindingAvailableRegionNames = new ArrayList<>();
         List<String> canDeleteIds = new ArrayList<>();

         for (String id : serverAvailableRegionIds) {
            if (this.serverLocalLineService.isBindingAvailable(id)) {
               try {
                  ServerAvailableRegion availableRegion = this.queryEntity(id);
                  bindingAvailableRegionNames.add(availableRegion.getAvailableName());
               } catch (Exception var8) {
                  bindingAvailableRegionNames.add(id);
               }
            } else {
               canDeleteIds.add(id);
            }
         }

         if (!bindingAvailableRegionNames.isEmpty()) {
            throw new CommonException("以下可用区已绑定线路，无法删除：{}", new Object[]{String.join("、", bindingAvailableRegionNames)});
         } else {
            if (!canDeleteIds.isEmpty()) {
               String placeholders = String.join(",", Collections.nCopies(canDeleteIds.size(), "?"));
               this.jdbcTemplate.update("DELETE FROM SERVER_AVAILABLE_REGION WHERE id IN (" + placeholders + ")", canDeleteIds.toArray());
            }
         }
      }
   }

   @Override
   public Boolean isBindingRegion(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("地域id为空", new Object[0]);
      } else {
         QueryWrapper<ServerAvailableRegion> queryWrapper = (QueryWrapper<ServerAvailableRegion>)new QueryWrapper().eq("region_id", id);
         List<ServerAvailableRegion> availableRegions = this.serverAvailableRegionMapper.selectList(queryWrapper);
         return !availableRegions.isEmpty();
      }
   }

   @Override
   public void updateSort(ServerAvailableRegionEditSortParam serverAvailableRegionEditSortParam) {
      ServerAvailableRegion serverAvailableRegion = this.queryEntity(serverAvailableRegionEditSortParam.getId());
      serverAvailableRegion.setSortId(serverAvailableRegionEditSortParam.getSortId());
      this.updateById(serverAvailableRegion);
   }
}
