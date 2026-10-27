package com.rxinyun.admin.member.modular.third.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.member.modular.third.entity.AuthThirdUser;
import com.rxinyun.admin.member.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserIdParam;
import java.util.List;

public interface AdminAuthThirdService extends IService<AuthThirdUser> {
   Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam);

   void delete(List<ClientUserIdParam> clientUserIdParamList);
}
