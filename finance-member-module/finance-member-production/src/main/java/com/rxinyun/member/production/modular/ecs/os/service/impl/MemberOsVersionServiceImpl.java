package com.rxinyun.member.production.modular.ecs.os.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsVersion;
import com.rxinyun.member.production.modular.ecs.os.mapper.MemberOsVersionMapper;
import com.rxinyun.member.production.modular.ecs.os.param.OsVersionIdParam;
import com.rxinyun.member.production.modular.ecs.os.param.OsVersionPageParam;
import com.rxinyun.member.production.modular.ecs.os.service.MemberOsVersionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberOsVersionServiceImpl extends ServiceImpl<MemberOsVersionMapper, MemberOsVersion> implements MemberOsVersionService {
   @Resource
   private MemberOsVersionMapper memberOsVersionMapper;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Page<MemberOsVersion> page(OsVersionPageParam pageParam) {
      QueryWrapper<MemberOsVersion> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberOsVersion::getOsName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOsClassId())) {
         queryWrapper.lambda().and(q -> q.eq(MemberOsVersion::getOsClassId, pageParam.getOsClassId()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getAvailableId())) {
         queryWrapper.lambda().and(q -> q.like(MemberOsVersion::getSupportedAvailabilityZone, pageParam.getAvailableId()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<MemberOsVersion> page = (Page<MemberOsVersion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      if (ObjectUtil.isNotEmpty(page.getRecords())) {
         page.getRecords().forEach(this::fillResourcePoolDisplay);
      }

      return page;
   }

   @Override
   public MemberOsVersion detail(OsVersionIdParam osVersionIdParam) {
      MemberOsVersion v = this.queryEntity(osVersionIdParam.getId());
      this.fillResourcePoolDisplay(v);
      return v;
   }

   private void fillResourcePoolDisplay(MemberOsVersion osVersion) {
      if (osVersion != null && !ObjectUtil.isEmpty(osVersion.getResourcePoolId())) {
         BaseResourcePool pool = this.resourcePoolApi.getById(osVersion.getResourcePoolId());
         if (ObjectUtil.isNotEmpty(pool)) {
            osVersion.setResourcePoolType(pool.getPoolType());
            osVersion.setResourcePoolName(pool.getName());
         }
      }
   }

   @Override
   public MemberOsVersion queryEntity(String id) {
      MemberOsVersion osVersion = (MemberOsVersion)this.getById(id);
      if (ObjectUtil.isEmpty(osVersion)) {
         throw new CommonException("系统版本不存在，id值为：{}", new Object[]{id});
      } else {
         return osVersion;
      }
   }
}
