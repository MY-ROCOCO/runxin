package com.rxinyun.admin.auth.modular.login.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rxinyun.admin.auth.modular.login.enums.AuthDeviceTypeEnum;
import com.rxinyun.admin.auth.modular.login.enums.AuthExceptionEnum;
import com.rxinyun.admin.auth.modular.login.param.AuthAccountPasswordLoginParam;
import com.rxinyun.admin.auth.modular.login.param.AuthGetPhoneValidCodeParam;
import com.rxinyun.admin.auth.modular.login.param.AuthPhoneValidCodeLoginParam;
import com.rxinyun.admin.auth.modular.login.result.AuthPicValidCodeResult;
import com.rxinyun.admin.auth.modular.login.service.AuthService;
import com.rxinyun.auth.api.LoginAdminApi;
import com.rxinyun.auth.api.SaBaseLoginUserApi;
import com.rxinyun.auth.core.enums.SaClientTypeEnum;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser.DataScope;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.DevSmsApi;
import com.rxinyun.dev.api.DevTelegramBindingApi;
import com.rxinyun.dev.pojo.DevTelegramBindingInfo;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService, LoginAdminApi {
   private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
   private static final String RUNXIN_SYS_DEFAULT_CAPTCHA_OPEN_KEY = "RUNXIN_SYS_DEFAULT_CAPTCHA_OPEN";
   private static final String RUNXIN_SMS_ALIYUN_TEMPLATE_CODE = "RUNXIN_SMS_ALIYUN_TEMPLATE_CODE";
   private static final String RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME = "RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME";
   private static final String AUTH_VALID_CODE_CACHE_KEY = "auth-validCode:";
   private static final String LOGIN_ERROR_TIMES_KEY_PREFIX = "login-error-times:";
   @Resource(
      name = "loginUserApi"
   )
   private SaBaseLoginUserApi loginUserApi;
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private DevSmsApi devSmsApi;
   @Resource
   private DevTelegramBindingApi devTelegramBindingApi;
   @Resource
   private CommonCacheOperator commonCacheOperator;

   @Override
   public AuthPicValidCodeResult getPicCaptcha(String type) {
      CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
      AuthPicValidCodeResult authPicValidCodeResult = new AuthPicValidCodeResult();
      String validCode = circleCaptcha.getCode();
      log.info("生成验证码：" + validCode);
      String validCodeBase64 = circleCaptcha.getImageBase64Data();
      String validCodeReqNo = IdWorker.getIdStr();
      authPicValidCodeResult.setValidCodeBase64(validCodeBase64);
      authPicValidCodeResult.setValidCodeReqNo(validCodeReqNo);
      this.commonCacheOperator.put("auth-validCode:" + validCodeReqNo, validCode, 300L);
      return authPicValidCodeResult;
   }

   @Override
   public String getPhoneValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type) {
      String phone = authGetPhoneValidCodeParam.getPhone();
      String validCode = authGetPhoneValidCodeParam.getValidCode();
      String validCodeReqNo = authGetPhoneValidCodeParam.getValidCodeReqNo();
      this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
      String phoneValidCode = RandomUtil.randomNumbers(6);
      String phoneValidCodeReqNo = IdWorker.getIdStr();
      log.info("发送短信：{}", phone);
      this.devSmsApi.sendSms(phone, JSONUtil.toJsonStr(JSONUtil.createObj().set("code", phoneValidCode)));
      log.info("发送短信结束：{}", phone);
      String SMS_KEY = "auth-validCode:" + phone + "_" + phoneValidCodeReqNo;
      log.info("SMS_KEY：{}", SMS_KEY);
      this.commonCacheOperator.put(SMS_KEY, phoneValidCode, 300L);
      return phoneValidCodeReqNo;
   }

   private void validValidCode(String phoneOrEmail, String validCode, String validCodeReqNo) {
      Object existValidCode;
      if (ObjectUtil.isEmpty(phoneOrEmail)) {
         existValidCode = this.commonCacheOperator.get("auth-validCode:" + validCodeReqNo);
      } else {
         existValidCode = this.commonCacheOperator.get("auth-validCode:" + phoneOrEmail + "_" + validCodeReqNo);
      }

      if (ObjectUtil.isEmpty(existValidCode)) {
         throw new CommonException(AuthExceptionEnum.VALID_CODE_ERROR.getValue(), new Object[0]);
      } else {
         if (ObjectUtil.isEmpty(phoneOrEmail)) {
            this.commonCacheOperator.remove(new String[]{"auth-validCode:" + validCodeReqNo});
         } else {
            this.commonCacheOperator.remove(new String[]{"auth-validCode:" + phoneOrEmail + "_" + validCodeReqNo});
         }

         if (!Convert.toStr(validCode).toLowerCase().equals(Convert.toStr(existValidCode).toLowerCase())) {
            throw new CommonException("图形验证码错误", new Object[0]);
         }
      }
   }

   private void validPhoneValidCodeParam(String phoneOrEmail, String validCode, String validCodeReqNo, String type) {
      if (ObjectUtil.isEmpty(phoneOrEmail)) {
         this.validValidCode(null, validCode, validCodeReqNo);
      } else {
         if (!PhoneUtil.isMobile(phoneOrEmail) && !CommonEmailUtil.isEmail(phoneOrEmail)) {
            throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue(), new Object[0]);
         }

         this.validValidCode(phoneOrEmail, validCode, validCodeReqNo);
         if (SaClientTypeEnum.B.getValue().equals(type) && ObjectUtil.isEmpty(this.loginUserApi.getUserByPhone(phoneOrEmail))) {
            throw new CommonException(AuthExceptionEnum.PHONE_ERROR.getValue(), new Object[0]);
         }
      }
   }

   @Override
   public String doLogin(AuthAccountPasswordLoginParam authAccountPasswordLoginParam, String type) {
      this.isDisableTime(authAccountPasswordLoginParam.getAccount());
      String account = authAccountPasswordLoginParam.getAccount();
      String password = authAccountPasswordLoginParam.getPassword();
      String device = authAccountPasswordLoginParam.getDevice();
      if (ObjectUtil.isEmpty(device)) {
         device = AuthDeviceTypeEnum.PC.getValue();
      } else {
         AuthDeviceTypeEnum.validate(device);
      }

      String defaultCaptchaOpen = this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_CAPTCHA_OPEN");

      String passwordHash;
      try {
         passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(password));
      } catch (Exception var9) {
         throw new CommonException(AuthExceptionEnum.PWD_DECRYPT_ERROR.getValue(), new Object[0]);
      }

      if (!SaClientTypeEnum.B.getValue().equals(type)) {
         throw new CommonException(AuthExceptionEnum.CLIENT_TYPE_ERROR.getValue(), new Object[0]);
      } else {
         SaBaseLoginUser saBaseLoginUser = this.loginUserApi.getUserByAccount(account);
         if (ObjectUtil.isEmpty(saBaseLoginUser)) {
            throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue(), new Object[0]);
         } else if (!saBaseLoginUser.getPassword().equals(passwordHash)) {
            this.saveLoginTimes(account);
            throw new CommonException(AuthExceptionEnum.PWD_ERROR.getValue(), new Object[0]);
         } else {
            this.clearLoginErrorTimes(account);
            return this.execLoginB(saBaseLoginUser, device);
         }
      }
   }

   @Override
   public String doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam, String type) {
      String phone = authPhoneValidCodeLoginParam.getPhone();
      this.validPhoneValidCodeParam(phone, authPhoneValidCodeLoginParam.getValidCode(), authPhoneValidCodeLoginParam.getValidCodeReqNo(), type);
      String device = authPhoneValidCodeLoginParam.getDevice();
      if (ObjectUtil.isEmpty(device)) {
         device = AuthDeviceTypeEnum.PC.getValue();
      } else {
         AuthDeviceTypeEnum.validate(device);
      }

      SaBaseLoginUser saBaseLoginUser = this.loginUserApi.getUserByPhone(phone);
      if (ObjectUtil.isEmpty(saBaseLoginUser)) {
         throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue(), new Object[0]);
      } else {
         return this.execLoginB(saBaseLoginUser, device);
      }
   }

   private void isDisableTime(String userAccount) {
      long disableTime = StpUtil.getDisableTime(userAccount);
      if (disableTime > 0L) {
         if (disableTime > 60L) {
            throw new CommonException(userAccount + "账号已被封禁, 请再" + disableTime / 60L + "分钟后重新尝试登录!!", new Object[0]);
         } else {
            throw new CommonException(userAccount + "账号已被封禁, 请再" + disableTime + "秒后重新尝试登录!!", new Object[0]);
         }
      }
   }

   private void saveLoginTimes(String userAccount) {
      String loginErrorKey = "login-error-times:" + userAccount;
      Integer number = (Integer)this.commonCacheOperator.get(loginErrorKey);
      if (number == null) {
         number = 2;
         this.commonCacheOperator.put(loginErrorKey, number, 300L);
      } else if (number < 5) {
         number = number + 1;
         this.commonCacheOperator.put(loginErrorKey, number, 300L);
      } else {
         StpUtil.disable(userAccount, 300L);
         this.clearLoginErrorTimes(userAccount);
      }
   }

   private void clearLoginErrorTimes(String userAccount) {
      String loginErrorKey = "login-error-times:" + userAccount;
      this.commonCacheOperator.remove(new String[]{loginErrorKey});
   }

   private String execLoginB(SaBaseLoginUser saBaseLoginUser, String device) {
      if (!saBaseLoginUser.getEnabled()) {
         throw new CommonException(AuthExceptionEnum.ACCOUNT_DISABLED.getValue(), new Object[0]);
      } else {
         StpUtil.login(saBaseLoginUser.getId(), new SaLoginModel().setDevice(device).setExtra("name", saBaseLoginUser.getName()));
         this.fillSaBaseLoginUserAndUpdateCache(saBaseLoginUser);
         return StpUtil.getTokenInfo().tokenValue;
      }
   }

   private void fillSaBaseLoginUserAndUpdateCache(SaBaseLoginUser saBaseLoginUser) {
      List<JSONObject> roleList = this.loginUserApi.getRoleListByUserId(saBaseLoginUser.getId());
      List<String> roleIdList = roleList.stream().map(jsonObject -> jsonObject.getStr("id")).collect(Collectors.toList());
      List<String> roleCodeList = roleList.stream().map(jsonObject -> jsonObject.getStr("code")).collect(Collectors.toList());
      List<String> userAndRoleIdList = CollectionUtil.unionAll(
         roleIdList, CollectionUtil.newArrayList(new String[]{saBaseLoginUser.getId()}), new Collection[0]
      );
      saBaseLoginUser.setButtonCodeList(this.loginUserApi.getButtonCodeListListByUserAndRoleIdList(userAndRoleIdList));
      saBaseLoginUser.setMobileButtonCodeList(this.loginUserApi.getMobileButtonCodeListListByUserIdAndRoleIdList(userAndRoleIdList));
      saBaseLoginUser.setDataScopeList(Convert.toList(DataScope.class, this.loginUserApi.getPermissionListByUserIdAndRoleIdList(userAndRoleIdList, null)));
      List<String> permissionCodeList = saBaseLoginUser.getDataScopeList().stream().<String>map(DataScope::getApiUrl).collect(Collectors.toList());
      saBaseLoginUser.setPermissionCodeList(permissionCodeList);
      this.commonCacheOperator.put("auth-b-permission-list:" + saBaseLoginUser.getId(), permissionCodeList);
      saBaseLoginUser.setRoleCodeList(roleCodeList);
      this.fillTelegramBindingInfo(saBaseLoginUser);
      StpUtil.getTokenSession().set("loginUser", saBaseLoginUser);
   }

   private void fillTelegramBindingInfo(SaBaseLoginUser saBaseLoginUser) {
      DevTelegramBindingInfo bindingInfo = this.devTelegramBindingApi.getBindingInfo("ADMIN", saBaseLoginUser.getId());
      saBaseLoginUser.setTelegramBound(Boolean.TRUE.equals(bindingInfo.getBound()));
      saBaseLoginUser.setTelegramChatId(bindingInfo.getChatId());
      saBaseLoginUser.setTelegramUserId(bindingInfo.getTelegramUserId());
      saBaseLoginUser.setTelegramUsername(bindingInfo.getTelegramUsername());
   }

   @Override
   public SaBaseLoginUser getLoginUser() {
      SaBaseLoginUser saBaseLoginUser = StpLoginUserUtil.getLoginUser();
      saBaseLoginUser = this.loginUserApi.getUserById(saBaseLoginUser.getId());
      this.fillSaBaseLoginUserAndUpdateCache(saBaseLoginUser);
      saBaseLoginUser.setPassword(null);
      saBaseLoginUser.setPermissionCodeList(null);
      saBaseLoginUser.setDataScopeList(null);
      if (ObjectUtil.isNotEmpty(saBaseLoginUser.getPhone())) {
         String decryptedPhone = CommonCryptogramUtil.doSm4CbcDecrypt(saBaseLoginUser.getPhone());
         saBaseLoginUser.setPhone(decryptedPhone);
      }

      return saBaseLoginUser;
   }

   @Override
   public String doLoginById(String userId, String device, String type) {
      if (!SaClientTypeEnum.B.getValue().equals(type)) {
         throw new CommonException(AuthExceptionEnum.CLIENT_TYPE_ERROR.getValue(), new Object[0]);
      } else {
         SaBaseLoginUser saBaseLoginUser = this.loginUserApi.getUserById(userId);
         if (ObjectUtil.isEmpty(saBaseLoginUser)) {
            throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue(), new Object[0]);
         } else {
            return this.execLoginB(saBaseLoginUser, device);
         }
      }
   }
}
