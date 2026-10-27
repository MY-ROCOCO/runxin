package com.rxinyun.member.auth.modular.login.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.rxinyun.agent.modular.service.AgentMemberBindService;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.order.OrderStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.dev.api.DevSmsApi;
import com.rxinyun.dev.api.DevTelegramBindingApi;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.dev.pojo.DevTelegramBindingInfo;
import com.rxinyun.member.auth.core.util.TencentCaptchaService;
import com.rxinyun.member.auth.modular.login.enums.AuthDeviceTypeEnum;
import com.rxinyun.member.auth.modular.login.enums.AuthExceptionEnum;
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
import com.rxinyun.member.auth.modular.login.service.MemberAuthService;
import com.rxinyun.member.auth.modular.third.entity.AuthThirdUser;
import com.rxinyun.member.auth.modular.third.service.MemberAuthThirdService;
import com.rxinyun.member.auth.modular.user.entity.ClientUser;
import com.rxinyun.member.auth.modular.user.enums.ClientUserStatusEnum;
import com.rxinyun.member.auth.modular.user.result.ClientLoginUser;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import com.rxinyun.member.core.enums.SaClientTypeEnum;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.api.RechargeBalanceApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.order.api.OrderApi;
import com.rxinyun.workorder.api.AdminWorkOrederApi;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.zhyd.oauth.model.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberAuthServiceImpl implements MemberAuthService {
   private static final String RUNXIN_SYS_DEFAULT_CAPTCHA_OPEN_KEY = "RUNXIN_SYS_DEFAULT_CAPTCHA_OPEN";
   private static final String RUNXIN_SYS_DEFAULT_CAPTCHA_TYPE = "RUNXIN_SYS_DEFAULT_CAPTCHA_TYPE";
   private static final String RUNXIN_SMS_ALIYUN_TEMPLATE_CODE = "RUNXIN_SMS_ALIYUN_TEMPLATE_CODE";
   private static final String RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME = "RUNXIN_SMS_ALIYUN_DEFAULT_SIGN_NAME";
   private static final String AUTH_VALID_CODE_CACHE_KEY = "auth-validCode:";
   private static final String LOGIN_ERROR_TIMES_KEY_PREFIX = "login-error-times:";
   private static final Logger log = LoggerFactory.getLogger(MemberAuthServiceImpl.class);
   @Resource
   private MemberSaBaseLoginUserApi clientLoginUserApi;
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private DevSmsApi devSmsApi;
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private ClientUserService clientUserService;
   @Resource
   private MemberBalanceService memberBalanceService;
   @Resource
   private MemberAuthThirdService memberAuthThirdService;
   @Resource
   private TencentCaptchaService tencentCaptchaService;
   @Resource
   private ToolsNotificationApi toolsNotificationApi;
   @Resource
   private DevTelegramBindingApi devTelegramBindingApi;
   @Resource
   private OrderApi orderApi;
   @Resource
   private RechargeBalanceApi rechargeBalanceApi;
   @Resource
   private AdminWorkOrederApi adminWorkOrederApi;
   @Resource
   private AgentMemberBindService agentMemberBindService;

   @Override
   public AuthPicValidCodeResult getPicCaptcha(String type) {
      CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(100, 38, 4, 10);
      AuthPicValidCodeResult authPicValidCodeResult = new AuthPicValidCodeResult();
      String validCode = circleCaptcha.getCode();
      log.info("验证码的值为：{}", validCode);
      String validCodeBase64 = circleCaptcha.getImageBase64Data();
      String validCodeReqNo = IdWorker.getIdStr();
      authPicValidCodeResult.setValidCodeBase64(validCodeBase64);
      authPicValidCodeResult.setValidCodeReqNo(validCodeReqNo);
      this.commonCacheOperator.put("auth-validCode:" + validCodeReqNo, validCode, 300L);
      return authPicValidCodeResult;
   }

   @Override
   public String getLoginValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type) {
      String phone = authGetPhoneValidCodeParam.getPhone();
      ClientLoginUser clientUser = this.clientUserService.getUserByPhone(phone);
      if (ObjectUtil.isNull(clientUser)) {
         throw new CommonException(AuthExceptionEnum.PHONE_ERROR.getValue(), new Object[0]);
      } else {
         String validCode = authGetPhoneValidCodeParam.getValidCode();
         String validCodeReqNo = authGetPhoneValidCodeParam.getValidCodeReqNo();
         this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
         String phoneValidCode = RandomUtil.randomNumbers(6);
         String phoneValidCodeReqNo = IdWorker.getIdStr();
         log.info("发送短信开始：phone:{},code:{}", phone, phoneValidCode);
         Map<String, Object> map = new HashMap<>();
         map.put("code", phoneValidCode);
         this.toolsNotificationApi.sendNotificationSms(NotificationTypeEnum.MEMBER_LOGIN_VERIFY.getCode(), phone, map);
         log.info("发送短信结束：phone:{},code:{}", phone, phoneValidCode);
         this.commonCacheOperator.put("auth-validCode:" + phone + "_" + phoneValidCodeReqNo, phoneValidCode, 300L);
         return phoneValidCodeReqNo;
      }
   }

   @Override
   public String getResetLoginValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type) {
      String phone = authGetPhoneValidCodeParam.getPhone();
      ClientLoginUser clientUser = this.clientUserService.getUserByPhone(phone);
      if (ObjectUtil.isNull(clientUser)) {
         throw new CommonException(AuthExceptionEnum.PHONE_ERROR.getValue(), new Object[0]);
      } else {
         String validCode = authGetPhoneValidCodeParam.getValidCode();
         String validCodeReqNo = authGetPhoneValidCodeParam.getValidCodeReqNo();
         this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
         String phoneValidCode = RandomUtil.randomNumbers(6);
         String phoneValidCodeReqNo = IdWorker.getIdStr();
         log.info("发送短信开始：phone:{},code:{}", phone, phoneValidCode);
         Map<String, Object> map = new HashMap<>();
         map.put("code", phoneValidCode);
         this.toolsNotificationApi.sendNotificationSms(NotificationTypeEnum.MEMBER_RESET_PASSWORD_VERIFY.getCode(), phone, map);
         log.info("发送短信结束：phone:{},code:{}", phone, phoneValidCode);
         this.commonCacheOperator.put("auth-validCode:" + phone + "_" + phoneValidCodeReqNo, phoneValidCode, 300L);
         return phoneValidCodeReqNo;
      }
   }

   @Override
   public String getResetPasswordEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type) {
      String email = authGetEmailValidCodeParam.getEmail();
      String validCode = authGetEmailValidCodeParam.getValidCode();
      String validCodeReqNo = authGetEmailValidCodeParam.getValidCodeReqNo();
      this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
      if (!CommonEmailUtil.isEmail(email)) {
         throw new CommonException("邮箱：{}格式错误", new Object[]{email});
      } else {
         ClientLoginUser clientUser = this.clientUserService.getUserByEmail(email);
         if (ObjectUtil.isNull(clientUser)) {
            throw new CommonException("邮箱：{}不存在", new Object[]{email});
         } else {
            String emailValidCode = RandomUtil.randomNumbers(6);
            String emailValidCodeReqNo = IdWorker.getIdStr();
            log.info("发送邮件开始：email:{},code:{}", email, emailValidCode);
            Map<String, Object> map = new HashMap<>();
            map.put("code", emailValidCode);
            this.toolsNotificationApi.sendNotificationEmail(NotificationTypeEnum.MEMBER_RESET_PASSWORD_VERIFY.getCode(), email, map);
            log.info("发送邮件结束：email:{},code:{}", email, emailValidCode);
            this.commonCacheOperator.put("auth-validCode:" + email + "_" + emailValidCodeReqNo, emailValidCode, 300L);
            return emailValidCodeReqNo;
         }
      }
   }

   @Override
   public String getRegisterValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type) {
      String phone = authGetPhoneValidCodeParam.getPhone();
      String validCode = authGetPhoneValidCodeParam.getValidCode();
      String validCodeReqNo = authGetPhoneValidCodeParam.getValidCodeReqNo();
      this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
      String phoneValidCode = RandomUtil.randomNumbers(6);
      String phoneValidCodeReqNo = IdWorker.getIdStr();
      log.info("发送短信开始：phone:{},code:{}", phone, phoneValidCode);
      Map<String, Object> map = new HashMap<>();
      map.put("code", phoneValidCode);
      this.toolsNotificationApi.sendNotificationSms(NotificationTypeEnum.MEMBER_REGISTER_VERIFY.getCode(), phone, map);
      log.info("发送短信结束：phone:{},code:{}", phone, phoneValidCode);
      this.commonCacheOperator.put("auth-validCode:" + phone + "_" + phoneValidCodeReqNo, phoneValidCode, 300L);
      return phoneValidCodeReqNo;
   }

   @Override
   public String getRegisterEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type) {
      String email = authGetEmailValidCodeParam.getEmail();
      String validCode = authGetEmailValidCodeParam.getValidCode();
      String validCodeReqNo = authGetEmailValidCodeParam.getValidCodeReqNo();
      this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
      if (!CommonEmailUtil.isEmail(email)) {
         throw new CommonException("邮箱：{}格式错误", new Object[]{email});
      } else {
         ClientLoginUser clientUser = this.clientUserService.getUserByEmail(email);
         if (ObjectUtil.isNotEmpty(clientUser)) {
            throw new CommonException("邮箱：{}已被注册", new Object[]{email});
         } else {
            String emailValidCode = RandomUtil.randomNumbers(6);
            String emailValidCodeReqNo = IdWorker.getIdStr();
            log.info("发送邮件开始：email:{},code:{}", email, emailValidCode);
            Map<String, Object> map = new HashMap<>();
            map.put("code", emailValidCode);
            this.toolsNotificationApi.sendNotificationEmail(NotificationTypeEnum.MEMBER_REGISTER_VERIFY.getCode(), email, map);
            log.info("发送邮件结束：email:{},code:{}", email, emailValidCode);
            this.commonCacheOperator.put("auth-validCode:" + email + "_" + emailValidCodeReqNo, emailValidCode, 300L);
            return emailValidCodeReqNo;
         }
      }
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
            throw new CommonException("验证码错误", new Object[0]);
         }
      }
   }

   private void validTencentCaptcha(String ticket, String randStr) {
      String userIp = "用户真实IP";
      boolean result = this.tencentCaptchaService.verifyTicket(ticket, randStr);
      if (!result) {
         throw new CommonException("腾讯验证码验证失败", new Object[0]);
      }
   }

   private void validPhoneValidCodeParam(String phoneOrEmail, String validCode, String validCodeReqNo, String type) {
      String defaultCaptchaType = this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_CAPTCHA_TYPE");
      log.info("默认验证码类型：{}", defaultCaptchaType);
      if (ObjectUtil.isEmpty(phoneOrEmail)) {
         if (defaultCaptchaType.equals("TENCENT")) {
            this.validTencentCaptcha(validCode, validCodeReqNo);
         } else {
            this.validValidCode(null, validCode, validCodeReqNo);
         }
      } else {
         if (!PhoneUtil.isMobile(phoneOrEmail) && !CommonEmailUtil.isEmail(phoneOrEmail)) {
            throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue(), new Object[0]);
         }

         if (defaultCaptchaType.equals("TENCENT")) {
            this.validTencentCaptcha(validCode, validCodeReqNo);
         } else {
            this.validValidCode(phoneOrEmail, validCode, validCodeReqNo);
         }

         if (ObjectUtil.isEmpty(this.clientLoginUserApi.getClientUserByPhone(phoneOrEmail))) {
            throw new CommonException(AuthExceptionEnum.PHONE_ERROR.getValue(), new Object[0]);
         }
      }
   }

   private void validLoginPhoneValidCodeParam(String phoneOrEmail, String validCode, String validCodeReqNo, String type) {
      if (ObjectUtil.isEmpty(phoneOrEmail)) {
         throw new CommonException(AuthExceptionEnum.PHONE_ERROR.getValue(), new Object[0]);
      } else if (!PhoneUtil.isMobile(phoneOrEmail) && !CommonEmailUtil.isEmail(phoneOrEmail)) {
         throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue(), new Object[0]);
      } else {
         this.validValidCode(phoneOrEmail, validCode, validCodeReqNo);
         if (ObjectUtil.isEmpty(this.clientLoginUserApi.getClientUserByPhone(phoneOrEmail))) {
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

      String validCode = authAccountPasswordLoginParam.getValidCode();
      String validCodeReqNo = authAccountPasswordLoginParam.getValidCodeReqNo();
      String defaultCaptchaOpen = this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_CAPTCHA_OPEN");
      boolean captchaOpen = ObjectUtil.isNotEmpty(defaultCaptchaOpen) && Convert.toBool(defaultCaptchaOpen);
      boolean sentCaptcha = ObjectUtil.isNotEmpty(validCode) && ObjectUtil.isNotEmpty(validCodeReqNo);
      if (captchaOpen) {
         if (ObjectUtil.isEmpty(validCode)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue(), new Object[0]);
         }

         if (ObjectUtil.isEmpty(validCodeReqNo)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue(), new Object[0]);
         }
      }

      if (captchaOpen || sentCaptcha) {
         if (ObjectUtil.isEmpty(validCode) || ObjectUtil.isEmpty(validCodeReqNo)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue(), new Object[0]);
         }

         String defaultCaptchaType = this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_CAPTCHA_TYPE");
         if ("TENCENT".equals(defaultCaptchaType)) {
            this.validTencentCaptcha(validCode, validCodeReqNo);
         } else {
            this.validValidCode(null, validCode, validCodeReqNo);
         }
      }

      String passwordHash;
      try {
         passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(password));
      } catch (Exception var13) {
         throw new CommonException(AuthExceptionEnum.PWD_DECRYPT_ERROR.getValue(), new Object[0]);
      }

      SaBaseClientLoginUser saBaseClientLoginUser;
      if (CommonEmailUtil.isEmail(account)) {
         saBaseClientLoginUser = this.clientUserService.getUserByEmail(account);
      } else {
         saBaseClientLoginUser = this.clientLoginUserApi.getClientUserByAccount(account);
      }

      if (ObjectUtil.isEmpty(saBaseClientLoginUser)) {
         throw new CommonException(AuthExceptionEnum.ACCOUNT_ERROR.getValue(), new Object[0]);
      } else if (!saBaseClientLoginUser.getPassword().equals(passwordHash)) {
         throw new CommonException(AuthExceptionEnum.PWD_ERROR.getValue(), new Object[0]);
      } else {
         return this.execLoginC(saBaseClientLoginUser, device);
      }
   }

   @Override
   public String doLoginByPhone(AuthPhoneValidCodeLoginParam authPhoneValidCodeLoginParam, String type) {
      String phone = authPhoneValidCodeLoginParam.getPhone();
      this.validLoginPhoneValidCodeParam(phone, authPhoneValidCodeLoginParam.getValidCode(), authPhoneValidCodeLoginParam.getValidCodeReqNo(), type);
      String device = authPhoneValidCodeLoginParam.getDevice();
      if (ObjectUtil.isEmpty(device)) {
         device = AuthDeviceTypeEnum.PC.getValue();
      } else {
         AuthDeviceTypeEnum.validate(device);
      }

      SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserByPhone(phone);
      if (ObjectUtil.isEmpty(saBaseClientLoginUser)) {
         throw new CommonException("账号不存在", new Object[0]);
      } else {
         if (ObjectUtil.isNotEmpty(authPhoneValidCodeLoginParam.getAuthUser())) {
            this.bindUser(authPhoneValidCodeLoginParam.getAuthUser(), saBaseClientLoginUser.getId());
         }

         return this.execLoginC(saBaseClientLoginUser, device);
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

   private String execLoginC(SaBaseClientLoginUser saBaseClientLoginUser, String device) {
      return this.execLoginC(saBaseClientLoginUser, device, null);
   }

   private String execLoginC(SaBaseClientLoginUser saBaseClientLoginUser, String device, String operatorAdminId) {
      if (!saBaseClientLoginUser.getEnabled()) {
         throw new CommonException(AuthExceptionEnum.ACCOUNT_DISABLED.getValue(), new Object[0]);
      } else {
         SaLoginModel loginModel = new SaLoginModel().setDevice(device).setExtra("account", saBaseClientLoginUser.getAccount());
         if (StrUtil.isNotBlank(operatorAdminId)) {
            loginModel.setExtra("impersonated", true).setExtra("operatorAdminId", operatorAdminId);
         }

         StpClientUtil.login(saBaseClientLoginUser.getId(), loginModel);
         this.fillSaBaseClientLoginUserAndUpdateCache(saBaseClientLoginUser);
         return StpClientUtil.getTokenInfo().tokenValue;
      }
   }

   private void fillSaBaseClientLoginUserAndUpdateCache(SaBaseClientLoginUser saBaseClientLoginUser) {
      saBaseClientLoginUser.setBindWechat(this.hasBindWechat(saBaseClientLoginUser.getId()));
      this.fillTelegramBindingInfo(saBaseClientLoginUser);
      StpClientUtil.getTokenSession().set("loginUser", saBaseClientLoginUser);
   }

   private void fillTelegramBindingInfo(SaBaseClientLoginUser saBaseClientLoginUser) {
      DevTelegramBindingInfo bindingInfo = this.devTelegramBindingApi.getBindingInfo("MEMBER", String.valueOf(saBaseClientLoginUser.getId()));
      saBaseClientLoginUser.setTelegramBound(Boolean.TRUE.equals(bindingInfo.getBound()));
      saBaseClientLoginUser.setTelegramChatId(bindingInfo.getChatId());
      saBaseClientLoginUser.setTelegramUserId(bindingInfo.getTelegramUserId());
      saBaseClientLoginUser.setTelegramUsername(bindingInfo.getTelegramUsername());
   }

   private boolean hasBindWechat(Long userId) {
      return this.memberAuthThirdService
            .count((Wrapper<AuthThirdUser>)((LambdaQueryWrapper<AuthThirdUser>)new LambdaQueryWrapper<AuthThirdUser>().eq(AuthThirdUser::getUserId, userId)).eq(AuthThirdUser::getCategory, "WECHAT_OPEN"))
         > 0L;
   }

   @Override
   public SaBaseClientLoginUser getClientLoginUser() {
      SaBaseClientLoginUser saBaseClientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      log.info("获取C端登录用户：", JSON.toJSONString(saBaseClientLoginUser));
      if (null == saBaseClientLoginUser) {
         throw new CommonException("用户未登录", new Object[0]);
      } else {
         log.info("获取C端登录用户ID：", saBaseClientLoginUser.getId());
         saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(saBaseClientLoginUser.getId());
         log.info("获取C端登录用户信息：", JSON.toJSONString(saBaseClientLoginUser));
         this.fillSaBaseClientLoginUserAndUpdateCache(saBaseClientLoginUser);
         saBaseClientLoginUser.setPassword(null);
         saBaseClientLoginUser.setPermissionCodeList(null);
         saBaseClientLoginUser.setDataScopeList(null);
         if (ObjectUtil.isNotEmpty(saBaseClientLoginUser.getPhone())) {
            String decryptedPhone = CommonCryptogramUtil.doSm4CbcDecrypt(saBaseClientLoginUser.getPhone());
            saBaseClientLoginUser.setPhone(decryptedPhone);
         }

         this.fillMemberAmountStatistics(saBaseClientLoginUser);
         return saBaseClientLoginUser;
      }
   }

   private void fillMemberAmountStatistics(SaBaseClientLoginUser loginUser) {
      Long memberNo = loginUser.getId();
      HashMap<String, LocalDateTime> monthRange = this.buildDateRange(LocalDate.now().withDayOfMonth(1).atStartOfDay(), LocalDateTime.now());
      HashMap<String, LocalDateTime> todayRange = this.buildDateRange(LocalDate.now().atStartOfDay(), LocalDateTime.now());
      loginUser.setCurrentMonthConsumeAmount(this.defaultAmount(this.orderApi.OrderPayByCreateTime(monthRange, memberNo)));
      loginUser.setCurrentMonthRechargeAmount(this.defaultAmount(this.rechargeBalanceApi.getRechargeMoneyByDate(monthRange, memberNo)));
      loginUser.setTodayConsumeAmount(this.defaultAmount(this.orderApi.OrderPayByCreateTime(todayRange, memberNo)));
      loginUser.setTotalConsumeAmount(this.defaultAmount(this.orderApi.OrderPayByCreateTime(new HashMap(), memberNo)));
      loginUser.setUnfinishedOrderCount(this.defaultCount(this.orderApi.CountByPayStatus(new HashMap(), OrderStatusEnum.UNPAID.getCode(), memberNo)));
      loginUser.setUnprocessedWorkOrderCount(this.defaultCount(this.adminWorkOrederApi.getUserUnprocessedOrderCount(memberNo)));
      loginUser.setInvoicableOrderCount(this.defaultCount(this.orderApi.countInvoicableOrders(memberNo)));
      loginUser.setInvoicableOrderAmount(this.defaultAmount(this.orderApi.sumInvoicableOrderAmount(memberNo)));
   }

   private HashMap<String, LocalDateTime> buildDateRange(LocalDateTime startTime, LocalDateTime endTime) {
      HashMap<String, LocalDateTime> range = new HashMap<>();
      range.put(DateResponseKeyEnum.START_TIME_NAME.getValue(), startTime);
      range.put(DateResponseKeyEnum.END_TIME_NAME.getValue(), endTime);
      return range;
   }

   private BigDecimal defaultAmount(BigDecimal amount) {
      return amount == null ? BigDecimal.ZERO : amount;
   }

   private Long defaultCount(Long count) {
      return count == null ? 0L : count;
   }

   @Override
   public String doLoginById(Long userId, String device, String type) {
      return this.doLoginById(userId, device, type, null);
   }

   private String doLoginById(Long userId, String device, String type, String operatorAdminId) {
      SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(userId);
      if (ObjectUtil.isEmpty(saBaseClientLoginUser)) {
         throw new CommonException("账号不存在", new Object[0]);
      } else {
         return this.execLoginC(saBaseClientLoginUser, device, operatorAdminId);
      }
   }

   @Override
   public String switchLogin(AuthSwitchLoginParam param) {
      String cacheKey = "admin-member-switch-login:" + param.getTicket();
      Object ticketCache = this.commonCacheOperator.getAndRemove(cacheKey);
      if (ObjectUtil.isEmpty(ticketCache)) {
         throw new CommonException("切换登录链接已过期或已使用", new Object[0]);
      } else {
         JSONObject ticketPayload = JSONUtil.parseObj(ticketCache.toString());
         if (!"ADMIN_MEMBER_SWITCH_LOGIN".equals(ticketPayload.getStr("purpose"))) {
            throw new CommonException("切换登录ticket无效", new Object[0]);
         } else {
            Long memberId = ticketPayload.getLong("memberId");
            if (ObjectUtil.isEmpty(memberId)) {
               throw new CommonException("切换登录ticket缺少会员信息", new Object[0]);
            } else {
               return this.doLoginById(memberId, AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.C.getValue(), ticketPayload.getStr("adminUserId"));
            }
         }
      }
   }

   @Override
   public void register(AuthRegisterParam authRegisterParam, String type) {
      log.info("执行注册:{}", JSON.toJSONString(authRegisterParam));
      if (SaClientTypeEnum.B.getValue().equals(type)) {
         throw new CommonException(AuthExceptionEnum.CLIENT_TYPE_ERROR.getValue(), new Object[0]);
      } else if (ObjectUtil.isEmpty(authRegisterParam.getPhone())) {
         throw new CommonException("手机号不能为空", new Object[0]);
      } else {
         this.checkParam(authRegisterParam);
         String validCode = authRegisterParam.getValidCode();
         String validCodeReqNo = authRegisterParam.getValidCodeReqNo();
         String phone = authRegisterParam.getPhone();
         if (ObjectUtil.isEmpty(validCode)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue(), new Object[0]);
         } else if (ObjectUtil.isEmpty(validCodeReqNo)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue(), new Object[0]);
         } else {
            this.validValidCode(phone, validCode, validCodeReqNo);
            if (!authRegisterParam.getPassword().equals(authRegisterParam.getPasswordConfirm())) {
               throw new CommonException("密码不正确", new Object[0]);
            } else {
               ClientUser clientUser = new ClientUser();
               clientUser.setAccount(authRegisterParam.getAccount());
               clientUser.setPhone(authRegisterParam.getPhone());
               String password = CommonCryptogramUtil.doSm2Encrypt(authRegisterParam.getPassword());
               String passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(password));
               log.info("密码解密后hash：{}", passwordHash);
               clientUser.setPassword(passwordHash);
               clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
               clientUser.setRegisterTime(new Date());
               this.clientUserService.save(clientUser);
               this.memberBalanceService.initializeUserBalance(clientUser.getId());
               this.agentMemberBindService.bindOnRegister(clientUser.getId(), authRegisterParam.getAgentCode());
               if (ObjectUtil.isNotEmpty(authRegisterParam.getAuthUser())) {
                  this.bindUser(authRegisterParam.getAuthUser(), clientUser.getId());
               }
            }
         }
      }
   }

   @Override
   public void registerByEmail(AuthRegisterByEmailParam authRegisterByEmailParam, String type) {
      log.info("执行邮箱注册:{}", JSON.toJSONString(authRegisterByEmailParam));
      if (SaClientTypeEnum.B.getValue().equals(type)) {
         throw new CommonException(AuthExceptionEnum.CLIENT_TYPE_ERROR.getValue(), new Object[0]);
      } else if (ObjectUtil.isEmpty(authRegisterByEmailParam.getEmail())) {
         throw new CommonException("邮箱不能为空", new Object[0]);
      } else {
         this.checkParamByEmail(authRegisterByEmailParam);
         String validCode = authRegisterByEmailParam.getValidCode();
         String validCodeReqNo = authRegisterByEmailParam.getValidCodeReqNo();
         String email = authRegisterByEmailParam.getEmail();
         if (ObjectUtil.isEmpty(validCode)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue(), new Object[0]);
         } else if (ObjectUtil.isEmpty(validCodeReqNo)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue(), new Object[0]);
         } else {
            this.validValidCode(email, validCode, validCodeReqNo);
            if (!authRegisterByEmailParam.getPassword().equals(authRegisterByEmailParam.getPasswordConfirm())) {
               throw new CommonException("密码不正确", new Object[0]);
            } else {
               ClientUser clientUser = new ClientUser();
               clientUser.setAccount(authRegisterByEmailParam.getAccount());
               clientUser.setEmail(authRegisterByEmailParam.getEmail());
               String password = CommonCryptogramUtil.doSm2Encrypt(authRegisterByEmailParam.getPassword());
               String passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(password));
               log.info("密码解密后hash：{}", passwordHash);
               clientUser.setPassword(passwordHash);
               clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
               clientUser.setRegisterTime(new Date());
               this.clientUserService.save(clientUser);
               this.memberBalanceService.initializeUserBalance(clientUser.getId());
               this.agentMemberBindService.bindOnRegister(clientUser.getId(), authRegisterByEmailParam.getAgentCode());
               if (ObjectUtil.isNotEmpty(authRegisterByEmailParam.getAuthUser())) {
                  this.bindUser(authRegisterByEmailParam.getAuthUser(), clientUser.getId());
               }
            }
         }
      }
   }

   private Long bindUser(AuthUser authUser, Long userId) {
      AuthThirdUser authThirdUser = new AuthThirdUser();
      authThirdUser.setThirdId(authUser.getUuid());
      authThirdUser.setUserId(userId);
      authThirdUser.setAvatar(authUser.getAvatar());
      authThirdUser.setName(authUser.getUsername());
      authThirdUser.setNickname(authUser.getNickname());
      authThirdUser.setGender(authUser.getGender().getDesc());
      authThirdUser.setCategory(authUser.getSource());
      authThirdUser.setExtJson(JSONUtil.toJsonStr(authUser.getRawUserInfo()));
      this.memberAuthThirdService.save(authThirdUser);
      return authThirdUser.getUserId();
   }

   private void checkParam(AuthRegisterParam authRegisterParam) {
      if (CommonEmailUtil.isEmail(authRegisterParam.getAccount())) {
         throw new CommonException("用户名不能为邮箱格式", new Object[0]);
      } else if (this.clientUserService.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, authRegisterParam.getAccount())) > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{authRegisterParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(authRegisterParam.getPhone())) {
            if (!PhoneUtil.isMobile(authRegisterParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{authRegisterParam.getPhone()});
            }

            if (this.clientUserService
                  .count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(authRegisterParam.getPhone())))
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{authRegisterParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(authRegisterParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(authRegisterParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{authRegisterParam.getEmail()});
            }

            if (this.clientUserService.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, authRegisterParam.getEmail())) > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{authRegisterParam.getEmail()});
            }
         }
      }
   }

   private void checkParamByEmail(AuthRegisterByEmailParam authRegisterByEmailParam) {
      if (this.clientUserService.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, authRegisterByEmailParam.getAccount())) > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{authRegisterByEmailParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(authRegisterByEmailParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(authRegisterByEmailParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{authRegisterByEmailParam.getEmail()});
            }

            if (this.clientUserService.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, authRegisterByEmailParam.getEmail())) > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{authRegisterByEmailParam.getEmail()});
            }
         }
      }
   }

   @Override
   public void updatePassword(AuthUpdateLoginPasswordParam authUpdateLoginPasswordParam, String type) {
      SaBaseClientLoginUser saBaseClientLoginUser = this.getClientLoginUser();
      ClientUser clientUser = (ClientUser)this.clientUserService.getById(saBaseClientLoginUser.getId());
      String passwordHash = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authUpdateLoginPasswordParam.getOldPassword()));
      log.info("旧密码：{}，哈希值：{}", clientUser.getPassword(), passwordHash);
      if (!passwordHash.equals(clientUser.getPassword())) {
         throw new CommonException("旧密码不正确", new Object[0]);
      } else {
         String newPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authUpdateLoginPasswordParam.getNewPassword()));
         String passwordConfirm = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authUpdateLoginPasswordParam.getPasswordConfirm()));
         log.info("新密码：{}，确认密码：{}", newPassword, passwordConfirm);
         if (ObjectUtil.isEmpty(newPassword) || ObjectUtil.isEmpty(passwordConfirm)) {
            throw new CommonException("新密码或确认密码不能为空", new Object[0]);
         } else if (!newPassword.equals(passwordConfirm)) {
            throw new CommonException("新密码不一致", new Object[0]);
         } else {
            String finalPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authUpdateLoginPasswordParam.getNewPassword()));
            ((this.clientUserService.lambdaUpdate().eq(ClientUser::getId, saBaseClientLoginUser.getId()))
                  .set(ClientUser::getPassword, finalPassword))
               .update();
         }
      }
   }

   @Override
   public void resetPassword(AuthResetLoginPasswordParam authResetLoginPasswordParam, String value) {
      String validCode = authResetLoginPasswordParam.getValidCode();
      String validCodeReqNo = authResetLoginPasswordParam.getValidCodeReqNo();
      String phone = authResetLoginPasswordParam.getPhone();
      if (ObjectUtil.isEmpty(validCode)) {
         throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue(), new Object[0]);
      } else if (ObjectUtil.isEmpty(validCodeReqNo)) {
         throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue(), new Object[0]);
      } else {
         this.validValidCode(phone, validCode, validCodeReqNo);
         ClientLoginUser clientLoginUser = this.clientUserService.getUserByPhone(authResetLoginPasswordParam.getPhone());
         if (ObjectUtil.isEmpty(clientLoginUser)) {
            throw new CommonException("手机号不存在", new Object[0]);
         } else {
            String newPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authResetLoginPasswordParam.getNewPassword()));
            String passwordConfirm = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authResetLoginPasswordParam.getPasswordConfirm()));
            log.info("新密码：{}，确认密码：{}", newPassword, passwordConfirm);
            if (ObjectUtil.isEmpty(newPassword) || ObjectUtil.isEmpty(passwordConfirm)) {
               throw new CommonException("新密码或确认密码不能为空", new Object[0]);
            } else if (!newPassword.equals(passwordConfirm)) {
               throw new CommonException("新密码不一致", new Object[0]);
            } else {
               String finalPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authResetLoginPasswordParam.getNewPassword()));
               ((this.clientUserService.lambdaUpdate().eq(ClientUser::getId, clientLoginUser.getId()))
                     .set(ClientUser::getPassword, finalPassword))
                  .update();
            }
         }
      }
   }

   @Override
   public void resetPasswordByEmail(AuthResetPasswordByEmailParam authResetPasswordByEmailParam, String value) {
      log.info("执行邮箱找回密码:{}", JSON.toJSONString(authResetPasswordByEmailParam));
      if (SaClientTypeEnum.B.getValue().equals(value)) {
         throw new CommonException(AuthExceptionEnum.CLIENT_TYPE_ERROR.getValue(), new Object[0]);
      } else if (ObjectUtil.isEmpty(authResetPasswordByEmailParam.getEmail())) {
         throw new CommonException("邮箱不能为空", new Object[0]);
      } else {
         String validCode = authResetPasswordByEmailParam.getValidCode();
         String validCodeReqNo = authResetPasswordByEmailParam.getValidCodeReqNo();
         String email = authResetPasswordByEmailParam.getEmail();
         if (ObjectUtil.isEmpty(validCode)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_EMPTY.getValue(), new Object[0]);
         } else if (ObjectUtil.isEmpty(validCodeReqNo)) {
            throw new CommonException(AuthExceptionEnum.VALID_CODE_REQ_NO_EMPTY.getValue(), new Object[0]);
         } else {
            this.validValidCode(email, validCode, validCodeReqNo);
            ClientLoginUser clientLoginUser = this.clientUserService.getUserByEmail(email);
            if (ObjectUtil.isEmpty(clientLoginUser)) {
               throw new CommonException("邮箱不存在", new Object[0]);
            } else {
               String newPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authResetPasswordByEmailParam.getNewPassword()));
               String passwordConfirm = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authResetPasswordByEmailParam.getPasswordConfirm()));
               log.info("新密码：{}，确认密码：{}", newPassword, passwordConfirm);
               if (ObjectUtil.isEmpty(newPassword) || ObjectUtil.isEmpty(passwordConfirm)) {
                  throw new CommonException("新密码或确认密码不能为空", new Object[0]);
               } else if (!newPassword.equals(passwordConfirm)) {
                  throw new CommonException("新密码不一致", new Object[0]);
               } else {
                  String finalPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(authResetPasswordByEmailParam.getNewPassword()));
                  ((this.clientUserService.lambdaUpdate().eq(ClientUser::getId, clientLoginUser.getId()))
                        .set(ClientUser::getPassword, finalPassword))
                     .update();
               }
            }
         }
      }
   }

   @Override
   public CommonResult<ClientUser> checkAccount(AuthCheckAccountParam authCheckAccountParam, String value) {
      String defaultCaptchaType = this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_CAPTCHA_TYPE");
      if (defaultCaptchaType.equals("TENCENT")) {
         this.validTencentCaptcha(authCheckAccountParam.getValidCode(), authCheckAccountParam.getValidCodeReqNo());
      } else {
         this.validValidCode(null, authCheckAccountParam.getValidCode(), authCheckAccountParam.getValidCodeReqNo());
      }

      ClientUser clientUser = (ClientUser)this.clientUserService
         .getOne((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, authCheckAccountParam.getAccount()));
      if (ObjectUtil.isEmpty(clientUser)) {
         CommonResult.error("账号不存在");
      }

      return CommonResult.data(clientUser);
   }

   @Override
   public String getBindPhoneValidCode(AuthGetPhoneValidCodeParam authGetPhoneValidCodeParam, String type) {
      SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
      if (ObjectUtil.isEmpty(currentUser)) {
         throw new CommonException("用户未登录", new Object[0]);
      } else {
         String phone = authGetPhoneValidCodeParam.getPhone();
         String validCode = authGetPhoneValidCodeParam.getValidCode();
         String validCodeReqNo = authGetPhoneValidCodeParam.getValidCodeReqNo();
         this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
         if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue(), new Object[0]);
         } else {
            ClientUser currentClientUser = (ClientUser)this.clientUserService.getById(currentUser.getId());
            if (ObjectUtil.isNotEmpty(currentClientUser) && ObjectUtil.isNotEmpty(currentClientUser.getPhone())) {
               throw new CommonException("您已绑定手机号，不允许修改", new Object[0]);
            } else {
               ClientLoginUser existUser = this.clientUserService.getUserByPhone(phone);
               if (ObjectUtil.isNotEmpty(existUser)) {
                  throw new CommonException("该手机号已被其他账号绑定", new Object[0]);
               } else {
                  String phoneValidCode = RandomUtil.randomNumbers(6);
                  String phoneValidCodeReqNo = IdWorker.getIdStr();
                  log.info("发送绑定手机验证码开始：phone:{},code:{}", phone, phoneValidCode);
                  Map<String, Object> map = new HashMap<>();
                  map.put("code", phoneValidCode);
                  this.toolsNotificationApi.sendNotificationSms(NotificationTypeEnum.MEMBER_BIND_PHONE_VERIFY.getCode(), phone, map);
                  log.info("发送绑定手机验证码结束：phone:{},code:{}", phone, phoneValidCode);
                  this.commonCacheOperator.put("auth-validCode:" + phone + "_" + phoneValidCodeReqNo, phoneValidCode, 300L);
                  return phoneValidCodeReqNo;
               }
            }
         }
      }
   }

   @Override
   public void bindPhone(AuthBindPhoneParam authBindPhoneParam, String type) {
      SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
      if (ObjectUtil.isEmpty(currentUser)) {
         throw new CommonException("用户未登录", new Object[0]);
      } else {
         String phone = authBindPhoneParam.getPhone();
         String validCode = authBindPhoneParam.getValidCode();
         String validCodeReqNo = authBindPhoneParam.getValidCodeReqNo();
         this.validValidCode(phone, validCode, validCodeReqNo);
         if (!PhoneUtil.isMobile(phone)) {
            throw new CommonException(AuthExceptionEnum.PHONE_FORMAT_ERROR.getValue(), new Object[0]);
         } else {
            ClientUser currentClientUser = (ClientUser)this.clientUserService.getById(currentUser.getId());
            if (ObjectUtil.isNotEmpty(currentClientUser) && ObjectUtil.isNotEmpty(currentClientUser.getPhone())) {
               throw new CommonException("您已绑定手机号，不允许修改", new Object[0]);
            } else {
               ClientLoginUser existUser = this.clientUserService.getUserByPhone(phone);
               if (ObjectUtil.isNotEmpty(existUser)) {
                  throw new CommonException("该手机号已被其他账号绑定", new Object[0]);
               } else {
                  currentClientUser.setPhone(phone);
                  this.clientUserService.updateById(currentClientUser);
                  SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(currentUser.getId());
                  StpClientLoginUserUtil.updateClientLoginUser(saBaseClientLoginUser);
               }
            }
         }
      }
   }

   @Override
   public String getBindEmailValidCode(AuthGetEmailValidCodeParam authGetEmailValidCodeParam, String type) {
      SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
      if (ObjectUtil.isEmpty(currentUser)) {
         throw new CommonException("用户未登录", new Object[0]);
      } else {
         String email = authGetEmailValidCodeParam.getEmail();
         String validCode = authGetEmailValidCodeParam.getValidCode();
         String validCodeReqNo = authGetEmailValidCodeParam.getValidCodeReqNo();
         this.validPhoneValidCodeParam(null, validCode, validCodeReqNo, type);
         if (!CommonEmailUtil.isEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", new Object[]{email});
         } else {
            ClientUser currentClientUser = (ClientUser)this.clientUserService.getById(currentUser.getId());
            if (ObjectUtil.isNotEmpty(currentClientUser) && ObjectUtil.isNotEmpty(currentClientUser.getEmail())) {
               throw new CommonException("您已绑定邮箱，不允许修改", new Object[0]);
            } else {
               ClientLoginUser existUser = this.clientUserService.getUserByEmail(email);
               if (ObjectUtil.isNotEmpty(existUser)) {
                  throw new CommonException("该邮箱已被其他账号绑定", new Object[0]);
               } else {
                  String emailValidCode = RandomUtil.randomNumbers(6);
                  String emailValidCodeReqNo = IdWorker.getIdStr();
                  log.info("发送绑定邮箱验证码开始：email:{},code:{}", email, emailValidCode);
                  Map<String, Object> map = new HashMap<>();
                  map.put("code", emailValidCode);
                  this.toolsNotificationApi.sendNotificationEmail(NotificationTypeEnum.MEMBER_BIND_EMAIL_VERIFY.getCode(), email, map);
                  log.info("发送绑定邮箱验证码结束：email:{},code:{}", email, emailValidCode);
                  this.commonCacheOperator.put("auth-validCode:" + email + "_" + emailValidCodeReqNo, emailValidCode, 300L);
                  return emailValidCodeReqNo;
               }
            }
         }
      }
   }

   @Override
   public void bindEmail(AuthBindEmailParam authBindEmailParam, String type) {
      SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
      if (ObjectUtil.isEmpty(currentUser)) {
         throw new CommonException("用户未登录", new Object[0]);
      } else {
         String email = authBindEmailParam.getEmail();
         String validCode = authBindEmailParam.getValidCode();
         String validCodeReqNo = authBindEmailParam.getValidCodeReqNo();
         this.validValidCode(email, validCode, validCodeReqNo);
         if (!CommonEmailUtil.isEmail(email)) {
            throw new CommonException("邮箱：{}格式错误", new Object[]{email});
         } else {
            ClientUser currentClientUser = (ClientUser)this.clientUserService.getById(currentUser.getId());
            if (ObjectUtil.isNotEmpty(currentClientUser) && ObjectUtil.isNotEmpty(currentClientUser.getEmail())) {
               throw new CommonException("您已绑定邮箱，不允许修改", new Object[0]);
            } else {
               ClientLoginUser existUser = this.clientUserService.getUserByEmail(email);
               if (ObjectUtil.isNotEmpty(existUser)) {
                  throw new CommonException("该邮箱已被其他账号绑定", new Object[0]);
               } else {
                  LambdaUpdateWrapper<ClientUser> updateWrapper = new LambdaUpdateWrapper();
                  ((LambdaUpdateWrapper<ClientUser>)updateWrapper.eq(ClientUser::getId, currentUser.getId())).set(ClientUser::getEmail, email);
                  this.clientUserService.update(updateWrapper);
                  SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(currentUser.getId());
                  StpClientLoginUserUtil.updateClientLoginUser(saBaseClientLoginUser);
               }
            }
         }
      }
   }
}
