package com.rxinyun.member.auth.modular.profile.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.member.auth.modular.profile.entity.MemberContactProfile;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileAddParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileEditParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfileIdParam;
import com.rxinyun.member.auth.modular.profile.param.MemberContactProfilePageParam;

public interface MemberContactProfileService extends IService<MemberContactProfile> {
   Page<MemberContactProfile> page(MemberContactProfilePageParam pageParam);

   void add(MemberContactProfileAddParam addParam);

   void edit(MemberContactProfileEditParam editParam);

   void delete(MemberContactProfileIdParam idParam);

   MemberContactProfile queryCurrentMemberEntity(Long id);
}
