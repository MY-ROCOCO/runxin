package com.rxinyun.member.tools.modular.config.service.impl;

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
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.tools.modular.config.entity.MemberConfig;
import com.rxinyun.member.tools.modular.config.enums.MemberConfigCategoryEnum;
import com.rxinyun.member.tools.modular.config.mapper.MemberConfigMapper;
import com.rxinyun.member.tools.modular.config.param.MemberConfigAddParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigBatchParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigEditParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigIdParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigListParam;
import com.rxinyun.member.tools.modular.config.param.MemberConfigPageParam;
import com.rxinyun.member.tools.modular.config.service.MemberConfigService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberConfigServiceImpl extends ServiceImpl<MemberConfigMapper, MemberConfig> implements MemberConfigService {
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
         MemberConfig memberConfig = (MemberConfig)this.getOne((Wrapper<MemberConfig>)new LambdaQueryWrapper<MemberConfig>().eq(MemberConfig::getConfigKey, key));
         if (ObjectUtil.isNotEmpty(memberConfig)) {
            this.commonCacheOperator.put("dev-config:" + key, memberConfig.getConfigValue());
            return memberConfig.getConfigValue();
         } else {
            return null;
         }
      }
   }

   @Override
   public Page<MemberConfig> page(MemberConfigPageParam memberConfigPageParam) {
      QueryWrapper<MemberConfig> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda()
         .select(
            
               MemberConfig::getId,
               MemberConfig::getConfigKey,
               MemberConfig::getConfigValue,
               MemberConfig::getCategory,
               MemberConfig::getRemark,
               MemberConfig::getSortCode
            
         );
      if (ObjectUtil.isNotEmpty(memberConfigPageParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberConfig::getConfigKey, memberConfigPageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{memberConfigPageParam.getSortField(), memberConfigPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(memberConfigPageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            memberConfigPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(memberConfigPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(MemberConfig::getSortCode);
      }

      queryWrapper.lambda().eq(MemberConfig::getCategory, MemberConfigCategoryEnum.BIZ_DEFINE.getValue());
      return (Page<MemberConfig>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<MemberConfig> sysBaseList() {
      MemberConfigListParam memberConfigListParam = new MemberConfigListParam();
      memberConfigListParam.setCategory(MemberConfigCategoryEnum.SYS_BASE.getValue());
      return this.list(memberConfigListParam)
         .stream()
         .filter(memberConfig -> !memberConfig.getConfigKey().equals("RUNXIN_SYS_DEFAULT_PASSWORD"))
         .collect(Collectors.toList());
   }

   @Override
   public List<MemberConfig> list(MemberConfigListParam memberConfigListParam) {
      LambdaQueryWrapper<MemberConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
      lambdaQueryWrapper.select(
         
            MemberConfig::getId,
            MemberConfig::getConfigKey,
            MemberConfig::getConfigValue,
            MemberConfig::getCategory,
            MemberConfig::getSortCode,
            MemberConfig::getRemark,
            MemberConfig::getRemark
         
      );
      if (ObjectUtil.isNotEmpty(memberConfigListParam.getCategory())) {
         lambdaQueryWrapper.eq(MemberConfig::getCategory, memberConfigListParam.getCategory());
      }

      return this.list(lambdaQueryWrapper);
   }

   @Override
   public void add(MemberConfigAddParam memberConfigAddParam) {
      this.checkParam(memberConfigAddParam);
      MemberConfig devConfig = (MemberConfig)BeanUtil.toBean(memberConfigAddParam, MemberConfig.class);
      devConfig.setCategory(MemberConfigCategoryEnum.BIZ_DEFINE.getValue());
      this.save(devConfig);
   }

   private void checkParam(MemberConfigAddParam memberConfigAddParam) {
      boolean hasSameConfig = this.count((Wrapper<MemberConfig>)new LambdaQueryWrapper<MemberConfig>().eq(MemberConfig::getConfigKey, memberConfigAddParam.getConfigKey())) > 0L;
      if (hasSameConfig) {
         throw new CommonException("存在重复的配置，配置键为：{}", new Object[]{memberConfigAddParam.getConfigKey()});
      }
   }

   @Override
   public void edit(MemberConfigEditParam memberConfigEditParam) {
      MemberConfig devConfig = this.queryEntity(memberConfigEditParam.getId());
      this.checkParam(memberConfigEditParam);
      BeanUtil.copyProperties(memberConfigEditParam, devConfig, new String[0]);
      devConfig.setCategory(MemberConfigCategoryEnum.BIZ_DEFINE.getValue());
      this.updateById(devConfig);
      this.commonCacheOperator.remove(new String[]{"dev-config:" + devConfig.getConfigKey()});
   }

   private void checkParam(MemberConfigEditParam memberConfigEditParam) {
      boolean hasSameConfig = this.count(
            (Wrapper<MemberConfig>)((LambdaQueryWrapper<MemberConfig>)new LambdaQueryWrapper<MemberConfig>().eq(MemberConfig::getConfigKey, memberConfigEditParam.getConfigKey()))
               .ne(MemberConfig::getId, memberConfigEditParam.getId())
         )
         > 0L;
      if (hasSameConfig) {
         throw new CommonException("存在重复的配置，配置键为：{}", new Object[]{memberConfigEditParam.getConfigKey()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<MemberConfigIdParam> devConfigIdParamList) {
      List<String> devConfigIdList = CollStreamUtil.toList(devConfigIdParamList, MemberConfigIdParam::getId);
      if (ObjectUtil.isNotEmpty(devConfigIdList)) {
         List<MemberConfig> devConfigList = this.listByIds(devConfigIdList);
         if (ObjectUtil.isNotEmpty(devConfigList)) {
            List<String> devConfigResultList = CollectionUtil.newArrayList(devConfigList.stream().map(MemberConfig::getCategory).collect(Collectors.toSet()));
            if (devConfigResultList.size() != 1 || !devConfigResultList.get(0).equals(MemberConfigCategoryEnum.BIZ_DEFINE.getValue())) {
               throw new CommonException("不可删除系统内置配置", new Object[0]);
            }

            List<MemberConfig> deleteDevConfigList = this.listByIds(devConfigIdList);
            this.removeByIds(devConfigIdList);
            deleteDevConfigList.forEach(devConfig -> this.commonCacheOperator.remove(new String[]{"dev-config:" + devConfig.getConfigKey()}));
         }
      }
   }

   @Override
   public MemberConfig detail(MemberConfigIdParam devConfigIdParam) {
      MemberConfig devConfig = this.queryEntity(devConfigIdParam.getId());
      if (MemberConfigCategoryEnum.SYS_BASE.getValue().equals(devConfig.getCategory()) && !"RUNXIN_SYS_DEFAULT_PASSWORD".equals(devConfig.getConfigKey())) {
         return devConfig;
      } else {
         throw new CommonException("配置不存在，id值为：{}", new Object[]{devConfigIdParam.getId()});
      }
   }

   @Override
   public MemberConfig queryEntity(String id) {
      MemberConfig devConfig = (MemberConfig)this.getById(id);
      if (ObjectUtil.isEmpty(devConfig)) {
         throw new CommonException("配置不存在，id值为：{}", new Object[]{id});
      } else {
         return devConfig;
      }
   }

   @Override
   public void editBatch(List<MemberConfigBatchParam> devConfigBatchParamList) {
      devConfigBatchParamList.forEach(
         devConfigBatchParam -> {
            this.update(
               (Wrapper<MemberConfig>)((LambdaUpdateWrapper<MemberConfig>)new LambdaUpdateWrapper<MemberConfig>().eq(MemberConfig::getConfigKey, devConfigBatchParam.getConfigKey()))
                  .set(MemberConfig::getConfigValue, devConfigBatchParam.getConfigValue())
            );
            this.commonCacheOperator.remove(new String[]{"dev-config:" + devConfigBatchParam.getConfigKey()});
         }
      );
   }
}
