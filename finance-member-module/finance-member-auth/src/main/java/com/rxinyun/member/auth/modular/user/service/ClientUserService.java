package com.rxinyun.member.auth.modular.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.user.entity.ClientUser;
import com.rxinyun.member.auth.modular.user.param.ClientUserAddParam;
import com.rxinyun.member.auth.modular.user.param.ClientUserEditParam;
import com.rxinyun.member.auth.modular.user.param.ClientUserIdParam;
import com.rxinyun.member.auth.modular.user.param.ClientUserPageParam;
import com.rxinyun.member.auth.modular.user.result.ClientLoginUser;
import java.util.List;

public interface ClientUserService extends IService<ClientUser> {
   ClientLoginUser getUserById(Long id);

   ClientLoginUser getUserByAccount(String account);

   ClientLoginUser getUserByPhone(String phone);

   ClientLoginUser getUserByEmail(String email);

   Page<ClientUser> page(ClientUserPageParam clientUserPageParam);

   void add(ClientUserAddParam clientUserAddParam);

   void edit(ClientUserEditParam clientUserEditParam);

   void delete(List<ClientUserIdParam> clientUserIdParamList);

   ClientUser detail(ClientUserIdParam clientUserIdParam);

   void updateUserLoginInfo(Long userId, String device);

   ClientUser queryEntity(Long id);

   CommonResult updateByRealStatus(Long id);

   void updateByRealUser(Long id, String name, String key);

   void updateNotificationEnabled(Integer notificationEnabled);
}
