package com.rxinyun.admin.member.modular.real.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.real.entity.PersonalRealInfo;
import com.rxinyun.admin.member.modular.real.mapper.PersonalRealInfoMapper;
import com.rxinyun.admin.member.modular.real.param.PersonalRealInfoIdParam;
import com.rxinyun.admin.member.modular.real.param.PersonalRealInfoPageParam;
import com.rxinyun.admin.member.modular.real.service.PersonalRealInfoService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PersonalRealInfoServiceImpl extends ServiceImpl<PersonalRealInfoMapper, PersonalRealInfo> implements PersonalRealInfoService {
   private static final Logger log = LoggerFactory.getLogger(PersonalRealInfoServiceImpl.class);

   @Override
   public Page<PersonalRealInfo> page(PersonalRealInfoPageParam pageParam) {
      QueryWrapper<PersonalRealInfo> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(PersonalRealInfo::getName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<PersonalRealInfo>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public PersonalRealInfo detail(PersonalRealInfoIdParam PersonalRealInfoIdParam) {
      return (PersonalRealInfo)this.getOne((Wrapper<PersonalRealInfo>)new LambdaQueryWrapper<PersonalRealInfo>().eq(PersonalRealInfo::getMemberNo, PersonalRealInfoIdParam.getMemberNo()));
   }

   @Override
   public PersonalRealInfo queryEntity(String id) {
      return (PersonalRealInfo)this.getById(id);
   }

   @Override
   public PersonalRealInfo seleteByMemberNo(Long memberNo) {
      return (PersonalRealInfo)this.getOne((Wrapper<PersonalRealInfo>)new LambdaQueryWrapper<PersonalRealInfo>().eq(PersonalRealInfo::getMemberNo, memberNo));
   }
}
