package com.rxinyun.member.auth.modular.user.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.CommonAvatarUtil;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.auth.modular.relation.service.ClientRelationService;
import com.rxinyun.member.auth.modular.user.entity.ClientUser;
import com.rxinyun.member.auth.modular.user.enums.ClientUserStatusEnum;
import com.rxinyun.member.auth.modular.user.mapper.ClientUserMapper;
import com.rxinyun.member.auth.modular.user.param.ClientUserAddParam;
import com.rxinyun.member.auth.modular.user.param.ClientUserEditParam;
import com.rxinyun.member.auth.modular.user.param.ClientUserIdParam;
import com.rxinyun.member.auth.modular.user.param.ClientUserPageParam;
import com.rxinyun.member.auth.modular.user.result.ClientLoginUser;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.api.ClientUserApi;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientUserServiceImpl extends ServiceImpl<ClientUserMapper, ClientUser> implements ClientUserService, ClientUserApi {
   private static final Logger log = LoggerFactory.getLogger(ClientUserServiceImpl.class);
   private static final String RUNXIN_SYS_DEFAULT_PASSWORD_KEY = "RUNXIN_SYS_DEFAULT_PASSWORD";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private ClientUserMapper clientUserMapper;
   @Resource
   private MemberSaBaseLoginUserApi clientLoginUserApi;
   @Resource
   private ClientRelationService clientRelationService;

   @Override
   public ClientLoginUser getUserById(Long id) {
      ClientUser clientUser = (ClientUser)this.getById(id);
      return ObjectUtil.isNotEmpty(clientUser) ? (ClientLoginUser)BeanUtil.copyProperties(clientUser, ClientLoginUser.class, new String[0]) : null;
   }

   @Override
   public ClientLoginUser getUserByAccount(String account) {
      ClientUser clientUser = (ClientUser)this.getOne((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, account));
      return ObjectUtil.isNotEmpty(clientUser) ? (ClientLoginUser)BeanUtil.copyProperties(clientUser, ClientLoginUser.class, new String[0]) : null;
   }

   @Override
   public ClientLoginUser getUserByPhone(String phone) {
      ClientUser clientUser = (ClientUser)this.getOne((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(phone)));
      return ObjectUtil.isNotEmpty(clientUser) ? (ClientLoginUser)BeanUtil.copyProperties(clientUser, ClientLoginUser.class, new String[0]) : null;
   }

   @Override
   public ClientLoginUser getUserByEmail(String email) {
      ClientUser clientUser = (ClientUser)this.getOne((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, email));
      return ObjectUtil.isNotEmpty(clientUser) ? (ClientLoginUser)BeanUtil.copyProperties(clientUser, ClientLoginUser.class, new String[0]) : null;
   }

   @Override
   public Page<ClientUser> page(ClientUserPageParam clientUserPageParam) {
      QueryWrapper<ClientUser> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(clientUserPageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<ClientUser>)((LambdaQueryWrapper<ClientUser>)q.like(ClientUser::getName, clientUserPageParam.getSearchKey())).or())
                  .like(ClientUser::getAccount, clientUserPageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{clientUserPageParam.getSortField(), clientUserPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(clientUserPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, clientUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(clientUserPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByAsc(ClientUser::getSortCode);
      }

      return (Page<ClientUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ClientUserAddParam clientUserAddParam) {
      this.checkParam(clientUserAddParam);
      ClientUser clientUser = (ClientUser)BeanUtil.toBean(clientUserAddParam, ClientUser.class);
      if (ObjectUtil.isEmpty(clientUser.getAvatar())) {
         clientUser.setAvatar(CommonAvatarUtil.generateImg(clientUser.getName()));
      }

      clientUser.setPassword(CommonCryptogramUtil.doHashValue(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_PASSWORD")));
      clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
      this.save(clientUser);
   }

   private void checkParam(ClientUserAddParam clientUserAddParam) {
      if (this.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, clientUserAddParam.getAccount())) > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{clientUserAddParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(clientUserAddParam.getPhone())) {
            if (!PhoneUtil.isMobile(clientUserAddParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{clientUserAddParam.getPhone()});
            }

            if (this.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserAddParam.getPhone())))
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{clientUserAddParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(clientUserAddParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(clientUserAddParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{clientUserAddParam.getEmail()});
            }

            if (this.count((Wrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, clientUserAddParam.getEmail())) > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{clientUserAddParam.getEmail()});
            }
         }
      }
   }

   @Override
   public void edit(ClientUserEditParam clientUserEditParam) {
      ClientUser clientUser = this.queryEntity(clientUserEditParam.getId());
      log.info("编辑用户信息：{}", JSON.toJSONString(clientUser));
      this.checkParam(clientUserEditParam);
      BeanUtil.copyProperties(clientUserEditParam, clientUser, new String[0]);
      log.info("编辑用户信息clientUser：{}", JSON.toJSONString(clientUser));
      this.updateById(clientUser);
      SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(clientUser.getId());
      StpClientLoginUserUtil.updateClientLoginUser(saBaseClientLoginUser);
   }

   private void checkParam(ClientUserEditParam clientUserEditParam) {
      if (this.count(
            (Wrapper<ClientUser>)((LambdaQueryWrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getAccount, clientUserEditParam.getAccount()))
               .ne(ClientUser::getId, clientUserEditParam.getId())
         )
         > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{clientUserEditParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(clientUserEditParam.getPhone())) {
            if (!PhoneUtil.isMobile(clientUserEditParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{clientUserEditParam.getPhone()});
            }

            if (this.count(
                  (Wrapper<ClientUser>)((LambdaQueryWrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>()
                        .eq(ClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserEditParam.getPhone())))
                     .ne(ClientUser::getId, clientUserEditParam.getId())
               )
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{clientUserEditParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(clientUserEditParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(clientUserEditParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{clientUserEditParam.getEmail()});
            }

            if (this.count(
                  (Wrapper<ClientUser>)((LambdaQueryWrapper<ClientUser>)new LambdaQueryWrapper<ClientUser>().eq(ClientUser::getEmail, clientUserEditParam.getEmail()))
                     .ne(ClientUser::getId, clientUserEditParam.getId())
               )
               > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{clientUserEditParam.getEmail()});
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void delete(List<ClientUserIdParam> clientUserIdParamList) {
      this.removeByIds(CollStreamUtil.toList(clientUserIdParamList, ClientUserIdParam::getId));
   }

   @Override
   public ClientUser detail(ClientUserIdParam clientUserIdParam) {
      return this.queryEntity(clientUserIdParam.getId());
   }

   @Override
   public void updateUserLoginInfo(Long userId, String device) {
      ClientUser clientUser = this.queryEntity(userId);
      clientUser.setLastLoginTime(clientUser.getLatestLoginTime());
      clientUser.setLastLoginIp(clientUser.getLatestLoginIp());
      clientUser.setLastLoginAddress(clientUser.getLatestLoginAddress());
      clientUser.setLastLoginDevice(clientUser.getLatestLoginDevice());
      clientUser.setLatestLoginTime(TimeUtil.toDate(TimeUtil.now()));
      String ip = CommonIpAddressUtil.getIp(CommonServletUtil.getRequest());
      clientUser.setLatestLoginIp(ip);
      clientUser.setLatestLoginAddress(CommonIpAddressUtil.getCityInfo(ip));
      clientUser.setLatestLoginDevice(device);
      this.updateById(clientUser);
   }

   @Override
   public ClientUser queryEntity(Long id) {
      ClientUser clientUser = (ClientUser)this.getById(id);
      if (ObjectUtil.isEmpty(clientUser)) {
         throw new CommonException("用户不存在，id值为：{}", new Object[]{id});
      } else {
         return clientUser;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult updateByRealStatus(Long id) {
      ((this.lambdaUpdate().eq(ClientUser::getId, id)).set(ClientUser::getRealNameVerified, 1)).update();
      SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(id);
      StpClientLoginUserUtil.updateClientLoginUser(saBaseClientLoginUser);
      return CommonResult.ok();
   }

   @Override
   public void updateByRealUser(Long memberNo, String name, String userType) {
      (((this.lambdaUpdate().eq(ClientUser::getId, memberNo))
               .set(ClientUser::getName, name))
            .set(ClientUser::getUserType, userType))
         .update();
      SaBaseClientLoginUser saBaseClientLoginUser = this.clientLoginUserApi.getClientUserById(memberNo);
      List<String> tokenValueList = StpClientUtil.getTokenValueListByLoginId(memberNo);
      if (ObjectUtil.isNotEmpty(tokenValueList)) {
         for (String tokenValue : tokenValueList) {
            try {
               SaSession tokenSession = StpClientUtil.getTokenSessionByToken(tokenValue);
               tokenSession.set("loginUser", saBaseClientLoginUser);
            } catch (Exception var9) {
               log.warn("刷新C端token-session失败，memberNo={}, token={}", new Object[]{memberNo, tokenValue, var9});
            }
         }
      }
   }

   @Override
   public void updateNotificationEnabled(Integer notificationEnabled) {
      if (!Integer.valueOf(0).equals(notificationEnabled) && !Integer.valueOf(1).equals(notificationEnabled)) {
         throw new CommonException("通知开关状态错误", new Object[0]);
      } else {
         SaBaseClientLoginUser currentUser = StpClientLoginUserUtil.getClientLoginUser();
         if (ObjectUtil.isEmpty(currentUser)) {
            throw new CommonException("用户未登录", new Object[0]);
         } else {
            ((this.lambdaUpdate().eq(ClientUser::getId, currentUser.getId()))
                  .set(ClientUser::getNotificationEnabled, notificationEnabled))
               .update();
            SaBaseClientLoginUser loginUser = this.clientLoginUserApi.getClientUserById(currentUser.getId());
            StpClientLoginUserUtil.updateClientLoginUser(loginUser);
         }
      }
   }

   public Long getUserCountByCreateTime(HashMap<String, LocalDateTime> hashMap) {
      LocalDateTime start = hashMap.get(DateResponseKeyEnum.START_TIME_NAME.getValue());
      LocalDateTime end = hashMap.get(DateResponseKeyEnum.END_TIME_NAME.getValue());
      return ((this.lambdaQuery().ge(CommonEntity::getCreateTime, start))
            .le(CommonEntity::getCreateTime, end))
         .count();
   }

   public static void main(String[] args) {
      System.out.println(CommonCryptogramUtil.doSm4CbcEncrypt("19560562334"));
   }
}
