package com.rxinyun.member.production.modular.ecs.ip.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.cache.CommonCacheOperator;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.NetworkStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPrivateNetwork;
import com.rxinyun.member.production.modular.ecs.ip.mapper.MemberPrivateNetworkMapper;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPrivateNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPrivateNetworkPageParam;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberPrivateNetworkService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberPrivateNetworkServiceImpl extends ServiceImpl<MemberPrivateNetworkMapper, MemberPrivateNetwork> implements MemberPrivateNetworkService {
   private static final Logger log = LoggerFactory.getLogger(MemberPrivateNetworkServiceImpl.class);
   @Resource
   private MemberPrivateNetworkMapper memberPrivateNetworkMapper;
   @Resource
   private CommonCacheOperator commonCacheOperator;

   @Override
   public Page<MemberPrivateNetwork> page(MemberPrivateNetworkPageParam pageParam) {
      QueryWrapper<MemberPrivateNetwork> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberPrivateNetwork>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberPrivateNetwork detail(MemberPrivateNetworkIdParam PrivateNetworkIdParam) {
      return this.queryEntity(PrivateNetworkIdParam.getId());
   }

   @Override
   public MemberPrivateNetwork queryEntity(String id) {
      MemberPrivateNetwork PrivateNetwork = (MemberPrivateNetwork)this.getById(id);
      if (ObjectUtil.isEmpty(PrivateNetwork)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return PrivateNetwork;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public MemberPrivateNetwork selectByMaster(String masterId) {
      if (StrUtil.isBlank(masterId)) {
         throw new CommonException("宿主机ID不能为空", new Object[0]);
      } else {
         int maxRetries = 3;

         for (int i = 0; i < maxRetries; i++) {
            try {
               MemberPrivateNetwork ip = this.memberPrivateNetworkMapper.selectAvailablePrivateNetwork(masterId);
               if (ip == null) {
                  throw new CommonException("IP资源不足", new Object[0]);
               }

               boolean updated = this.update(
                  (Wrapper<MemberPrivateNetwork>)((LambdaUpdateWrapper<MemberPrivateNetwork>)((LambdaUpdateWrapper<MemberPrivateNetwork>)new LambdaUpdateWrapper<MemberPrivateNetwork>().eq(MemberPrivateNetwork::getId, ip.getId()))
                        .eq(MemberPrivateNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode()))
                     .set(MemberPrivateNetwork::getEnable, NetworkStatusEnum.OCCUPIED.getCode())
               );
               if (updated) {
                  ip.setEnable(NetworkStatusEnum.OCCUPIED.getCode());
                  return ip;
               }

               log.warn("私网IP已被占用，正在进行第{}次重试", i + 1);
               Thread.sleep(50L);
            } catch (Exception var7) {
               if (i == maxRetries - 1) {
                  throw new CommonException("分配私网IP失败: " + var7.getMessage(), new Object[0]);
               }

               log.warn("分配私网IP时发生异常，正在进行第{}次重试: {}", i + 1, var7.getMessage());

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var6) {
                  Thread.currentThread().interrupt();
                  throw new CommonException("分配私网IP被中断", new Object[0]);
               }
            }
         }

         throw new CommonException("分配私网IP失败，已达到最大重试次数", new Object[0]);
      }
   }

   @Override
   public void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion) {
      boolean rows = this.update(
         (Wrapper<MemberPrivateNetwork>)((LambdaUpdateWrapper<MemberPrivateNetwork>)((LambdaUpdateWrapper<MemberPrivateNetwork>)((LambdaUpdateWrapper<MemberPrivateNetwork>)new LambdaUpdateWrapper<MemberPrivateNetwork>().eq(MemberPrivateNetwork::getId, id))
                  .eq(MemberPrivateNetwork::getVersion, currentVersion))
               .set(MemberPrivateNetwork::getEnable, newStatus))
            .setSql("version = version + 1", new Object[0])
      );
      if (!rows) {
         throw new CommonException("IP已被占用，请重试", new Object[0]);
      }
   }

   @Override
   public void updateStatus(String id, int code) {
      MemberPrivateNetwork memberPrivateNetwork = this.queryEntity(id);
      memberPrivateNetwork.setEnable(code);
      this.updateById(memberPrivateNetwork);
   }

   @Override
   public void updateStatus(String ip) {
      this.update(
         (Wrapper<MemberPrivateNetwork>)((LambdaUpdateWrapper<MemberPrivateNetwork>)new LambdaUpdateWrapper<MemberPrivateNetwork>().eq(MemberPrivateNetwork::getIp, ip))
            .set(MemberPrivateNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void rollbackStatus(String id) {
      MemberPrivateNetwork network = this.queryEntity(id);
      network.setEnable(NetworkStatusEnum.UNOCCUPIED.getCode());
      this.updateById(network);
   }
}
