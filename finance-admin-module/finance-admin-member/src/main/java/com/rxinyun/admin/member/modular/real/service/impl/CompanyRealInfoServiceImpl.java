package com.rxinyun.admin.member.modular.real.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.real.entity.CompanyRealInfo;
import com.rxinyun.admin.member.modular.real.mapper.CompanyRealInfoMapper;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoIdParam;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoPageParam;
import com.rxinyun.admin.member.modular.real.service.CompanyRealInfoService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CompanyRealInfoServiceImpl extends ServiceImpl<CompanyRealInfoMapper, CompanyRealInfo> implements CompanyRealInfoService {
   private static final Logger log = LoggerFactory.getLogger(CompanyRealInfoServiceImpl.class);

   @Override
   public Page<CompanyRealInfo> page(CompanyRealInfoPageParam pageParam) {
      QueryWrapper<CompanyRealInfo> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(CompanyRealInfo::getCompanyName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<CompanyRealInfo>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public CompanyRealInfo detail(CompanyRealInfoIdParam companyRealInfoIdParam) {
      return (CompanyRealInfo)this.getOne((Wrapper<CompanyRealInfo>)new LambdaQueryWrapper<CompanyRealInfo>().eq(CompanyRealInfo::getMemberNo, companyRealInfoIdParam.getMemberNo()));
   }

   @Override
   public CompanyRealInfo queryEntity(String id) {
      return (CompanyRealInfo)this.getById(id);
   }

   @Override
   public CompanyRealInfo seleteByMemberNo(Long memberNo) {
      return (CompanyRealInfo)this.getOne((Wrapper<CompanyRealInfo>)new LambdaQueryWrapper<CompanyRealInfo>().eq(CompanyRealInfo::getMemberNo, memberNo));
   }
}
