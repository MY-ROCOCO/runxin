package com.rxinyun.member.auth.modular.third.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.third.entity.AuthThirdUser;
import com.rxinyun.member.auth.modular.third.param.AuthThirdCallbackParam;
import com.rxinyun.member.auth.modular.third.param.AuthThirdRenderParam;
import com.rxinyun.member.auth.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.member.auth.modular.third.result.AuthThirdRenderResult;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;

public interface MemberAuthThirdService extends IService<AuthThirdUser> {
   AuthThirdRenderResult render(AuthThirdRenderParam authThirdRenderParam);

   CommonResult callback(AuthThirdCallbackParam authThirdCallbackParam, AuthCallback authCallback);

   Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam);

   CommonResult bind(AuthUser authUser);
}
