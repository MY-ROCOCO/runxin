package com.rxinyun.admin.dev.modular.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.mapper.ResourcePoolMapper;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolAddParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolEditParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolIdParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolPageParam;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolStatusParam;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourcePoolServiceImpl extends ServiceImpl<ResourcePoolMapper, ResourcePool> implements ResourcePoolService {
   @Resource
   private JdbcTemplate jdbcTemplate;

   @Override
   public Page<ResourcePool> page(ResourcePoolPageParam resourcePoolPageParam) {
      QueryWrapper<ResourcePool> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(resourcePoolPageParam.getPoolType())) {
         queryWrapper.lambda().eq(ResourcePool::getPoolType, resourcePoolPageParam.getPoolType());
      }

      if (ObjectUtil.isNotEmpty(resourcePoolPageParam.getSearchKey())) {
         queryWrapper.lambda().like(ResourcePool::getName, resourcePoolPageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(resourcePoolPageParam.getStatus())) {
         queryWrapper.lambda().eq(ResourcePool::getStatus, resourcePoolPageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{resourcePoolPageParam.getSortField(), resourcePoolPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(resourcePoolPageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            resourcePoolPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(resourcePoolPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<ResourcePool>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ResourcePoolAddParam resourcePoolAddParam) {
      boolean exists = this.count((Wrapper<ResourcePool>)new LambdaQueryWrapper<ResourcePool>().eq(ResourcePool::getPoolType, resourcePoolAddParam.getPoolType())) > 0L;
      if (exists) {
         throw new CommonException("该资源池类型已存在，资源池类型为：{}", new Object[]{resourcePoolAddParam.getPoolType()});
      } else {
         String apiSecretEncrypted = CommonCryptogramUtil.doSm4CbcEncrypt(resourcePoolAddParam.getApiSecret());
         ResourcePool resourcePool = (ResourcePool)BeanUtil.toBean(resourcePoolAddParam, ResourcePool.class);
         resourcePool.setId(SnowflakeIdGenerator.generateId());
         resourcePool.setApiSecret(apiSecretEncrypted);
         resourcePool.setStatus(1);
         String loginUserId = StpLoginUserUtil.getLoginUser().getId();
         resourcePool.setCreateUser(loginUserId);
         this.save(resourcePool);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(ResourcePoolEditParam resourcePoolEditParam) {
      ResourcePool resourcePool = this.queryEntity(resourcePoolEditParam.getId());
      if (!resourcePool.getPoolType().equals(resourcePoolEditParam.getPoolType())) {
         boolean exists = this.count(
               (Wrapper<ResourcePool>)((LambdaQueryWrapper<ResourcePool>)new LambdaQueryWrapper<ResourcePool>().eq(ResourcePool::getPoolType, resourcePoolEditParam.getPoolType()))
                  .ne(ResourcePool::getId, resourcePoolEditParam.getId())
            )
            > 0L;
         if (exists) {
            throw new CommonException("该资源池类型已存在，资源池类型为：{}", new Object[]{resourcePoolEditParam.getPoolType()});
         }
      }

      BeanUtil.copyProperties(resourcePoolEditParam, resourcePool, new String[]{"id", "apiSecret"});
      if (StrUtil.isNotBlank(resourcePoolEditParam.getApiSecret())) {
         String apiSecretEncrypted = CommonCryptogramUtil.doSm4CbcEncrypt(resourcePoolEditParam.getApiSecret());
         resourcePool.setApiSecret(apiSecretEncrypted);
      }

      String loginUserId = StpLoginUserUtil.getLoginUser().getId();
      resourcePool.setUpdateUser(loginUserId);
      this.updateById(resourcePool);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ResourcePoolIdParam> resourcePoolIdParamList) {
      List<String> resourcePoolIdList = CollStreamUtil.toList(resourcePoolIdParamList, ResourcePoolIdParam::getId);
      if (ObjectUtil.isNotEmpty(resourcePoolIdList)) {
         for (String resourcePoolId : resourcePoolIdList) {
            this.validateNoProductBound(resourcePoolId);
         }

         this.removeByIds(resourcePoolIdList);
      }
   }

   private void validateNoProductBound(String resourcePoolId) {
      if (!StrUtil.isBlank(resourcePoolId)) {
         ResourcePoolServiceImpl.ResourcePoolBindingCheck[] checks = new ResourcePoolServiceImpl.ResourcePoolBindingCheck[]{
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("SERVER_LOCAL_LINE", "云服务器线路", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("SERVER_AVAILABLE_REGION", "云服务器可用区", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("SERVER_REGION", "云服务器地域", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("OS_CLASS", "云服务器操作系统类别", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("OS_VERSION", "云服务器操作系统版本", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("INDEPENDENT_LINE", "独立服务器线路", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("INDEPENDENT_MACHINE", "独立服务器机型", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("INDEPENDENT_REGION", "独立服务器地域", false),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("INDEPENDENT_OS_CLASS", "独立服务器操作系统类别", true),
            new ResourcePoolServiceImpl.ResourcePoolBindingCheck("INDEPENDENT_OS_VERSION", "独立服务器操作系统版本", true)
         };

         for (ResourcePoolServiceImpl.ResourcePoolBindingCheck check : checks) {
            if (this.countResourcePoolBinding(check, resourcePoolId) > 0L) {
               throw new CommonException("该资源池下存在{}，不允许删除", new Object[]{check.name});
            }
         }
      }
   }

   private long countResourcePoolBinding(ResourcePoolServiceImpl.ResourcePoolBindingCheck check, String resourcePoolId) {
      String sql = "SELECT COUNT(1) FROM " + check.tableName + " WHERE RESOURCE_POOL_ID = ?";
      if (check.hasDeleteFlag) {
         sql = sql + " AND DELETE_FLAG = 'NOT_DELETE'";
      }

      Long count = (Long)this.jdbcTemplate.queryForObject(sql, Long.class, new Object[]{resourcePoolId});
      return count != null ? count : 0L;
   }

   @Override
   public ResourcePool detail(ResourcePoolIdParam resourcePoolIdParam) {
      return this.queryEntity(resourcePoolIdParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void changeStatus(ResourcePoolStatusParam resourcePoolStatusParam) {
      ResourcePool resourcePool = this.queryEntity(resourcePoolStatusParam.getId());
      resourcePool.setStatus(resourcePoolStatusParam.getStatus());
      String loginUserId = StpLoginUserUtil.getLoginUser().getId();
      resourcePool.setUpdateUser(loginUserId);
      this.updateById(resourcePool);
   }

   @Override
   public ResourcePool getByPoolType(String poolType) {
      return (ResourcePool)this.getOne(
         (Wrapper<ResourcePool>)((LambdaQueryWrapper<ResourcePool>)new LambdaQueryWrapper<ResourcePool>().eq(ResourcePool::getPoolType, poolType)).eq(ResourcePool::getStatus, 1)
      );
   }

   @Override
   public ResourcePool getByIdForApi(String resourcePoolId) {
      ResourcePool resourcePool = (ResourcePool)super.getById(resourcePoolId);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在，资源池 ID 为：{}", new Object[]{resourcePoolId});
      } else if (resourcePool.getStatus() != 1) {
         throw new CommonException("资源池已禁用，资源池 ID 为：{}", new Object[]{resourcePoolId});
      } else {
         return resourcePool;
      }
   }

   @Override
   public List<ResourcePool> listEnabled() {
      return this.list((Wrapper<ResourcePool>)new LambdaQueryWrapper<ResourcePool>().eq(ResourcePool::getStatus, 1));
   }

   @Override
   public boolean validateResourcePoolApiCredentials(String apiKey, String apiSecretPlain) {
      if (!StrUtil.isBlank(apiKey) && apiSecretPlain != null) {
         ResourcePool pool = (ResourcePool)this.getOne(
            (Wrapper<ResourcePool>)((LambdaQueryWrapper<ResourcePool>)new LambdaQueryWrapper<ResourcePool>().eq(ResourcePool::getApiKey, apiKey)).eq(ResourcePool::getStatus, 1)
         );
         if (pool == null) {
            return false;
         } else {
            String decrypted = CommonCryptogramUtil.doSm4CbcDecrypt(pool.getApiSecret());
            return StrUtil.equals(apiSecretPlain, decrypted);
         }
      } else {
         return false;
      }
   }

   public ResourcePool queryEntity(String id) {
      ResourcePool resourcePool = (ResourcePool)this.getById(id);
      if (ObjectUtil.isEmpty(resourcePool)) {
         throw new CommonException("资源池不存在，id 值为：{}", new Object[]{id});
      } else {
         return resourcePool;
      }
   }

   private static class ResourcePoolBindingCheck {
      private final String tableName;
      private final String name;
      private final boolean hasDeleteFlag;

      private ResourcePoolBindingCheck(String tableName, String name, boolean hasDeleteFlag) {
         this.tableName = tableName;
         this.name = name;
         this.hasDeleteFlag = hasDeleteFlag;
      }
   }
}
