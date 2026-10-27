package com.rxinyun.admin.auth.modular.third.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.auth.modular.third.entity.AuthThirdUser;
import com.rxinyun.admin.auth.modular.third.mapper.AuthThirdMapper;
import com.rxinyun.admin.auth.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.admin.auth.modular.third.service.AuthThirdService;
import com.rxinyun.auth.api.SaBaseLoginUserApi;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class AuthThirdServiceImpl extends ServiceImpl<AuthThirdMapper, AuthThirdUser> implements AuthThirdService {
   @Resource(
      name = "loginUserApi"
   )
   private SaBaseLoginUserApi loginUserApi;

   @Override
   public Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam) {
      QueryWrapper<AuthThirdUser> queryWrapper = new QueryWrapper<AuthThirdUser>().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(authThirdUserPageParam.getCategory())) {
         queryWrapper.lambda().eq(AuthThirdUser::getCategory, authThirdUserPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(authThirdUserPageParam.getSearchKey())) {
         queryWrapper.and(
            q -> q.lambda().like(AuthThirdUser::getName, authThirdUserPageParam.getSearchKey()).or()
               .like(AuthThirdUser::getNickname, authThirdUserPageParam.getSearchKey())
         );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{authThirdUserPageParam.getSortField(), authThirdUserPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(authThirdUserPageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            authThirdUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(authThirdUserPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<AuthThirdUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }
}
