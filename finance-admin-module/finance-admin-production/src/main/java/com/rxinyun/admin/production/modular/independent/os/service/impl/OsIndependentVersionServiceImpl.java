package com.rxinyun.admin.production.modular.independent.os.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsClass;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsVersion;
import com.rxinyun.admin.production.modular.independent.os.mapper.OsIndependentClassMapper;
import com.rxinyun.admin.production.modular.independent.os.mapper.OsIndependentVersionMapper;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionEditParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionIdParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionPageParam;
import com.rxinyun.admin.production.modular.independent.os.service.OsVersionService;
import com.rxinyun.admin.production.modular.util.ResourcePoolDisplayHelper;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.dev.api.ResourcePoolApi;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
   rollbackFor = {Exception.class}
)
@Service
public class OsIndependentVersionServiceImpl extends ServiceImpl<OsIndependentVersionMapper, IndependentOsVersion> implements OsVersionService {
   private static final Logger log = LoggerFactory.getLogger(OsIndependentVersionServiceImpl.class);
   @Resource
   private OsIndependentVersionMapper osIndependentVersionMapper;
   @Resource
   private OsIndependentClassMapper osIndependentClassMapper;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Boolean isBindLine(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("线路id为空", new Object[0]);
      } else {
         QueryWrapper<IndependentOsVersion> queryWrapper = (QueryWrapper<IndependentOsVersion>)new QueryWrapper()
            .apply("FIND_IN_SET({0}, REPLACE(line_id, ' ', '')) > 0", new Object[]{id});
         List<IndependentOsVersion> independentOsVersions = this.osIndependentVersionMapper.selectList(queryWrapper);
         return !independentOsVersions.isEmpty();
      }
   }

   public Boolean isBindType(String id) {
      if (ObjectUtil.isEmpty(id)) {
         throw new CommonException("操作系统类型id为空", new Object[0]);
      } else {
         return this.osIndependentVersionMapper.exists((Wrapper<IndependentOsVersion>)new LambdaQueryWrapper<IndependentOsVersion>().eq(IndependentOsVersion::getOsClassId, id));
      }
   }

   @Override
   public Page<IndependentOsVersion> page(OsVersionPageParam pageParam) {
      QueryWrapper<IndependentOsVersion> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(IndependentOsVersion::getOsName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOsClassId())) {
         queryWrapper.lambda().eq(IndependentOsVersion::getOsClassId, pageParam.getOsClassId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getLineId())) {
         queryWrapper.apply("FIND_IN_SET({0}, REPLACE(line_id, ' ', '')) > 0", new Object[]{pageParam.getLineId()});
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<IndependentOsVersion> page = (Page<IndependentOsVersion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);

      for (IndependentOsVersion row : page.getRecords()) {
         IndependentOsClass osClass = (IndependentOsClass)this.osIndependentClassMapper.selectById(row.getOsClassId());
         if (osClass != null) {
            row.setOsClassName(osClass.getOsClassName());
         }

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
      this.checkParam(osVersionAddParam);
      IndependentOsVersion osVersion = (IndependentOsVersion)BeanUtil.toBean(osVersionAddParam, IndependentOsVersion.class);
      this.save(osVersion);
   }

   private void checkParam(OsVersionAddParam newsDto) {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<OsVersionAddParam>> violations = validator.validate(newsDto, new Class[0]);
      if (!violations.isEmpty()) {
         StringBuilder errorMessage = new StringBuilder("参数验证失败: ");

         for (ConstraintViolation<OsVersionAddParam> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
         }

         throw new CommonException(errorMessage.toString(), new Object[0]);
      }
   }

   @Override
   public void edit(OsVersionEditParam param) {
      IndependentOsVersion existing = (IndependentOsVersion)this.getById(param.getId());
      if (existing == null) {
         throw new CommonException("记录不存在", new Object[0]);
      } else {
         IndependentOsVersion bean = (IndependentOsVersion)BeanUtil.toBean(param, IndependentOsVersion.class);
         bean.setUpdateTime(new Date());
         log.info("更新数据: {}", JSON.toJSONString(bean));
         this.updateById(bean);
      }
   }

   @Override
   public IndependentOsVersion detail(OsVersionIdParam osVersionIdParam) {
      return this.queryEntity(osVersionIdParam.getId());
   }

   @Override
   public IndependentOsVersion queryEntity(String id) {
      IndependentOsVersion osVersion = (IndependentOsVersion)this.getById(id);
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
         this.jdbcTemplate.update("DELETE FROM independent_os_version WHERE id IN (" + placeholders + ")", osVersionIds.toArray());
      }
   }
}
