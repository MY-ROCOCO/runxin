package com.rxinyun.member.auth.modular.login.service;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.login.param.AuthAccountPasswordLoginParam;
import com.rxinyun.member.auth.modular.login.param.AuthBindEmailParam;
import com.rxinyun.member.auth.modular.login.param.AuthBindPhoneParam;
import com.rxinyun.member.auth.modular.login.param.AuthCheckAccountParam;
import com.rxinyun.member.auth.modular.login.param.AuthGetEmailValidCodeParam;
import com.rxinyun.member.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.rxinyun.member.auth.modular.login.param.AuthPhoneValidCodeLoginParam;
import com.rxinyun.member.auth.modular.login.param.AuthRegisterByEmailParam;
import com.rxinyun.member.auth.modular.login.param.AuthRegisterParam;
import com.rxinyun.member.auth.modular.login.param.AuthResetLoginPasswordParam;
import com.rxinyun.member.auth.modular.login.param.AuthResetPasswordByEmailParam;
import com.rxinyun.member.auth.modular.login.param.AuthSwitchLoginParam;
import com.rxinyun.member.auth.modular.login.param.AuthUpdateLoginPasswordParam;
import com.rxinyun.member.auth.modular.login.result.AuthPicValidCodeResult;
import com.rxinyun.member.auth.modular.user.entity.ClientUser;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;

public interface MemberAuthService {
   AuthPicValidCodeResult getPicCaptcha(String type);

   String getLoginValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type);

   String getResetLoginValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type);

   String getResetPasswordEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type);

   String getRegisterValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type);

   String getRegisterEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type);

   String getBindPhoneValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type);

   void bindPhone(AuthBindPhoneParam authBindPhoneParam, String type);

   String getBindEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type);

   void bindEmail(AuthBindEmailParam authBindEmailParam, String type);

   String doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam, String type);

   String doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam, String type);

   SaBaseClientLoginUser getClientLoginUser();

   String doLoginById(Long userId, String device, String type);

   void register(AuthRegisterParam authRegisterParam, String type);

   void registerByEmail(AuthRegisterByEmailParam authRegisterByEmailParam, String type);

   void updatePassword(AuthUpdateLoginPasswordParam authUpdateLoginPasswordParam, String value);

   void resetPassword(AuthResetLoginPasswordParam authResetLoginPasswordParam, String value);

   void resetPasswordByEmail(AuthResetPasswordByEmailParam authResetPasswordByEmailParam, String value);

   CommonResult<ClientUser> checkAccount(AuthCheckAccountParam authCheckAccountParam, String value);

   String switchLogin(AuthSwitchLoginParam param);
}
