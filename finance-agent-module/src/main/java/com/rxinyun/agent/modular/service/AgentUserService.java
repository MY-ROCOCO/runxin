package com.rxinyun.agent.modular.service;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.agent.core.util.StpAgentLoginUserUtil;
import com.rxinyun.agent.core.util.StpAgentUtil;
import com.rxinyun.agent.modular.auth.result.AgentLoginUser;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.entity.AgentUser;
import com.rxinyun.agent.modular.mapper.AgentUserMapper;
import com.rxinyun.agent.modular.param.AgentLoginParam;
import com.rxinyun.agent.modular.param.AgentUpdatePasswordParam;
import com.rxinyun.agent.modular.param.AgentUserAddParam;
import com.rxinyun.agent.modular.param.AgentUserPageParam;
import com.rxinyun.agent.modular.param.AgentUserResetPasswordParam;
import com.rxinyun.agent.modular.param.AgentUserStatusParam;
import com.rxinyun.agent.modular.response.AgentUserPageResponse;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.CommonCryptogramUtil;
import com.rxinyun.common.util.CommonIpAddressUtil;
import com.rxinyun.common.util.CommonServletUtil;
import com.rxinyun.common.util.CommonUaUtil;
import com.rxinyun.common.util.TimeUtil;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AgentUserService extends ServiceImpl<AgentUserMapper, AgentUser> {
   private static final int LOGIN_DEVICE_MAX_LENGTH = 64;
   private final AgentInfoService agentInfoService;

   public AgentUserService(AgentInfoService agentInfoService) {
      this.agentInfoService = agentInfoService;
   }

   public void add(AgentUserAddParam addParam) {
      this.agentInfoService.queryEntity(addParam.getAgentId());
      this.ensureUniqueLoginAccount(addParam.getLoginAccount(), null);
      AgentUser agentUser = new AgentUser();
      agentUser.setId(IdWorker.getIdStr());
      agentUser.setAgentId(addParam.getAgentId());
      agentUser.setLoginAccount(addParam.getLoginAccount().trim());
      agentUser.setPassword(this.hashPassword(addParam.getPassword()));
      agentUser.setUserStatus("ENABLE");
      this.save(agentUser);
   }

   public void resetPassword(AgentUserResetPasswordParam resetPasswordParam) {
      AgentUser agentUser = this.queryEntity(resetPasswordParam.getId());
      agentUser.setPassword(this.hashPassword(resetPasswordParam.getNewPassword()));
      this.updateById(agentUser);
   }

   public void updateStatus(AgentUserStatusParam statusParam) {
      AgentUser agentUser = this.queryEntity(statusParam.getId());
      agentUser.setUserStatus(statusParam.getStatus().trim());
      this.updateById(agentUser);
   }

   public Page<AgentUserPageResponse> page(AgentUserPageParam pageParam) {
      QueryWrapper<AgentUser> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getAgentId())) {
         queryWrapper.lambda().eq(AgentUser::getAgentId, pageParam.getAgentId().trim());
      }

      if (StrUtil.isNotBlank(pageParam.getLoginAccount())) {
         queryWrapper.lambda().like(AgentUser::getLoginAccount, pageParam.getLoginAccount().trim());
      }

      if (StrUtil.isNotBlank(pageParam.getUserStatus())) {
         queryWrapper.lambda().eq(AgentUser::getUserStatus, pageParam.getUserStatus().trim());
      }

      queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      Page<AgentUser> userPage = (Page<AgentUser>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<AgentUserPageResponse> resultPage = new Page(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
      if (userPage.getRecords().isEmpty()) {
         resultPage.setRecords(Collections.emptyList());
         return resultPage;
      } else {
         Map<String, AgentInfo> agentInfoMap = this.agentInfoService
            .listByIds(userPage.getRecords().stream().map(AgentUser::getAgentId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(AgentInfo::getId, Function.identity(), (a, b) -> a));
         List<AgentUserPageResponse> records = userPage.getRecords().stream().map(agentUser -> {
            AgentUserPageResponse response = (AgentUserPageResponse)BeanUtil.copyProperties(agentUser, AgentUserPageResponse.class, new String[0]);
            AgentInfo agentInfo = agentInfoMap.get(agentUser.getAgentId());
            if (agentInfo != null) {
               response.setAgentCode(agentInfo.getAgentCode());
               response.setAgentName(agentInfo.getAgentName());
            }

            return response;
         }).toList();
         resultPage.setRecords(records);
         return resultPage;
      }
   }

   public String doLogin(AgentLoginParam loginParam) {
      AgentUser agentUser = (AgentUser)this.getOne((Wrapper<AgentUser>)new LambdaQueryWrapper<AgentUser>().eq(AgentUser::getLoginAccount, loginParam.getAccount().trim()), false);
      if (agentUser == null) {
         throw new CommonException("账号不存在", new Object[0]);
      } else {
         AgentInfo agentInfo = this.agentInfoService.queryEntity(agentUser.getAgentId());
         if (!"ENABLE".equals(agentUser.getUserStatus())) {
            throw new CommonException("代理账号已停用", new Object[0]);
         } else if (!"ENABLE".equals(agentInfo.getStatus())) {
            throw new CommonException("代理已停用", new Object[0]);
         } else {
            String passwordHash = this.hashPassword(loginParam.getPassword());
            if (!StrUtil.equals(agentUser.getPassword(), passwordHash)) {
               throw new CommonException("密码错误", new Object[0]);
            } else {
               String device = this.normalizeLoginDevice(loginParam.getDevice());
               AgentLoginUser loginUser = this.buildLoginUser(agentUser, agentInfo);
               StpAgentUtil.login(agentUser.getId(), new SaLoginModel().setDevice(device).setExtra("account", agentUser.getLoginAccount()));
               StpAgentLoginUserUtil.updateLoginUser(loginUser);
               this.updateLoginInfo(agentUser.getId(), device);
               StpAgentLoginUserUtil.updateLoginUser(this.buildLoginUser((AgentUser)this.getById(agentUser.getId()), agentInfo));
               return StpAgentUtil.getTokenInfo().tokenValue;
            }
         }
      }
   }

   public AgentLoginUser getLoginUser() {
      AgentLoginUser loginUser = StpAgentLoginUserUtil.getLoginUser();
      if (loginUser == null) {
         throw new CommonException("代理未登录", new Object[0]);
      } else {
         return loginUser;
      }
   }

   public void updatePassword(AgentUpdatePasswordParam updatePasswordParam) {
      AgentLoginUser loginUser = this.getLoginUser();
      AgentUser agentUser = this.queryEntity(loginUser.getId());
      String oldPassword = this.hashPassword(updatePasswordParam.getOldPassword());
      if (!StrUtil.equals(agentUser.getPassword(), oldPassword)) {
         throw new CommonException("旧密码错误", new Object[0]);
      } else {
         String newPassword = this.hashPassword(updatePasswordParam.getNewPassword());
         String confirmPassword = this.hashPassword(updatePasswordParam.getConfirmPassword());
         if (!StrUtil.equals(newPassword, confirmPassword)) {
            throw new CommonException("两次输入的新密码不一致", new Object[0]);
         } else {
            agentUser.setPassword(newPassword);
            this.updateById(agentUser);
         }
      }
   }

   public AgentUser queryEntity(String id) {
      AgentUser agentUser = (AgentUser)this.getById(id);
      if (agentUser == null) {
         throw new CommonException("代理账号不存在，id值为：{}", new Object[]{id});
      } else {
         return agentUser;
      }
   }

   public void updateLoginInfo(String userId, String device) {
      AgentUser agentUser = this.queryEntity(userId);
      agentUser.setLastLoginTime(agentUser.getLatestLoginTime());
      agentUser.setLastLoginIp(agentUser.getLatestLoginIp());
      agentUser.setLastLoginAddress(agentUser.getLatestLoginAddress());
      agentUser.setLastLoginDevice(agentUser.getLatestLoginDevice());
      agentUser.setLatestLoginTime(TimeUtil.toDate(TimeUtil.now()));
      String ip = CommonIpAddressUtil.getIp(CommonServletUtil.getRequest());
      agentUser.setLatestLoginIp(ip);
      agentUser.setLatestLoginAddress(CommonIpAddressUtil.getCityInfo(ip));
      agentUser.setLatestLoginDevice(device);
      this.updateById(agentUser);
   }

   public AgentLoginUser buildLoginUser(AgentUser agentUser, AgentInfo agentInfo) {
      AgentLoginUser loginUser = new AgentLoginUser();
      loginUser.setId(agentUser.getId());
      loginUser.setAgentId(agentInfo.getId());
      loginUser.setAgentCode(agentInfo.getAgentCode());
      loginUser.setAgentName(agentInfo.getAgentName());
      loginUser.setAccount(agentUser.getLoginAccount());
      loginUser.setUserStatus(agentUser.getUserStatus());
      loginUser.setAgentStatus(agentInfo.getStatus());
      loginUser.setLatestLoginTime(agentUser.getLatestLoginTime());
      return loginUser;
   }

   private void ensureUniqueLoginAccount(String loginAccount, String excludeId) {
      LambdaQueryWrapper<AgentUser> wrapper = (LambdaQueryWrapper<AgentUser>)new LambdaQueryWrapper<AgentUser>().eq(AgentUser::getLoginAccount, loginAccount.trim());
      if (StrUtil.isNotBlank(excludeId)) {
         wrapper.ne(AgentUser::getId, excludeId);
      }

      if (this.count(wrapper) > 0L) {
         throw new CommonException("存在重复的代理账号，账号为：{}", new Object[]{loginAccount});
      }
   }

   private String hashPassword(String rawPassword) {
      if (StrUtil.isBlank(rawPassword)) {
         throw new CommonException("密码不能为空", new Object[0]);
      } else {
         try {
            String plain = CommonCryptogramUtil.doSm2Decrypt(rawPassword.trim());
            return CommonCryptogramUtil.doHashValue(plain);
         } catch (Exception var3) {
            return CommonCryptogramUtil.doHashValue(rawPassword.trim());
         }
      }
   }

   private String normalizeLoginDevice(String device) {
      String normalizedDevice = StrUtil.blankToDefault(StrUtil.trim(device), "PC");
      if (normalizedDevice.length() <= 64) {
         return normalizedDevice;
      } else {
         String browser = CommonUaUtil.getBrowser(CommonServletUtil.getRequest());
         return StrUtil.isNotBlank(browser) && browser.length() <= 64 ? browser : normalizedDevice.substring(0, 64);
      }
   }
}
