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
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberNatNetwork;
import com.rxinyun.member.production.modular.ecs.ip.mapper.MemberNatNetworkMapper;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberNatNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberNatNetworkPageParam;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberNatNetworkService;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberNatNetworkServiceImpl extends ServiceImpl<MemberNatNetworkMapper, MemberNatNetwork> implements MemberNatNetworkService {
   private static final Logger log = LoggerFactory.getLogger(MemberNatNetworkServiceImpl.class);
   @Resource
   private MemberNatNetworkMapper memberNatNetworkMapper;
   @Resource
   private CommonCacheOperator commonCacheOperator;

   @Override
   public Page<MemberNatNetwork> page(MemberNatNetworkPageParam pageParam) {
      QueryWrapper<MemberNatNetwork> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberNatNetwork>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberNatNetwork detail(MemberNatNetworkIdParam memberNatNetworkIdParam) {
      return this.queryEntity(memberNatNetworkIdParam.getId());
   }

   @Override
   public MemberNatNetwork queryEntity(String id) {
      MemberNatNetwork serverIp = (MemberNatNetwork)this.getById(id);
      if (ObjectUtil.isEmpty(serverIp)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverIp;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public MemberNatNetwork selectByMaster(String masterId) {
      if (StrUtil.isBlank(masterId)) {
         throw new CommonException("宿主机ID不能为空", new Object[0]);
      } else {
         int maxRetries = 3;

         for (int i = 0; i < maxRetries; i++) {
            try {
               MemberNatNetwork ip = this.memberNatNetworkMapper.selectAvailableNatNetwork(masterId);
               if (ip == null) {
                  throw new CommonException("IP资源不足", new Object[0]);
               }

               boolean updated = this.update(
                  (Wrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)new LambdaUpdateWrapper<MemberNatNetwork>().eq(MemberNatNetwork::getId, ip.getId()))
                        .eq(MemberNatNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode()))
                     .set(MemberNatNetwork::getEnable, NetworkStatusEnum.OCCUPIED.getCode())
               );
               if (updated) {
                  ip.setEnable(NetworkStatusEnum.OCCUPIED.getCode());
                  return ip;
               }

               log.warn("NAT IP已被占用，正在进行第{}次重试", i + 1);
               Thread.sleep(50L);
            } catch (Exception var7) {
               if (i == maxRetries - 1) {
                  throw new CommonException("分配NAT IP失败: " + var7.getMessage(), new Object[0]);
               }

               log.warn("分配NAT IP时发生异常，正在进行第{}次重试: {}", i + 1, var7.getMessage());

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var6) {
                  Thread.currentThread().interrupt();
                  throw new CommonException("分配NAT IP被中断", new Object[0]);
               }
            }
         }

         throw new CommonException("分配NAT IP失败，已达到最大重试次数", new Object[0]);
      }
   }

   @Override
   public void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion) {
      boolean rows = this.update(
         (Wrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)new LambdaUpdateWrapper<MemberNatNetwork>().eq(MemberNatNetwork::getId, id))
                  .eq(MemberNatNetwork::getVersion, currentVersion))
               .set(MemberNatNetwork::getEnable, newStatus))
            .setSql("version = version + 1", new Object[0])
      );
      if (!rows) {
         throw new CommonException("IP已被占用，请重试", new Object[0]);
      }
   }

   @Override
   public void updateStatus(String id, int code) {
      MemberNatNetwork memberNatNetwork = this.queryEntity(id);
      memberNatNetwork.setEnable(code);
      this.updateById(memberNatNetwork);
   }

   @Override
   public void updateStatus(String ip) {
      this.update(
         (Wrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)new LambdaUpdateWrapper<MemberNatNetwork>().eq(MemberNatNetwork::getIp, ip))
            .set(MemberNatNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode())
      );
   }

   @Override
   public void updateStatus(List<String> ipList) {
      this.update(
         (Wrapper<MemberNatNetwork>)((LambdaUpdateWrapper<MemberNatNetwork>)new LambdaUpdateWrapper<MemberNatNetwork>().in(MemberNatNetwork::getIp, ipList))
            .set(MemberNatNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void rollbackStatus(String id) {
      MemberNatNetwork network = this.queryEntity(id);
      network.setEnable(NetworkStatusEnum.UNOCCUPIED.getCode());
      this.updateById(network);
   }
}
