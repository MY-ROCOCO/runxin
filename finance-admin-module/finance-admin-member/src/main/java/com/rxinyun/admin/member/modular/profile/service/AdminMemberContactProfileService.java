package com.rxinyun.admin.member.modular.profile.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.member.modular.profile.entity.AdminMemberContactProfile;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfileEditParam;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfileIdParam;
import com.rxinyun.admin.member.modular.profile.param.AdminMemberContactProfilePageParam;

public interface AdminMemberContactProfileService extends IService<AdminMemberContactProfile> {
   Page<AdminMemberContactProfile> page(AdminMemberContactProfilePageParam pageParam);

   void edit(AdminMemberContactProfileEditParam editParam);

   void delete(AdminMemberContactProfileIdParam idParam);

   AdminMemberContactProfile queryEntity(Long id);
}
