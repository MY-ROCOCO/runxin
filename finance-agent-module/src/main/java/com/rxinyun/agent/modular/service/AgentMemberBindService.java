package com.rxinyun.agent.modular.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.agent.modular.entity.AgentClientUser;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.entity.AgentMemberBind;
import com.rxinyun.agent.modular.mapper.AgentClientUserMapper;
import com.rxinyun.agent.modular.mapper.AgentMemberBindMapper;
import com.rxinyun.agent.modular.param.AgentBindIdParam;
import com.rxinyun.agent.modular.param.AgentBindParam;
import com.rxinyun.agent.modular.param.AgentMemberPageParam;
import com.rxinyun.agent.modular.response.AgentMemberPageResponse;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentMemberBindService extends ServiceImpl<AgentMemberBindMapper, AgentMemberBind> {
   private final AgentInfoService agentInfoService;
   private final AgentClientUserMapper agentClientUserMapper;

   public AgentMemberBindService(AgentInfoService agentInfoService, AgentClientUserMapper agentClientUserMapper) {
      this.agentInfoService = agentInfoService;
      this.agentClientUserMapper = agentClientUserMapper;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void bindOnRegister(Long memberNo, String agentCode) {
      if (memberNo != null && !StrUtil.isBlank(agentCode)) {
         AgentInfo agentInfo = this.agentInfoService.queryEnabledByCode(agentCode.trim());
         this.bindMember(memberNo, agentInfo.getId(), "REGISTER_URL");
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void bindByAdmin(AgentBindParam bindParam) {
      this.agentInfoService.queryEntity(bindParam.getAgentId());
      this.bindMember(bindParam.getMemberNo(), bindParam.getAgentId(), "ADMIN_MANUAL");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void unbind(AgentBindIdParam bindIdParam) {
      AgentMemberBind bind = this.queryEntity(bindIdParam.getId());
      bind.setStatus("DISABLED");
      this.updateById(bind);
   }

   public Page<AgentMemberPageResponse> page(AgentMemberPageParam pageParam) {
      QueryWrapper<AgentMemberBind> wrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getAgentId())) {
         wrapper.lambda().eq(AgentMemberBind::getAgentId, pageParam.getAgentId());
      }

      if (StrUtil.isNotBlank(pageParam.getAccount())) {
         List<Long> matchedMemberNos = this.agentClientUserMapper
            .selectList((Wrapper<AgentClientUser>)new LambdaQueryWrapper<AgentClientUser>().like(AgentClientUser::getAccount, pageParam.getAccount().trim()))
            .stream()
            .map(AgentClientUser::getId)
            .toList();
         if (CollUtil.isEmpty(matchedMemberNos)) {
            Page<AgentMemberPageResponse> emptyPage = new Page();
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0L);
            return emptyPage;
         }

         wrapper.lambda().in(AgentMemberBind::getMemberNo, matchedMemberNos);
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         wrapper.lambda().eq(AgentMemberBind::getMemberNo, pageParam.getMemberNo());
      }

      ((LambdaQueryWrapper<AgentMemberBind>)wrapper.lambda().eq(AgentMemberBind::getStatus, "ENABLE")).orderByDesc(AgentMemberBind::getBindTime);
      Page<AgentMemberBind> bindPage = (Page<AgentMemberBind>)this.page(CommonPageRequest.defaultPage(), wrapper);
      Page<AgentMemberPageResponse> resultPage = new Page(bindPage.getCurrent(), bindPage.getSize(), bindPage.getTotal());
      if (CollUtil.isEmpty(bindPage.getRecords())) {
         resultPage.setRecords(Collections.emptyList());
         return resultPage;
      } else {
         Map<Long, AgentClientUser> userMap = this.loadUserMap(bindPage.getRecords().stream().map(AgentMemberBind::getMemberNo).distinct().toList());
         Map<String, AgentInfo> agentMap = this.loadAgentMap(bindPage.getRecords().stream().map(AgentMemberBind::getAgentId).distinct().toList());
         List<AgentMemberPageResponse> records = bindPage.getRecords().stream().map(bind -> {
            AgentMemberPageResponse response = (AgentMemberPageResponse)BeanUtil.copyProperties(bind, AgentMemberPageResponse.class, new String[0]);
            response.setBindId(bind.getId());
            AgentInfo agentInfo = agentMap.get(bind.getAgentId());
            if (agentInfo != null) {
               response.setAgentCode(agentInfo.getAgentCode());
               response.setAgentName(agentInfo.getAgentName());
            }

            AgentClientUser user = userMap.get(bind.getMemberNo());
            if (user != null) {
               response.setAccount(user.getAccount());
               response.setName(user.getName());
               response.setPhone(user.getPhone());
               response.setEmail(user.getEmail());
               response.setUserStatus(user.getUserStatus());
               response.setRegisterTime(user.getRegisterTime());
            }

            return response;
         }).toList();
         resultPage.setRecords(records);
         return resultPage;
      }
   }

   public AgentMemberBind getEnabledBindByMemberNo(Long memberNo) {
      return (AgentMemberBind)this.getOne(
         (Wrapper<AgentMemberBind>)((LambdaQueryWrapper<AgentMemberBind>)new LambdaQueryWrapper<AgentMemberBind>().eq(AgentMemberBind::getMemberNo, memberNo)).eq(AgentMemberBind::getStatus, "ENABLE"), false
      );
   }

   public List<Long> listEnabledMemberNosByAgentId(String agentId) {
      return this.list(
            (Wrapper<AgentMemberBind>)((LambdaQueryWrapper<AgentMemberBind>)new LambdaQueryWrapper<AgentMemberBind>().eq(AgentMemberBind::getAgentId, agentId)).eq(AgentMemberBind::getStatus, "ENABLE")
         )
         .stream()
         .map(AgentMemberBind::getMemberNo)
         .distinct()
         .toList();
   }

   public Map<Long, AgentClientUser> loadUserMap(List<Long> memberNos) {
      return CollUtil.isEmpty(memberNos)
         ? Collections.emptyMap()
         : this.agentClientUserMapper.selectBatchIds(memberNos).stream().collect(Collectors.toMap(AgentClientUser::getId, Function.identity(), (a, b) -> a));
   }

   private Map<String, AgentInfo> loadAgentMap(List<String> agentIds) {
      return CollUtil.isEmpty(agentIds)
         ? Collections.emptyMap()
         : this.agentInfoService.listByIds(agentIds).stream().collect(Collectors.toMap(AgentInfo::getId, Function.identity(), (a, b) -> a));
   }

   public AgentMemberBind queryEntity(String id) {
      AgentMemberBind bind = (AgentMemberBind)this.getById(id);
      if (bind == null) {
         throw new CommonException("绑定记录不存在，id值为：{}", new Object[]{id});
      } else {
         return bind;
      }
   }

   private void bindMember(Long memberNo, String agentId, String bindSource) {
      AgentClientUser clientUser = (AgentClientUser)this.agentClientUserMapper.selectById(memberNo);
      if (clientUser == null) {
         throw new CommonException("会员不存在，memberNo值为：{}", new Object[]{memberNo});
      } else {
         AgentMemberBind existBind = this.getEnabledBindByMemberNo(memberNo);
         if (existBind != null) {
            if (StrUtil.equals(existBind.getAgentId(), agentId)) {
               return;
            }

            existBind.setStatus("DISABLED");
            this.updateById(existBind);
         }

         AgentMemberBind bind = new AgentMemberBind();
         bind.setId(IdWorker.getIdStr());
         bind.setMemberNo(memberNo);
         bind.setAgentId(agentId);
         bind.setBindSource(bindSource);
         bind.setBindTime(new Date());
         bind.setStatus("ENABLE");
         this.save(bind);
      }
   }
}
