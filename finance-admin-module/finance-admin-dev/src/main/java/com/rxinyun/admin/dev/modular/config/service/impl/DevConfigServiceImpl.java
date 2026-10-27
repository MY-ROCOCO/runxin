package com.rxinyun.admin.dev.modular.config.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.config.entity.DevConfig;
import com.rxinyun.admin.dev.modular.config.enums.DevConfigCategoryEnum;
import com.rxinyun.admin.dev.modular.config.mapper.DevConfigMapper;
import com.rxinyun.admin.dev.modular.config.param.DevConfigAddParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigBatchParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigEditParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigIdParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigKeyEditParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigListParam;
import com.rxinyun.admin.dev.modular.config.param.DevConfigPageParam;
import com.rxinyun.admin.dev.modular.config.service.DevConfigService;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DevConfigServiceImpl extends ServiceImpl<DevConfigMapper, DevConfig> implements DevConfigService {
   private static final String CONFIG_CACHE_KEY = "dev-config:";
   private static final String RUNXIN_SYS_DEFAULT_PASSWORD_KEY = "RUNXIN_SYS_DEFAULT_PASSWORD";
   @Resource
   private CommonCacheOperator commonCacheOperator;

   @Override
   public String getValueByKey(String key) {
      Object cacheValue = this.commonCacheOperator.get("dev-config:" + key);
      if (ObjectUtil.isNotEmpty(cacheValue)) {
         return Convert.toStr(cacheValue);
      } else {
         DevConfig devConfig = (DevConfig)this.getOne((Wrapper<DevConfig>)new LambdaQueryWrapper<DevConfig>().eq(DevConfig::getConfigKey, key));
         if (ObjectUtil.isNotEmpty(devConfig)) {
            this.commonCacheOperator.put("dev-config:" + key, devConfig.getConfigValue());
            return devConfig.getConfigValue();
         } else {
            return null;
         }
      }
   }

   @Override
   public String getValueDetailByKey(String key) {
      DevConfig devConfig = (DevConfig)this.getOne((Wrapper<DevConfig>)new LambdaQueryWrapper<DevConfig>().eq(DevConfig::getConfigKey, key));
      if (ObjectUtil.isNotEmpty(devConfig)) {
         this.commonCacheOperator.put("dev-config:" + key, devConfig.getConfigValue());
         return devConfig.getConfigValue();
      } else {
         return null;
      }
   }

   @Override
   public Page<DevConfig> page(DevConfigPageParam devConfigPageParam) {
      QueryWrapper<DevConfig> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda()
         .select(
            
               DevConfig::getId,
               DevConfig::getConfigKey,
               DevConfig::getConfigValue,
               DevConfig::getCategory,
               DevConfig::getRemark,
               DevConfig::getSortCode,
               DevConfig::getRemark,
               DevConfig::getRemark
            
         );
      if (ObjectUtil.isNotEmpty(devConfigPageParam.getSearchKey())) {
         queryWrapper.lambda().like(DevConfig::getConfigKey, devConfigPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{devConfigPageParam.getSortField(), devConfigPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(devConfigPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, devConfigPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(devConfigPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(DevConfig::getSortCode);
      }

      queryWrapper.lambda().eq(DevConfig::getCategory, DevConfigCategoryEnum.BIZ_DEFINE.getValue());
      return (Page<DevConfig>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<DevConfig> sysBaseList() {
      DevConfigListParam devConfigListParam = new DevConfigListParam();
      devConfigListParam.setCategory(DevConfigCategoryEnum.SYS_BASE.getValue());
      return this.list(devConfigListParam)
         .stream()
         .filter(devConfig -> !devConfig.getConfigKey().equals("RUNXIN_SYS_DEFAULT_PASSWORD"))
         .collect(Collectors.toList());
   }

   @Override
   public List<DevConfig> list(DevConfigListParam devConfigListParam) {
      LambdaQueryWrapper<DevConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         
            DevConfig::getId,
            DevConfig::getConfigKey,
            DevConfig::getConfigValue,
            DevConfig::getCategory,
            DevConfig::getSortCode,
            DevConfig::getRemark,
            DevConfig::getRemark
         
      );
      if (ObjectUtil.isNotEmpty(devConfigListParam.getCategory())) {
         lambdaQueryWrapper.eq(DevConfig::getCategory, devConfigListParam.getCategory());
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public void add(DevConfigAddParam devConfigAddParam) {
      this.checkParam(devConfigAddParam);
      DevConfig devConfig = (DevConfig)BeanUtil.toBean(devConfigAddParam, DevConfig.class);
      devConfig.setCategory(DevConfigCategoryEnum.BIZ_DEFINE.getValue());
      this.save(devConfig);
   }

   private void checkParam(DevConfigAddParam devConfigAddParam) {
      boolean hasSameConfig = this.count((Wrapper<DevConfig>)new LambdaQueryWrapper<DevConfig>().eq(DevConfig::getConfigKey, devConfigAddParam.getConfigKey())) > 0L;
      if (hasSameConfig) {
         throw new CommonException("存在重复的配置，配置键为：{}", new Object[]{devConfigAddParam.getConfigKey()});
      }
   }

   @Override
   public void edit(DevConfigEditParam devConfigEditParam) {
      DevConfig devConfig = this.queryEntity(devConfigEditParam.getId());
      this.checkParam(devConfigEditParam);
      BeanUtil.copyProperties(devConfigEditParam, devConfig, new String[0]);
      devConfig.setCategory(devConfigEditParam.getCategory());
      this.updateById(devConfig);
      this.commonCacheOperator.remove(new String[]{"dev-config:" + devConfig.getConfigKey()});
   }

   private void checkParam(DevConfigEditParam devConfigEditParam) {
      boolean hasSameConfig = this.count(
            (Wrapper<DevConfig>)((LambdaQueryWrapper<DevConfig>)new LambdaQueryWrapper<DevConfig>().eq(DevConfig::getConfigKey, devConfigEditParam.getConfigKey()))
               .ne(DevConfig::getId, devConfigEditParam.getId())
         )
         > 0L;
      if (hasSameConfig) {
         throw new CommonException("存在重复的配置，配置键为：{}", new Object[]{devConfigEditParam.getConfigKey()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<DevConfigIdParam> devConfigIdParamList) {
      List<String> devConfigIdList = CollStreamUtil.toList(devConfigIdParamList, DevConfigIdParam::getId);
      if (ObjectUtil.isNotEmpty(devConfigIdList)) {
         List<DevConfig> devConfigList = this.listByIds(devConfigIdList);
         if (ObjectUtil.isNotEmpty(devConfigList)) {
            List<String> devConfigResultList = CollectionUtil.newArrayList(devConfigList.stream().map(DevConfig::getCategory).collect(Collectors.toSet()));
            if (devConfigResultList.size() != 1 || !devConfigResultList.get(0).equals(DevConfigCategoryEnum.BIZ_DEFINE.getValue())) {
               throw new CommonException("不可删除系统内置配置", new Object[0]);
            }

            List<DevConfig> deleteDevConfigList = this.listByIds(devConfigIdList);
            this.removeByIds(devConfigIdList);
            deleteDevConfigList.forEach(devConfig -> this.commonCacheOperator.remove(new String[]{"dev-config:" + devConfig.getConfigKey()}));
         }
      }
   }

   @Override
   public DevConfig detail(DevConfigIdParam devConfigIdParam) {
      return this.queryEntity(devConfigIdParam.getId());
   }

   @Override
   public DevConfig queryEntity(String id) {
      DevConfig devConfig = (DevConfig)this.getById(id);
      if (ObjectUtil.isEmpty(devConfig)) {
         throw new CommonException("配置不存在，id值为：{}", new Object[]{id});
      } else {
         return devConfig;
      }
   }

   @Override
   public DevConfig queryByConfigKey(String configKey) {
      DevConfig devConfig = (DevConfig)this.getOne((Wrapper<DevConfig>)new LambdaQueryWrapper<DevConfig>().eq(DevConfig::getConfigKey, configKey));
      if (ObjectUtil.isEmpty(devConfig)) {
         throw new CommonException("配置不存在，configKey值为：{}", new Object[]{configKey});
      } else {
         return devConfig;
      }
   }

   @Override
   public void editBatch(List<DevConfigBatchParam> devConfigBatchParamList) {
      devConfigBatchParamList.forEach(
         devConfigBatchParam -> {
            this.update(
               (Wrapper<DevConfig>)((LambdaUpdateWrapper<DevConfig>)new LambdaUpdateWrapper<DevConfig>().eq(DevConfig::getConfigKey, devConfigBatchParam.getConfigKey()))
                  .set(DevConfig::getConfigValue, devConfigBatchParam.getConfigValue())
            );
            this.commonCacheOperator.remove(new String[]{"dev-config:" + devConfigBatchParam.getConfigKey()});
         }
      );
   }

   @Override
   public void updateByConfigKey(DevConfigKeyEditParam devConfigKeyEditParam) {
      String configKey = devConfigKeyEditParam.getConfigKey();
      String configValue = devConfigKeyEditParam.getConfigValue();
      boolean updated = this.update(
         (Wrapper<DevConfig>)((LambdaUpdateWrapper<DevConfig>)new LambdaUpdateWrapper<DevConfig>().eq(DevConfig::getConfigKey, configKey)).set(DevConfig::getConfigValue, configValue)
      );
      if (!updated) {
         throw new CommonException("配置不存在，configKey值为：{}", new Object[]{configKey});
      } else {
         this.commonCacheOperator.remove(new String[]{"dev-config:" + configKey});
      }
   }
}
