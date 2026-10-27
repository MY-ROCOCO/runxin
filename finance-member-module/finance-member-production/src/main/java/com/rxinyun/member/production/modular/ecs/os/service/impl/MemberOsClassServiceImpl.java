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
import com.rxinyun.member.production.modular.ecs.os.entity.MemberOsClass;
import com.rxinyun.member.production.modular.ecs.os.mapper.MemberOsClassMapper;
import com.rxinyun.member.production.modular.ecs.os.param.OsClassIdParam;
import com.rxinyun.member.production.modular.ecs.os.param.OsClassPageParam;
import com.rxinyun.member.production.modular.ecs.os.service.MemberOsClassService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberOsClassServiceImpl extends ServiceImpl<MemberOsClassMapper, MemberOsClass> implements MemberOsClassService {
   @Resource
   private MemberOsClassMapper memberOsClassMapper;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Page<MemberOsClass> page(OsClassPageParam pageParam) {
      QueryWrapper<MemberOsClass> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberOsClass::getOsClassName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<MemberOsClass> page = (Page<MemberOsClass>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      if (ObjectUtil.isNotEmpty(page.getRecords())) {
         page.getRecords().forEach(this::fillResourcePoolDisplay);
      }

      return page;
   }

   @Override
   public MemberOsClass detail(OsClassIdParam osClassIdParam) {
      MemberOsClass osClass = this.queryEntity(osClassIdParam.getId());
      this.fillResourcePoolDisplay(osClass);
      return osClass;
   }

   private void fillResourcePoolDisplay(MemberOsClass osClass) {
      if (osClass != null && !ObjectUtil.isEmpty(osClass.getResourcePoolId())) {
         BaseResourcePool pool = this.resourcePoolApi.getById(osClass.getResourcePoolId());
         if (ObjectUtil.isNotEmpty(pool)) {
            osClass.setResourcePoolType(pool.getPoolType());
            osClass.setResourcePoolName(pool.getName());
         }
      }
   }

   @Override
   public MemberOsClass queryEntity(String id) {
      MemberOsClass osClass = (MemberOsClass)this.getById(id);
      if (ObjectUtil.isEmpty(osClass)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return osClass;
      }
   }
}
