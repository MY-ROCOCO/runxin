package com.rxinyun.admin.production.modular.esc.os.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.os.entity.OsVersion;
import com.rxinyun.admin.production.modular.esc.os.mapper.OsVersionMapper;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionEditParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionIdParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionPageParam;
import com.rxinyun.admin.production.modular.esc.os.service.OsVersionService;
import com.rxinyun.admin.production.modular.util.ResourcePoolDisplayHelper;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.ResourcePoolApi;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OsVersionServiceImpl extends ServiceImpl<OsVersionMapper, OsVersion> implements OsVersionService {
   @Resource
   private OsVersionMapper osVersionMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Page<OsVersion> page(OsVersionPageParam pageParam) {
      QueryWrapper<OsVersion> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(OsVersion::getOsName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOsClassId())) {
         queryWrapper.lambda().and(q -> q.eq(OsVersion::getOsClassId, pageParam.getOsClassId()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getAvailableId())) {
         queryWrapper.lambda().and(q -> q.like(OsVersion::getSupportedAvailabilityZone, pageParam.getAvailableId()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<OsVersion> page = (Page<OsVersion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);

      for (OsVersion row : page.getRecords()) {
         ResourcePoolDisplayHelper.ifPresent(this.resourcePoolApi, row.getResourcePoolId(), pool -> {
            row.setResourcePoolType(pool.getPoolType());
            row.setResourcePoolName(pool.getName());
         });
      }

      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(OsVersionAddParam osVersionAddParam) {
      OsVersion osVersion = (OsVersion)BeanUtil.toBean(osVersionAddParam, OsVersion.class);
      this.save(osVersion);
   }

   @Override
   public void edit(OsVersionEditParam osVersionEditParam) {
      OsVersion osVersion = this.queryEntity(osVersionEditParam.getId());
      if (ObjectUtil.equal(osVersion.getType(), 1)) {
         throw new CommonException("资源池类型的操作系统版本不允许编辑", new Object[0]);
      } else {
         BeanUtil.copyProperties(osVersionEditParam, osVersion, true);
         this.update(osVersion, (Wrapper)new QueryWrapper().eq("id", osVersion.getId()));
      }
   }

   @Override
   public OsVersion detail(OsVersionIdParam osVersionIdParam) {
      return this.queryEntity(osVersionIdParam.getId());
   }

   @Override
   public OsVersion queryEntity(String id) {
      OsVersion osVersion = (OsVersion)this.getById(id);
      if (ObjectUtil.isEmpty(osVersion)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return osVersion;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<OsVersionIdParam> osVersionIdParamList) {
      List<String> osVersionIds = CollStreamUtil.toList(osVersionIdParamList, OsVersionIdParam::getId);
      if (ObjectUtil.isNotEmpty(osVersionIds)) {
         String placeholders = String.join(",", Collections.nCopies(osVersionIds.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM OS_VERSION WHERE id IN (" + placeholders + ")", osVersionIds.toArray());
      }
   }
}
