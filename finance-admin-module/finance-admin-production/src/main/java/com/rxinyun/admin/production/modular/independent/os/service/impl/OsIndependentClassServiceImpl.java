package com.rxinyun.admin.production.modular.independent.os.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsClass;
import com.rxinyun.admin.production.modular.independent.os.mapper.OsIndependentClassMapper;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassEditParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassIdParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassPageParam;
import com.rxinyun.admin.production.modular.independent.os.service.OsClassService;
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
import java.util.List;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(
   rollbackFor = {Exception.class}
)
@Service
public class OsIndependentClassServiceImpl extends ServiceImpl<OsIndependentClassMapper, IndependentOsClass> implements OsClassService {
   @Resource
   private OsIndependentClassMapper osIndependentClassMapper;
   @Resource
   private OsIndependentVersionServiceImpl osIndependentVersionService;
   @Resource
   private JdbcTemplate jdbcTemplate;
   @Resource
   private ResourcePoolApi resourcePoolApi;

   @Override
   public Page<IndependentOsClass> page(OsClassPageParam pageParam) {
      QueryWrapper<IndependentOsClass> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(IndependentOsClass::getOsClassName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<IndependentOsClass> page = (Page<IndependentOsClass>)this.page(CommonPageRequest.defaultPage(), queryWrapper);

      for (IndependentOsClass row : page.getRecords()) {
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
      this.checkParam(osClassAddParam);
      IndependentOsClass osClass = (IndependentOsClass)BeanUtil.toBean(osClassAddParam, IndependentOsClass.class);
      this.osIndependentClassMapper.insert(osClass);
   }

   private void checkParam(OsClassAddParam newsDto) {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();
      Set<ConstraintViolation<OsClassAddParam>> violations = validator.validate(newsDto, new Class[0]);
      if (!violations.isEmpty()) {
         StringBuilder errorMessage = new StringBuilder("参数验证失败: ");

         for (ConstraintViolation<OsClassAddParam> violation : violations) {
            errorMessage.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("; ");
         }

         throw new CommonException(errorMessage.toString(), new Object[0]);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(OsClassEditParam osClassEditParam) {
      IndependentOsClass osClass = this.queryEntity(osClassEditParam.getId());
      BeanUtil.copyProperties(osClassEditParam, osClass, new String[0]);
      this.updateById(osClass);
   }

   @Override
   public IndependentOsClass detail(OsClassIdParam osClassIdParam) {
      return this.queryEntity(osClassIdParam.getId());
   }

   @Override
   public IndependentOsClass queryEntity(String id) {
      IndependentOsClass osClass = (IndependentOsClass)this.getById(id);
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
         for (String osClassId : osClassIds) {
            if (this.osIndependentVersionService.isBindType(osClassId)) {
               throw new CommonException("当有操作系统版本绑定操作系统类别时不允许删除", new Object[0]);
            }

            this.jdbcTemplate.update("DELETE FROM independent_os_class WHERE id = ?", new Object[]{osClassId});
         }
      }
   }
}
