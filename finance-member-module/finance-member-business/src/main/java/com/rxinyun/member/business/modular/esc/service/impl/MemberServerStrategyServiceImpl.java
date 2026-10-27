package com.rxinyun.member.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.controller.CloudControllerRouter;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.qzsystem.entity.HostStrategy;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerStrategy;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerStrategyMapper;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerStrategyPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerService;
import com.rxinyun.member.business.modular.esc.service.MemberServerStrategyService;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
public class MemberServerStrategyServiceImpl extends ServiceImpl<MemberServerStrategyMapper, MemberServerStrategy> implements MemberServerStrategyService {
   private static final Logger log = LoggerFactory.getLogger(MemberServerStrategyServiceImpl.class);
   @Resource
   private MemberServerService memberServerService;
   @Resource
   private MemberMasterApi memberMasterApi;
   @Resource
   private CloudControllerRouter cloudControllerRouter;
   @Resource
   private MemberServerStrategyMapper memberServerStrategyMapper;

   @Override
   public Page<MemberServerStrategy> page(MemberServerStrategyPageParam pageParam) {
      if (StrUtil.isBlank(pageParam.getServerId())) {
         throw new CommonException("请选择服务器", new Object[0]);
      } else {
         this.memberServerService.queryEntity(pageParam.getServerId());
         QueryWrapper<MemberServerStrategy> queryWrapper = new QueryWrapper().checkSqlInjection();
         queryWrapper.lambda().eq(MemberServerStrategy::getServerId, pageParam.getServerId());
         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<MemberServerStrategy>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult add(MemberServerStrategyAddParam serverStrategyAddParam) {
      try {
         MemberServer server = this.memberServerService.queryEntity(serverStrategyAddParam.getServerId());
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
            if (null == memberMaster) {
               return CommonResult.error("宿主机不存在");
            } else {
               MemberServerStrategy serverStrategy = (MemberServerStrategy)BeanUtil.toBean(serverStrategyAddParam, MemberServerStrategy.class);
               serverStrategy.setWeight(this.resolveFirewallWeight(server.getId(), serverStrategy.getDirection(), null, null));
               this.save(serverStrategy);
               log.info("请求新增策略：server:", JSON.toJSONString(server));
               HostStrategy hostStrategy = new HostStrategy();
               BeanUtils.copyProperties(server, hostStrategy);
               hostStrategy.setHostName(server.getHostName());
               hostStrategy.setStrategyName(serverStrategy.getId());
               hostStrategy.setServerId(server.getId());
               hostStrategy.setMasterIp(memberMaster.getIp());
               hostStrategy.setApiKey(memberMaster.getApiPassword());
               hostStrategy.setApiPort(memberMaster.getApiPort());
               hostStrategy.setAction(serverStrategyAddParam.getAction());
               hostStrategy.setDirection(serverStrategyAddParam.getDirection());
               hostStrategy.setProtocol(serverStrategyAddParam.getProtocol());
               hostStrategy.setWeight(serverStrategy.getWeight());
               hostStrategy.setPort(serverStrategyAddParam.getPort());
               hostStrategy.setIp(serverStrategyAddParam.getIp());
               log.info("请求新增策略：hostStrategy:", JSON.toJSONString(hostStrategy));
               CommonResult result = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).addFirewallHost(hostStrategy);
               if (result != null && result.getCode() != null && Integer.valueOf(200).equals(result.getCode())) {
                  log.info("新增策略结束：hostStrategy:", JSON.toJSONString(result));
                  return result;
               } else {
                  throw new CommonException("新增防火墙策略失败：{}", new Object[]{result != null ? result.getMsg() : "受控无响应"});
               }
            }
         }
      } catch (Exception var7) {
         TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
         log.error("启动服务器失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(MemberServerStrategyEditParam serverStrategyEditParam) {
      MemberServerStrategy serverStrategy = this.queryEntity(serverStrategyEditParam.getId());
      Integer oldWeight = serverStrategy.getWeight();
      BeanUtil.copyProperties(serverStrategyEditParam, serverStrategy, new String[0]);
      serverStrategy.setWeight(this.resolveFirewallWeight(serverStrategy.getServerId(), serverStrategy.getDirection(), serverStrategy.getId(), oldWeight));
      MemberServer server = this.memberServerService.queryEntity(serverStrategy.getServerId());
      if (null == server) {
         throw new CommonException("服务器不存在", new Object[0]);
      } else {
         BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
         if (null == memberMaster) {
            throw new CommonException("宿主机不存在", new Object[0]);
         } else {
            HostStrategy hostStrategy = new HostStrategy();
            BeanUtils.copyProperties(server, hostStrategy);
            hostStrategy.setHostName(server.getHostName());
            hostStrategy.setStrategyName(serverStrategy.getId());
            hostStrategy.setServerId(server.getId());
            hostStrategy.setMasterIp(memberMaster.getIp());
            hostStrategy.setApiKey(memberMaster.getApiPassword());
            hostStrategy.setApiPort(memberMaster.getApiPort());
            hostStrategy.setAction(serverStrategy.getAction());
            hostStrategy.setDirection(serverStrategy.getDirection());
            hostStrategy.setProtocol(serverStrategy.getProtocol());
            hostStrategy.setWeight(serverStrategy.getWeight());
            hostStrategy.setOldWeight(oldWeight);
            hostStrategy.setPort(serverStrategy.getPort());
            hostStrategy.setIp(serverStrategy.getIp());
            CommonResult result = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).updateFirewallHost(hostStrategy);
            if (result != null && result.getCode() != null && Integer.valueOf(200).equals(result.getCode())) {
               this.updateById(serverStrategy);
            } else {
               throw new CommonException("修改防火墙策略失败：{}", new Object[]{result != null ? result.getMsg() : "受控无响应"});
            }
         }
      }
   }

   private Integer resolveFirewallWeight(String serverId, String direction, String excludeId, Integer currentWeight) {
      String normalizedDirection = this.normalizeFirewallDirection(direction);
      if (currentWeight != null
         && this.isFirewallWeightInRange(currentWeight, normalizedDirection)
         && !this.isFirewallWeightUsed(serverId, currentWeight, excludeId)) {
         return currentWeight;
      } else {
         Set<Integer> usedWeights = new HashSet<>();

         for (MemberServerStrategy strategy : this.queryByServerId(serverId)) {
            if (strategy != null && (excludeId == null || !excludeId.equals(strategy.getId())) && strategy.getWeight() != null) {
               usedWeights.add(strategy.getWeight());
            }
         }

         int weight = this.firewallWeightStart(normalizedDirection);

         while (usedWeights.contains(weight)) {
            weight++;
         }

         return weight;
      }
   }

   private boolean isFirewallWeightUsed(String serverId, Integer weight, String excludeId) {
      for (MemberServerStrategy strategy : this.queryByServerId(serverId)) {
         if (strategy != null && (excludeId == null || !excludeId.equals(strategy.getId())) && weight.equals(strategy.getWeight())) {
            return true;
         }
      }

      return false;
   }

   private boolean isFirewallWeightInRange(Integer weight, String normalizedDirection) {
      return weight != null && weight >= this.firewallWeightStart(normalizedDirection);
   }

   private int firewallWeightStart(String normalizedDirection) {
      return "Outbound".equals(normalizedDirection) ? 103 : 4;
   }

   private String normalizeFirewallDirection(String direction) {
      if (StrUtil.isBlank(direction)) {
         throw new CommonException("防火墙方向无效", new Object[0]);
      } else {
         String value = direction.trim();
         if ("1".equals(value) || "in".equalsIgnoreCase(value) || "inbound".equalsIgnoreCase(value)) {
            return "Inbound";
         } else if (!"2".equals(value) && !"out".equalsIgnoreCase(value) && !"outbound".equalsIgnoreCase(value)) {
            throw new CommonException("防火墙方向无效", new Object[0]);
         } else {
            return "Outbound";
         }
      }
   }

   @Override
   public MemberServerStrategy detail(MemberServerStrategyIdParam serverStrategyIdParam) {
      MemberServerStrategy serverStrategy = this.queryEntity(serverStrategyIdParam.getId());
      this.memberServerService.queryEntity(serverStrategy.getServerId());
      return serverStrategy;
   }

   @Override
   public MemberServerStrategy queryEntity(String id) {
      MemberServerStrategy serverStrategy = (MemberServerStrategy)this.getById(id);
      if (ObjectUtil.isEmpty(serverStrategy)) {
         throw new CommonException("服务器策略不存在，id值为：{}", new Object[]{id});
      } else {
         return serverStrategy;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult delete(MemberServerStrategyIdParam serverStrategyIdParam) {
      try {
         MemberServerStrategy serverStrategy = this.queryEntity(serverStrategyIdParam.getId());
         if (null == serverStrategy) {
            return CommonResult.error("服务器策略不存在");
         } else {
            MemberServer server = this.memberServerService.queryEntity(serverStrategy.getServerId());
            if (null == server) {
               return CommonResult.error("服务器不存在");
            } else {
               BaseMemberMaster memberMaster = this.memberMasterApi.selectByMasterId(server.getMasterId());
               if (null == memberMaster) {
                  return CommonResult.error("宿主机不存在");
               } else {
                  this.memberServerStrategyMapper.deleteById(serverStrategy.getId());
                  HostStrategy hostStrategy = new HostStrategy();
                  BeanUtils.copyProperties(server, hostStrategy);
                  hostStrategy.setHostName(server.getHostName());
                  hostStrategy.setStrategyName(serverStrategy.getId());
                  hostStrategy.setServerId(server.getId());
                  hostStrategy.setMasterIp(memberMaster.getIp());
                  hostStrategy.setApiKey(memberMaster.getApiPassword());
                  hostStrategy.setApiPort(memberMaster.getApiPort());
                  hostStrategy.setStrategyName(serverStrategy.getId());
                  hostStrategy.setWeight(serverStrategy.getWeight());
                  CommonResult result = this.cloudControllerRouter.getAdapterByGuardVersion(memberMaster.getGuardVersion()).removeFirewallHost(hostStrategy);
                  if (result != null && result.getCode() != null && Integer.valueOf(200).equals(result.getCode())) {
                     return result;
                  } else {
                     throw new CommonException("删除防火墙策略失败：{}", new Object[]{result != null ? result.getMsg() : "受控无响应"});
                  }
               }
            }
         }
      } catch (Exception var7) {
         TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
         log.error("删除服务器策略失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public List<MemberServerStrategy> queryByServerId(String serverId) {
      LambdaQueryWrapper<MemberServerStrategy> queryWrapper = (LambdaQueryWrapper<MemberServerStrategy>)new LambdaQueryWrapper<MemberServerStrategy>()
         .eq(MemberServerStrategy::getServerId, serverId);
      return this.list(queryWrapper);
   }
}
