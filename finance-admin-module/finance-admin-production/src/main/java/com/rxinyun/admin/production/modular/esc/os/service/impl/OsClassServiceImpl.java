package com.rxinyun.admin.production.modular.esc.os.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.os.entity.OsClass;
import com.rxinyun.admin.production.modular.esc.os.entity.OsVersion;
import com.rxinyun.admin.production.modular.esc.os.mapper.OsClassMapper;
import com.rxinyun.admin.production.modular.esc.os.mapper.OsVersionMapper;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassEditParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassIdParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassPageParam;
import com.rxinyun.admin.production.modular.esc.os.service.OsClassService;
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
public class OsClassServiceImpl extends ServiceImpl<OsClassMapper, OsClass> implements OsClassService {
   @Resource
   private OsClassMapper osClassMapper;
   @Resource
   private OsVersionMapper osVersionMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Page<OsClass> page(OsClassPageParam pageParam) {
      QueryWrapper<OsClass> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(OsClass::getOsClassName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<OsClass> page = (Page<OsClass>)this.page(CommonPageRequest.defaultPage(), queryWrapper);

      for (OsClass row : page.getRecords()) {
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
   public void add(OsClassAddParam osClassAddParam) {
      OsClass osClass = (OsClass)BeanUtil.toBean(osClassAddParam, OsClass.class);
      this.save(osClass);
   }

   @Override
   public void edit(OsClassEditParam osClassEditParam) {
      OsClass osClass = this.queryEntity(osClassEditParam.getId());
      if (ObjectUtil.equal(osClass.getType(), 1)) {
         throw new CommonException("资源池类型的操作系统类别不允许编辑", new Object[0]);
      } else {
         BeanUtil.copyProperties(osClassEditParam, osClass, new String[0]);
         this.updateById(osClass);
      }
   }

   @Override
   public OsClass detail(OsClassIdParam osClassIdParam) {
      return this.queryEntity(osClassIdParam.getId());
   }

   @Override
   public OsClass queryEntity(String id) {
      OsClass osClass = (OsClass)this.getById(id);
      if (ObjectUtil.isEmpty(osClass)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return osClass;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<OsClassIdParam> osClassIdParamList) {
      List<String> osClassIds = CollStreamUtil.toList(osClassIdParamList, OsClassIdParam::getId);
      if (ObjectUtil.isNotEmpty(osClassIds)) {
         long boundVersionCount = this.osVersionMapper.selectCount((Wrapper<OsVersion>)new LambdaQueryWrapper<OsVersion>().in(OsVersion::getOsClassId, osClassIds));
         if (boundVersionCount > 0L) {
            throw new CommonException("该操作系统类别下存在操作系统版本，无法删除", new Object[0]);
         }

         String placeholders = String.join(",", Collections.nCopies(osClassIds.size(), "?"));
         this.jdbcTemplate.update("DELETE FROM OS_CLASS WHERE id IN (" + placeholders + ")", osClassIds.toArray());
      }
   }
}
