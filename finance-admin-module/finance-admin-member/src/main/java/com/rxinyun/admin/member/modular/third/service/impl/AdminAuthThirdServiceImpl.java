package com.rxinyun.admin.member.modular.third.service.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.third.entity.AuthThirdUser;
import com.rxinyun.admin.member.modular.third.mapper.AdminAuthThirdMapper;
import com.rxinyun.admin.member.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.admin.member.modular.third.service.AdminAuthThirdService;
import com.rxinyun.admin.member.modular.user.param.ClientUserIdParam;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthThirdServiceImpl extends ServiceImpl<AdminAuthThirdMapper, AuthThirdUser> implements AdminAuthThirdService {
   private static final Logger log = LoggerFactory.getLogger(AdminAuthThirdServiceImpl.class);
   @Resource
   private AdminAuthThirdMapper authThirdUserMapper;

   @Override
   public Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam) {
      QueryWrapper<AuthThirdUser> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(authThirdUserPageParam.getCategory())) {
         queryWrapper.lambda().eq(AuthThirdUser::getCategory, authThirdUserPageParam.getCategory());
      }

      if (ObjectUtil.isNotEmpty(authThirdUserPageParam.getSearchKey())) {
         queryWrapper.and(
            q -> ((LambdaQueryWrapper<AuthThirdUser>)((LambdaQueryWrapper<AuthThirdUser>)q.lambda().like(AuthThirdUser::getName, authThirdUserPageParam.getSearchKey())).or())
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

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ClientUserIdParam> clientUserIdParamList) {
      this.authThirdUserMapper.removeByUserIds(CollStreamUtil.toList(clientUserIdParamList, ClientUserIdParam::getId));
   }
}
