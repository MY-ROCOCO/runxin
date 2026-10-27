package com.rxinyun.admin.auth.modular.login.service;

import com.rxinyun.admin.auth.modular.login.param.AuthAccountPasswordLoginParam;
import com.rxinyun.admin.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.rxinyun.admin.auth.modular.login.param.AuthPhoneValidCodeLoginParam;
import com.rxinyun.admin.auth.modular.login.result.AuthPicValidCodeResult;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;

public interface AuthService {
   AuthPicValidCodeResult getPicCaptcha(String type);

   String getPhoneValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type);

   String doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam, String type);

   String doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam, String type);

   SaBaseLoginUser getLoginUser();

   String doLoginById(String userId, String device, String type);
}
