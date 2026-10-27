package com.rxinyun.admin.member.modular.profile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.profile.entity.AdminMemberContactProfile;
import com.rxinyun.admin.member.modular.profile.mapper.AdminMemberContactProfileMapper;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfileEditParam;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfileIdParam;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfilePageParam;
import com.rxinyun.admin.member.modular.profile.service.AdminMemberContactProfileService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminMemberContactProfileServiceImpl
   extends ServiceImpl<AdminMemberContactProfileMapper, AdminMemberContactProfile>
   implements AdminMemberContactProfileService {
   @Override
   public Page<AdminMemberContactProfile> page(AdminMemberContactProfilePageParam pageParam) {
      QueryWrapper<AdminMemberContactProfile> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getMemberId())) {
         queryWrapper.lambda().eq(AdminMemberContactProfile::getMemberId, pageParam.getMemberId());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<AdminMemberContactProfile>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public void edit(AdminMemberContactProfileEditParam editParam) {
      AdminMemberContactProfile profile = this.queryEntity(editParam.getId());
      BeanUtil.copyProperties(editParam, profile, new String[0]);
      this.updateById(profile);
   }

   @Override
   public void delete(AdminMemberContactProfileIdParam idParam) {
      this.queryEntity(idParam.getId());
      this.removeById(idParam.getId());
   }

   @Override
   public AdminMemberContactProfile queryEntity(Long id) {
      AdminMemberContactProfile profile = (AdminMemberContactProfile)this.getById(id);
      if (ObjectUtil.isEmpty(profile)) {
         throw new CommonException("会员基本资料不存在，id值为：{}", new Object[]{id});
      } else {
         return profile;
      }
   }
}
