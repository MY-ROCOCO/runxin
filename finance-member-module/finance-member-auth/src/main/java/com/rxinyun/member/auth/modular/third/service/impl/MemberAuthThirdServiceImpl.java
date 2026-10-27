package com.rxinyun.member.auth.modular.third.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.login.enums.AuthDeviceTypeEnum;
import com.rxinyun.member.auth.modular.login.service.MemberAuthService;
import com.rxinyun.member.auth.modular.third.entity.AuthThirdUser;
import com.rxinyun.member.auth.modular.third.enums.AuthThirdPlatformEnum;
import com.rxinyun.member.auth.modular.third.mapper.MemberAuthThirdMapper;
import com.rxinyun.member.auth.modular.third.param.AuthThirdCallbackParam;
import com.rxinyun.member.auth.modular.third.param.AuthThirdRenderParam;
import com.rxinyun.member.auth.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.member.auth.modular.third.result.AuthThirdRenderResult;
import com.rxinyun.member.auth.modular.third.service.MemberAuthThirdService;
import com.rxinyun.member.core.enums.SaClientTypeEnum;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.hutool.HutoolImpl;
import jakarta.annotation.Resource;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthGiteeRequest;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.request.AuthWeChatOpenRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberAuthThirdServiceImpl extends ServiceImpl<MemberAuthThirdMapper, AuthThirdUser> implements MemberAuthThirdService {
   private static final Logger log = LoggerFactory.getLogger(MemberAuthThirdServiceImpl.class);
   private static final String RUNXIN_THIRD_GITEE_CLIENT_ID_KEY = "RUNXIN_THIRD_GITEE_CLIENT_ID";
   private static final String RUNXIN_THIRD_GITEE_CLIENT_SECRET_KEY = "RUNXIN_THIRD_GITEE_CLIENT_SECRET";
   private static final String RUNXIN_THIRD_WECHAT_CLIENT_ID_KEY = "RUNXIN_THIRD_WECHAT_CLIENT_ID";
   private static final String RUNXIN_THIRD_WECHAT_CLIENT_SECRET_KEY = "RUNXIN_THIRD_WECHAT_CLIENT_SECRET";
   private static final String RUNXIN_SYS_HOME_URL = "RUNXIN_SYS_HOME_URL";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private MemberAuthService authService;
   @Resource
   private MemberSaBaseLoginUserApi clientLoginUserApi;

   @Override
   public AuthThirdRenderResult render(AuthThirdRenderParam authThirdRenderParam) {
      AuthRequest authRequest = this.getAuthRequest(authThirdRenderParam.getPlatform());
      String state = AuthStateUtils.createState();
      String authorizeUrl = authRequest.authorize(state);
      AuthThirdRenderResult authThirdRenderResult = new AuthThirdRenderResult();
      authThirdRenderResult.setAuthorizeUrl(authorizeUrl);
      authThirdRenderResult.setState(state);
      return authThirdRenderResult;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult callback(AuthThirdCallbackParam authThirdCallbackParam, AuthCallback authCallback) {
      AuthRequest authRequest = this.getAuthRequest(authThirdCallbackParam.getPlatform());
      AuthResponse<AuthUser> authResponse = authRequest.login(authCallback);
      if (authResponse.ok()) {
         AuthUser authUser = (AuthUser)authResponse.getData();
         String uuid = authUser.getUuid();
         String source = authUser.getSource();
         AuthThirdUser authThirdUser = this.getAuthThirdUser(uuid, source);
         Long userId = null;
         if (ObjectUtil.isEmpty(authThirdUser)) {
            return CommonResult.error("用户不存在，返回需要绑定或注册", authUser);
         } else {
            if (StpClientUtil.isLogin()) {
               SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
               if (ObjectUtil.isNotEmpty(currentUser)) {
                  if (ObjectUtil.equal(authThirdUser.getUserId(), currentUser.getId())) {
                     return CommonResult.ok("当前微信已绑定该账号");
                  }

                  return CommonResult.error("该微信已绑定其他账号");
               }
            }

            userId = authThirdUser.getUserId();
            return CommonResult.data(this.authService.doLoginById(userId, AuthDeviceTypeEnum.PC.getValue(), SaClientTypeEnum.B.getValue()));
         }
      } else {
         throw new CommonException("第三方登录授权回调失败，原因：{}", new Object[]{authResponse.getMsg()});
      }
   }

   @Override
   public Page<AuthThirdUser> page(AuthThirdUserPageParam authThirdUserPageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (clientLoginUser != null && clientLoginUser.getId() != null) {
         QueryWrapper<AuthThirdUser> queryWrapper = new QueryWrapper().checkSqlInjection();
         queryWrapper.lambda().eq(AuthThirdUser::getUserId, clientLoginUser.getId());
         if (ObjectUtil.isNotEmpty(authThirdUserPageParam.getCategory())) {
            queryWrapper.lambda().eq(AuthThirdUser::getCategory, authThirdUserPageParam.getCategory());
         }

         if (ObjectUtil.isNotEmpty(authThirdUserPageParam.getSearchKey())) {
            queryWrapper.and(
               q -> ((LambdaQueryWrapper<AuthThirdUser>)((LambdaQueryWrapper<AuthThirdUser>)q.lambda().like(AuthThirdUser::getName, authThirdUserPageParam.getSearchKey())).or())
                  .like(AuthThirdUser::getNickname, authThirdUserPageParam.getSearchKey())
            );
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{authThirdUserPageParam.getSortField(), authThirdUserPageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(authThirdUserPageParam.getSortOrder());
            queryWrapper.orderBy(
               true,
               authThirdUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()),
               StrUtil.toUnderlineCase(authThirdUserPageParam.getSortField())
            );
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<AuthThirdUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      } else {
         throw new CommonException("请先登录", new Object[0]);
      }
   }

   @Override
   public CommonResult bind(AuthUser authUser) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      AuthThirdUser existedAuthThirdUser = this.getAuthThirdUser(authUser.getUuid(), authUser.getSource());
      if (ObjectUtil.isNotEmpty(existedAuthThirdUser)) {
         return ObjectUtil.equal(existedAuthThirdUser.getUserId(), clientLoginUser.getId()) ? CommonResult.ok("当前微信已绑定该账号") : CommonResult.error("该微信已绑定其他账号");
      } else {
         AuthThirdUser authThirdUser = new AuthThirdUser();
         authThirdUser.setThirdId(authUser.getUuid());
         authThirdUser.setUserId(clientLoginUser.getId());
         authThirdUser.setAvatar(authUser.getAvatar());
         authThirdUser.setName(authUser.getUsername());
         authThirdUser.setNickname(authUser.getNickname());
         authThirdUser.setGender(authUser.getGender().getDesc());
         authThirdUser.setCategory(authUser.getSource());
         authThirdUser.setExtJson(JSONUtil.toJsonStr(authUser.getRawUserInfo()));
         this.save(authThirdUser);
         return CommonResult.ok("绑定成功");
      }
   }

   private AuthThirdUser getAuthThirdUser(String thirdId, String category) {
      return (AuthThirdUser)this.getOne(
         (Wrapper<AuthThirdUser>)((LambdaQueryWrapper<AuthThirdUser>)new LambdaQueryWrapper<AuthThirdUser>().eq(AuthThirdUser::getThirdId, thirdId)).eq(AuthThirdUser::getCategory, category)
      );
   }

   private AuthRequest getAuthRequest(String source) {
      AuthRequest authRequest = null;
      source = source.toUpperCase();
      HttpUtil.setHttp(new HutoolImpl());
      AuthThirdPlatformEnum.validate(source);
      if (source.equals(AuthThirdPlatformEnum.GITEE.getValue())) {
         String homeUrl = this.devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL") + "/member-api/member/auth/third/callback?platform=wechat";
         authRequest = new AuthGiteeRequest(
            AuthConfig.builder()
               .clientId(this.devConfigApi.getValueByKey("RUNXIN_THIRD_GITEE_CLIENT_ID"))
               .clientSecret(this.devConfigApi.getValueByKey("RUNXIN_THIRD_GITEE_CLIENT_SECRET"))
               .redirectUri(homeUrl)
               .build()
         );
      }

      if (source.equals(AuthThirdPlatformEnum.WECHAT.getValue())) {
         String homeUrl = this.devConfigApi.getValueByKey("RUNXIN_SYS_HOME_URL") + "/member-api/member/auth/third/callback?platform=wechat";
         log.info("微信登录RUNXIN_THIRD_WECHAT_REDIRECT_URL_KEY:{}", homeUrl);
         authRequest = new AuthWeChatOpenRequest(
            AuthConfig.builder()
               .clientId(this.devConfigApi.getValueByKey("RUNXIN_THIRD_WECHAT_CLIENT_ID"))
               .clientSecret(this.devConfigApi.getValueByKey("RUNXIN_THIRD_WECHAT_CLIENT_SECRET"))
               .redirectUri(homeUrl)
               .build()
         );
      }

      return authRequest;
   }
}
