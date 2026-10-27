package com.rxinyun.admin.auth.modular.third.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.admin.auth.modular.third.entity.AuthThirdUser;
import com.rxinyun.admin.auth.modular.third.param.AuthThirdUserPageParam;

public interface AuthThirdService extends IService<AuthThirdUser> {
   Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam);
}
