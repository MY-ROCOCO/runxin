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
import com.rxinyun.admin.dev.modular.api.entity.ApiKey;
import com.rxinyun.admin.dev.modular.api.mapper.ApiKeyMapper;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyAddParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyEditParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyIdParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyPageParam;
import com.rxinyun.admin.dev.modular.api.param.ApiKeyStatusParam;
import com.rxinyun.admin.dev.modular.api.result.ApiKeyCreateResult;
import com.rxinyun.admin.dev.modular.api.service.ApiKeyService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyMapper, ApiKey> implements ApiKeyService {
   @Resource
   private MemberSaBaseLoginUserApi memberSaBaseLoginUserApi;

   @Override
   public Page<ApiKey> page(ApiKeyPageParam apiKeyPageParam) {
      QueryWrapper<ApiKey> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(apiKeyPageParam.getMemberNo())) {
         queryWrapper.lambda().eq(ApiKey::getMemberNo, apiKeyPageParam.getMemberNo());
      }

      if (ObjectUtil.isNotEmpty(apiKeyPageParam.getSearchKey())) {
         queryWrapper.lambda().like(ApiKey::getName, apiKeyPageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(apiKeyPageParam.getStatus())) {
         queryWrapper.lambda().eq(ApiKey::getStatus, apiKeyPageParam.getStatus());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{apiKeyPageParam.getSortField(), apiKeyPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(apiKeyPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, apiKeyPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(apiKeyPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<ApiKey>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public ApiKeyCreateResult add(ApiKeyAddParam apiKeyAddParam) {
      Long memberNo = apiKeyAddParam.getMemberNo();
      if (ObjectUtil.isEmpty(this.memberSaBaseLoginUserApi.getClientUserById(memberNo))) {
         throw new CommonException("会员不存在，会员编号为：{}", new Object[]{memberNo});
      } else {
         boolean hasApiKey = this.count((Wrapper<ApiKey>)new LambdaQueryWrapper<ApiKey>().eq(ApiKey::getMemberNo, memberNo)) > 0L;
         if (hasApiKey) {
            throw new CommonException("该会员已拥有API密钥，一个会员只能拥有一个API密钥，会员编号为：{}", new Object[]{memberNo});
         } else {
            String apiKey = UUID.randomUUID().toString().replace("-", "");
            String apiSecretOriginal = UUID.randomUUID().toString().replace("-", "");
            String apiSecretEncrypted = CommonCryptogramUtil.doSm4CbcEncrypt(apiSecretOriginal);
            ApiKey apiKeyEntity = (ApiKey)BeanUtil.toBean(apiKeyAddParam, ApiKey.class);
            apiKeyEntity.setId(SnowflakeIdGenerator.generateId());
            apiKeyEntity.setApiKey(apiKey);
            apiKeyEntity.setApiSecret(apiSecretEncrypted);
            apiKeyEntity.setStatus(1);
            String loginUserId = StpLoginUserUtil.getLoginUser().getId();
            apiKeyEntity.setCreateUser(loginUserId);
            this.save(apiKeyEntity);
            ApiKeyCreateResult result = new ApiKeyCreateResult();
            result.setApiKey(apiKey);
            result.setApiSecret(apiSecretOriginal);
            return result;
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(ApiKeyEditParam apiKeyEditParam) {
      ApiKey apiKey = this.queryEntity(apiKeyEditParam.getId());
      BeanUtil.copyProperties(apiKeyEditParam, apiKey, new String[0]);
      String loginUserId = StpLoginUserUtil.getLoginUser().getId();
      apiKey.setUpdateUser(loginUserId);
      this.updateById(apiKey);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ApiKeyIdParam> apiKeyIdParamList) {
      List<String> apiKeyIdList = CollStreamUtil.toList(apiKeyIdParamList, ApiKeyIdParam::getId);
      if (ObjectUtil.isNotEmpty(apiKeyIdList)) {
         this.removeByIds(apiKeyIdList);
      }
   }

   @Override
   public ApiKey detail(ApiKeyIdParam apiKeyIdParam) {
      return this.queryEntity(apiKeyIdParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void changeStatus(ApiKeyStatusParam apiKeyStatusParam) {
      ApiKey apiKey = this.queryEntity(apiKeyStatusParam.getId());
      apiKey.setStatus(apiKeyStatusParam.getStatus());
      String loginUserId = StpLoginUserUtil.getLoginUser().getId();
      apiKey.setUpdateUser(loginUserId);
      this.updateById(apiKey);
   }

   @Override
   public ApiKey getByApiKey(String apiKey) {
      return (ApiKey)this.getOne((Wrapper<ApiKey>)new LambdaQueryWrapper<ApiKey>().eq(ApiKey::getApiKey, apiKey));
   }

   public ApiKey queryEntity(String id) {
      ApiKey apiKey = (ApiKey)this.getById(id);
      if (ObjectUtil.isEmpty(apiKey)) {
         throw new CommonException("API密钥不存在，id值为：{}", new Object[]{id});
      } else {
         return apiKey;
      }
   }
}
