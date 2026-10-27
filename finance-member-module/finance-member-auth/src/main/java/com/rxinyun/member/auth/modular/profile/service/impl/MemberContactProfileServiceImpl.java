package com.rxinyun.member.auth.modular.profile.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.auth.modular.profile.entity.MemberContactProfile;
import com.rxinyun.member.auth.modular.profile.mapper.MemberContactProfileMapper;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileAddParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileEditParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileIdParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfilePageParam;
import com.rxinyun.member.auth.modular.profile.service.MemberContactProfileService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import org.springframework.stereotype.Service;

@Service
public class MemberContactProfileServiceImpl extends ServiceImpl<MemberContactProfileMapper, MemberContactProfile> implements MemberContactProfileService {
   @Override
   public Page<MemberContactProfile> page(MemberContactProfilePageParam pageParam) {
      QueryWrapper<MemberContactProfile> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberContactProfile::getMemberId, this.getCurrentMemberId());
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberContactProfile>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public void add(MemberContactProfileAddParam addParam) {
      MemberContactProfile profile = (MemberContactProfile)BeanUtil.toBean(addParam, MemberContactProfile.class);
      profile.setMemberId(this.getCurrentMemberId());
      this.save(profile);
   }

   @Override
   public void edit(MemberContactProfileEditParam editParam) {
      MemberContactProfile profile = this.queryCurrentMemberEntity(editParam.getId());
      BeanUtil.copyProperties(editParam, profile, new String[0]);
      this.updateById(profile);
   }

   @Override
   public void delete(MemberContactProfileIdParam idParam) {
      this.queryCurrentMemberEntity(idParam.getId());
      this.removeById(idParam.getId());
   }

   @Override
   public MemberContactProfile queryCurrentMemberEntity(Long id) {
      MemberContactProfile profile = (MemberContactProfile)this.getById(id);
      if (!ObjectUtil.isEmpty(profile) && this.getCurrentMemberId().equals(profile.getMemberId())) {
         return profile;
      } else {
         throw new CommonException("会员基本资料不存在，id值为：{}", new Object[]{id});
      }
   }

   private Long getCurrentMemberId() {
      return StpClientLoginUserUtil.getClientLoginUser().getId();
   }
}
