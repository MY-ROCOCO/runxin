package com.rxinyun.admin.member.modular.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.member.modular.groups.entity.ClientMemberGroups;
import com.rxinyun.admin.member.modular.groups.service.GroupsService;
import com.rxinyun.admin.member.modular.relation.service.AdminClientRelationService;
import com.rxinyun.admin.member.modular.third.service.AdminAuthThirdService;
import com.rxinyun.admin.member.modular.user.entity.AdminClientUser;
import com.rxinyun.admin.member.modular.user.entity.AdminMemberBalance;
import com.rxinyun.admin.member.modular.user.enums.ClientUserStatusEnum;
import com.rxinyun.admin.member.modular.user.mapper.AdminClientUserMapper;
import com.rxinyun.admin.member.modular.user.param.AdminMemberAddParam;
import com.rxinyun.admin.member.modular.user.param.ClientEmailUpdateParam;
import com.rxinyun.admin.member.modular.user.param.ClientPhoneUpdateParam;
import com.rxinyun.admin.member.modular.user.param.ClientResetPasswordParam;
import com.rxinyun.admin.member.modular.user.param.ClientSwitchLoginTicketParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserAddParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserDataStatisticsParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserEditParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserIdParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserPageParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserStatusParam;
import com.rxinyun.admin.member.modular.user.param.UpdateMemberGroupParam;
import com.rxinyun.admin.member.modular.user.response.ClientUserResponse;
import com.rxinyun.admin.member.modular.user.service.AdminClientUserService;
import com.rxinyun.admin.member.modular.user.service.AdminMemberBalanceService;
import com.rxinyun.auth.api.LoginAdminApi;
import com.rxinyun.biz.BizUserApi;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.util.CommonAvatarUtil;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonEmailUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.common.util.date.DateConversionFactoryUtil;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.core.util.StpClientUtil;
import com.rxinyun.member.member.api.RechargeBalanceApi;
import com.rxinyun.order.api.OrderApi;
import com.rxinyun.payments.api.BalanceAdjustmentApi;
import com.rxinyun.payments.api.PaymentsRecordApi;
import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;
import com.rxinyun.workorder.api.AdminWorkOrederApi;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminClientUserServiceImpl extends ServiceImpl<AdminClientUserMapper, AdminClientUser> implements AdminClientUserService, BalanceAdjustmentApi {
   private static final Logger log = LoggerFactory.getLogger(AdminClientUserServiceImpl.class);
   private static final String RUNXIN_SYS_DEFAULT_PASSWORD_KEY = "RUNXIN_SYS_DEFAULT_PASSWORD";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private AdminClientRelationService clientRelationService;
   @Resource
   private AdminMemberBalanceService adminMemberBalanceService;
   @Resource
   private AdminAuthThirdService adminAuthThirdService;
   @Resource
   private PaymentsRecordApi paymentsRecordApi;
   @Resource
   private LoginAdminApi loginAdminApi;
   @Resource
   private BizUserApi bizUserApi;
   @Resource
   private AdminWorkOrederApi workOrederApi;
   @Resource
   private OrderApi orderApi;
   @Resource
   private RechargeBalanceApi rechargeBalanceApi;
   @Resource
   private GroupsService groupsService;
   @Resource
   private CommonCacheOperator commonCacheOperator;

   @Override
   public Page<ClientUserResponse> page(ClientUserPageParam clientUserPageParam) {
      QueryWrapper<AdminClientUser> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(clientUserPageParam.getMemberId())) {
         queryWrapper.lambda().and(q -> q.eq(AdminClientUser::getId, clientUserPageParam.getMemberId()));
      }

      if (StrUtil.isNotBlank(clientUserPageParam.getPhone())) {
         queryWrapper.lambda().and(q -> q.eq(AdminClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserPageParam.getPhone())));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{clientUserPageParam.getSortField(), clientUserPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(clientUserPageParam.getSortOrder());
         queryWrapper.orderBy(
            true, clientUserPageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(clientUserPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(AdminClientUser::getId);
      }

      Page<AdminClientUser> page = (Page<AdminClientUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<ClientUserResponse> responsePage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      if (CollUtil.isNotEmpty(page.getRecords())) {
         List<ClientUserResponse> responseList = page.getRecords().stream().map(user -> {
            ClientUserResponse userResponse = new ClientUserResponse();
            BeanUtils.copyProperties(user, userResponse);
            AdminMemberBalance balance = this.adminMemberBalanceService.selectByMemberNo(user.getId());
            userResponse.setExtJson(JSON.toJSONString(balance));
            if (StringUtils.isNotEmpty(user.getGroupId())) {
               ClientMemberGroups group = this.groupsService.queryById(user.getGroupId());
               if (group != null) {
                  userResponse.setGroupName(group.getGroupName());
               }
            }

            return userResponse;
         }).collect(Collectors.toList());
         responsePage.setRecords(responseList);
      }

      return responsePage;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(ClientUserAddParam clientUserAddParam) {
      this.checkParam(clientUserAddParam);
      AdminClientUser clientUser = (AdminClientUser)BeanUtil.toBean(clientUserAddParam, AdminClientUser.class);
      if (ObjectUtil.isEmpty(clientUser.getAvatar())) {
         clientUser.setAvatar(CommonAvatarUtil.generateImg(clientUser.getName()));
      }

      clientUser.setPassword(CommonCryptogramUtil.doHashValue(this.devConfigApi.getValueByKey("RUNXIN_SYS_DEFAULT_PASSWORD")));
      clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
      this.save(clientUser);
   }

   private void checkParam(ClientUserAddParam clientUserAddParam) {
      if (this.count((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getAccount, clientUserAddParam.getAccount())) > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{clientUserAddParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(clientUserAddParam.getPhone())) {
            if (!PhoneUtil.isMobile(clientUserAddParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{clientUserAddParam.getPhone()});
            }

            if (this.count((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserAddParam.getPhone())))
               > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{clientUserAddParam.getPhone()});
            }
         }

         if (ObjectUtil.isNotEmpty(clientUserAddParam.getEmail())) {
            if (!CommonEmailUtil.isEmail(clientUserAddParam.getEmail())) {
               throw new CommonException("邮箱：{}格式错误", new Object[]{clientUserAddParam.getEmail()});
            }

            if (this.count((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getEmail, clientUserAddParam.getEmail())) > 0L) {
               throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{clientUserAddParam.getEmail()});
            }
         }
      }
   }

   @Override
   public void edit(ClientUserEditParam clientUserEditParam) {
      AdminClientUser clientUser = this.queryEntity(clientUserEditParam.getId());
      this.checkParam(clientUserEditParam);
      BeanUtil.copyProperties(clientUserEditParam, clientUser, new String[0]);
      this.updateById(clientUser);
   }

   private void checkParam(ClientUserEditParam clientUserEditParam) {
      if (this.count(
            (Wrapper<AdminClientUser>)((LambdaQueryWrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getAccount, clientUserEditParam.getAccount()))
               .ne(AdminClientUser::getId, clientUserEditParam.getId())
         )
         > 0L) {
         throw new CommonException("存在重复的账号，账号为：{}", new Object[]{clientUserEditParam.getAccount()});
      } else {
         if (ObjectUtil.isNotEmpty(clientUserEditParam.getPhone())) {
            if (!PhoneUtil.isMobile(clientUserEditParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{clientUserEditParam.getPhone()});
            }

            if (this.count(
                  (Wrapper<AdminClientUser>)((LambdaQueryWrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>()
                        .eq(AdminClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(clientUserEditParam.getPhone())))
                     .ne(AdminClientUser::getId, clientUserEditParam.getId())
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
                  (Wrapper<AdminClientUser>)((LambdaQueryWrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getEmail, clientUserEditParam.getEmail()))
                     .ne(AdminClientUser::getId, clientUserEditParam.getId())
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
      this.adminAuthThirdService.delete(clientUserIdParamList);
   }

   @Override
   public AdminClientUser detail(ClientUserIdParam clientUserIdParam) {
      AdminClientUser adminClientUser = this.queryEntity(clientUserIdParam.getId());
      if (null != adminClientUser) {
         AdminMemberBalance balance = this.adminMemberBalanceService.selectByMemberNo(adminClientUser.getId());
         adminClientUser.setExtJson(JSON.toJSONString(balance));
      }

      return adminClientUser;
   }

   @Override
   public void updateUserLoginInfo(Long userId, String device) {
      AdminClientUser clientUser = this.queryEntity(userId);
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
   public AdminClientUser queryEntity(Long id) {
      AdminClientUser clientUser = (AdminClientUser)this.getById(id);
      if (ObjectUtil.isEmpty(clientUser)) {
         throw new CommonException("用户不存在，id值为：{}", new Object[]{id});
      } else {
         return clientUser;
      }
   }

   @Override
   public void updateStatus(ClientUserStatusParam clientUserStatusParam) {
      ClientUserStatusEnum.validate(clientUserStatusParam.getUserStatus());
      AdminClientUser clientUser = this.queryEntity(clientUserStatusParam.getId());
      clientUser.setUserStatus(clientUserStatusParam.getUserStatus());
      this.updateById(clientUser);
      if (ClientUserStatusEnum.DISABLED.getValue().equals(clientUserStatusParam.getUserStatus())) {
         try {
            StpClientUtil.kickout(clientUser.getId());
         } catch (Exception var4) {
            log.warn("禁用会员时踢下线失败，memberId={}", clientUser.getId(), var4);
         }
      }
   }

   @Override
   public void resetPassword(ClientResetPasswordParam clientResetPasswordParam) {
      AdminClientUser clientUser = this.queryEntity(clientResetPasswordParam.getId());
      log.info("充值密码用户信息:{}", JSON.toJSONString(clientUser));
      String newPassword = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(clientResetPasswordParam.getNewPassword()));
      String passwordConfirm = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(clientResetPasswordParam.getPasswordConfirm()));
      log.info("新密码：{}，确认密码：{}", newPassword, passwordConfirm);
      if (ObjectUtil.isEmpty(newPassword) || ObjectUtil.isEmpty(passwordConfirm)) {
         throw new CommonException("新密码或确认密码不能为空", new Object[0]);
      } else if (!newPassword.equals(passwordConfirm)) {
         throw new CommonException("新密码不一致", new Object[0]);
      } else {
         clientUser.setPassword(CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(clientResetPasswordParam.getNewPassword())));
         this.updateById(clientUser);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void adminAdjustAdd(PaymentsAdminBalancePram paymentsAdminAddBalancePram) {
      String password;
      try {
         password = CommonCryptogramUtil.doHashValue(CommonCryptogramUtil.doSm2Decrypt(paymentsAdminAddBalancePram.getAdminPassword()));
      } catch (Exception var6) {
         throw new CommonException("密码解密失败，请检查前端公钥", new Object[0]);
      }

      String adminId = this.loginAdminApi.getLoginUser().getId();
      if (!ObjectUtil.isNotEmpty(adminId)) {
         throw new CommonException("无权限", new Object[0]);
      } else if (!this.bizUserApi.verifyPassword(adminId, password)) {
         throw new CommonException("密码错误", new Object[0]);
      } else {
         AdminMemberBalance memberBalance = this.adminMemberBalanceService.selectByMemberNo(Long.valueOf(paymentsAdminAddBalancePram.getMemberNo()));
         if (!ObjectUtil.isNotEmpty(memberBalance)) {
            throw new CommonException("会员余额异常", new Object[0]);
         } else {
            if (paymentsAdminAddBalancePram.getIsAdd() == 1) {
               memberBalance.setBalance(memberBalance.getBalance().add(paymentsAdminAddBalancePram.getPayAmount()));
            } else if (paymentsAdminAddBalancePram.getIsAdd() == 0) {
               BigDecimal temp = memberBalance.getBalance().subtract(paymentsAdminAddBalancePram.getPayAmount());
               if (temp.compareTo(BigDecimal.ZERO) < 0) {
                  throw new CommonException("余额不足,无法扣减", new Object[0]);
               }

               memberBalance.setBalance(temp);
            }

            memberBalance.setUpdateTime(Date.from(Instant.now()));
            this.adminMemberBalanceService.update(memberBalance);
            this.paymentsRecordApi.adminAdjustAddRecord(paymentsAdminAddBalancePram, adminId);
         }
      }
   }

   @Override
   public ClientUserDataStatisticsParam findDatastatistics(Long userId) {
      HashMap<String, LocalDateTime> map = DateConversionFactoryUtil.conversion("全部");
      ClientUserDataStatisticsParam clientUserDatastatisticsParam = new ClientUserDataStatisticsParam();
      clientUserDatastatisticsParam.setWorkOrderNumber(this.workOrederApi.getUserOrderCount(userId));
      clientUserDatastatisticsParam.setConsumeAmount(this.orderApi.OrderPayByCreateTime(map, userId));
      clientUserDatastatisticsParam.setRechargeAmount(this.rechargeBalanceApi.getRechargeMoneyByDate(map, userId));
      return clientUserDatastatisticsParam;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateMemberGroup(UpdateMemberGroupParam updateMemberGroupParam) {
      AdminClientUser clientUser = this.queryEntity(updateMemberGroupParam.getId());
      clientUser.setGroupId(updateMemberGroupParam.getGroupId());
      this.updateById(clientUser);
   }

   @Override
   public Long countGroupNumber(String groupId) {
      QueryWrapper<AdminClientUser> queryWrapper = new QueryWrapper();
      queryWrapper.lambda().eq(AdminClientUser::getGroupId, groupId);
      return this.count(queryWrapper);
   }

   @Override
   public void updatePhone(ClientPhoneUpdateParam updatePhoneParam) {
      if (!PhoneUtil.isMobile(updatePhoneParam.getPhone())) {
         throw new CommonException("手机号码：{}格式错误", new Object[]{updatePhoneParam.getPhone()});
      } else if (this.count(
            (Wrapper<AdminClientUser>)((LambdaQueryWrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>()
                  .eq(AdminClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(updatePhoneParam.getPhone())))
               .ne(AdminClientUser::getId, updatePhoneParam.getId())
         )
         > 0L) {
         throw new CommonException("该手机号已被其他账号使用，手机号为：{}", new Object[]{updatePhoneParam.getPhone()});
      } else {
         AdminClientUser clientUser = this.queryEntity(updatePhoneParam.getId());
         clientUser.setPhone(updatePhoneParam.getPhone());
         this.updateById(clientUser);
      }
   }

   @Override
   public void updateEmail(ClientEmailUpdateParam updateEmailParam) {
      if (!CommonEmailUtil.isEmail(updateEmailParam.getEmail())) {
         throw new CommonException("邮箱：{}格式错误", new Object[]{updateEmailParam.getEmail()});
      } else if (this.count(
            (Wrapper<AdminClientUser>)((LambdaQueryWrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getEmail, updateEmailParam.getEmail()))
               .ne(AdminClientUser::getId, updateEmailParam.getId())
         )
         > 0L) {
         throw new CommonException("该邮箱已被其他账号使用，邮箱为：{}", new Object[]{updateEmailParam.getEmail()});
      } else {
         AdminClientUser clientUser = this.queryEntity(updateEmailParam.getId());
         clientUser.setEmail(updateEmailParam.getEmail());
         this.updateById(clientUser);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void addMemberByAdmin(AdminMemberAddParam addParam) {
      if (this.count((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getAccount, addParam.getAccount())) > 0L) {
         throw new CommonException("存在重复的用户名，用户名为：{}", new Object[]{addParam.getAccount()});
      } else {
         if (StrUtil.isNotBlank(addParam.getPhone())) {
            if (!PhoneUtil.isMobile(addParam.getPhone())) {
               throw new CommonException("手机号码：{}格式错误", new Object[]{addParam.getPhone()});
            }

            if (this.count((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getPhone, CommonCryptogramUtil.doSm4CbcEncrypt(addParam.getPhone()))) > 0L) {
               throw new CommonException("存在重复的手机号，手机号为：{}", new Object[]{addParam.getPhone()});
            }
         }

         if (!CommonEmailUtil.isEmail(addParam.getEmail())) {
            throw new CommonException("邮箱：{}格式错误", new Object[]{addParam.getEmail()});
         } else if (this.count((Wrapper<AdminClientUser>)new LambdaQueryWrapper<AdminClientUser>().eq(AdminClientUser::getEmail, addParam.getEmail())) > 0L) {
            throw new CommonException("存在重复的邮箱，邮箱为：{}", new Object[]{addParam.getEmail()});
         } else {
            AdminClientUser clientUser = new AdminClientUser();
            clientUser.setGroupId(StrUtil.isNotBlank(addParam.getGroupId()) ? addParam.getGroupId() : null);
            clientUser.setAccount(addParam.getAccount().trim());
            if (StrUtil.isNotBlank(addParam.getPhone())) {
               clientUser.setPhone(addParam.getPhone().trim());
            }

            clientUser.setEmail(addParam.getEmail().trim());
            String rawPassword = addParam.getPassword();
            if (StrUtil.isBlank(rawPassword)) {
               throw new CommonException("密码不能为空", new Object[0]);
            } else {
               String passwordHash;
               try {
                  String plain = CommonCryptogramUtil.doSm2Decrypt(rawPassword.trim());
                  passwordHash = CommonCryptogramUtil.doHashValue(plain);
               } catch (Exception var6) {
                  passwordHash = CommonCryptogramUtil.doHashValue(rawPassword.trim());
               }

               if (StrUtil.isBlank(passwordHash)) {
                  throw new CommonException("密码处理失败，请检查密码格式或加密方式", new Object[0]);
               } else {
                  clientUser.setPassword(passwordHash);
                  if (ObjectUtil.isEmpty(clientUser.getAvatar())) {
                     clientUser.setAvatar(CommonAvatarUtil.generateImg(clientUser.getAccount()));
                  }

                  clientUser.setUserStatus(ClientUserStatusEnum.ENABLE.getValue());
                  clientUser.setRegisterTime(new Date());
                  this.save(clientUser);
                  this.adminMemberBalanceService.initializeUserBalance(clientUser.getId());
               }
            }
         }
      }
   }

   @Override
   public String createSwitchLoginTicket(ClientSwitchLoginTicketParam param) {
      AdminClientUser clientUser = this.queryEntity(param.getMemberId());
      if (!ClientUserStatusEnum.ENABLE.getValue().equals(clientUser.getUserStatus())) {
         throw new CommonException("会员已禁用，无法切换登录", new Object[0]);
      } else {
         String ticket = RandomUtil.randomString(32);
         JSONObject ticketPayload = JSONUtil.createObj()
            .set("memberId", clientUser.getId())
            .set("adminUserId", StpUtil.getLoginIdAsString())
            .set("createdTime", System.currentTimeMillis())
            .set("purpose", "ADMIN_MEMBER_SWITCH_LOGIN");
         this.commonCacheOperator.put("admin-member-switch-login:" + ticket, ticketPayload.toString(), 60L);
         return ticket;
      }
   }
}
