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
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPublicNetwork;
import com.rxinyun.member.production.modular.ecs.ip.mapper.MemberPublicNetworkMapper;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPublicNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPublicNetworkPageParam;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberPublicNetworkService;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLocalLineService;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberPublicNetworkServiceImpl extends ServiceImpl<MemberPublicNetworkMapper, MemberPublicNetwork> implements MemberPublicNetworkService {
   private static final Logger log = LoggerFactory.getLogger(MemberPublicNetworkServiceImpl.class);
   @Resource
   private MemberPublicNetworkMapper memberPublicNetworkMapper;
   @Resource
   private CommonCacheOperator commonCacheOperator;
   @Resource
   private MemberLocalLineService memberLocalLineService;

   @Override
   public Page<MemberPublicNetwork> page(MemberPublicNetworkPageParam pageParam) {
      QueryWrapper<MemberPublicNetwork> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberPublicNetwork>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberPublicNetwork detail(MemberPublicNetworkIdParam serverIpIdParam) {
      return this.queryEntity(serverIpIdParam.getId());
   }

   @Override
   public MemberPublicNetwork queryEntity(String id) {
      MemberPublicNetwork serverIp = (MemberPublicNetwork)this.getById(id);
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
   public MemberPublicNetwork selectByMaster(String masterId, String lineId) {
      if (StrUtil.isBlank(masterId)) {
         throw new CommonException("宿主机ID不能为空", new Object[0]);
      } else {
         Integer ipRand = this.resolveIpRand(lineId);
         log.info("分配公网IP，masterId={}，lineId={}，ipRand={}", new Object[]{masterId, lineId, ipRand});
         int maxRetries = 3;

         for (int i = 0; i < maxRetries; i++) {
            try {
               MemberPublicNetwork ip = this.memberPublicNetworkMapper.selectAvailablePublicNetwork(masterId, ipRand);
               if (ip == null) {
                  throw new CommonException("没有可用的IP", new Object[0]);
               }

               boolean updated = this.update(
                  (Wrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)new LambdaUpdateWrapper<MemberPublicNetwork>().eq(MemberPublicNetwork::getId, ip.getId()))
                        .eq(MemberPublicNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode()))
                     .set(MemberPublicNetwork::getEnable, NetworkStatusEnum.OCCUPIED.getCode())
               );
               if (updated) {
                  ip.setEnable(NetworkStatusEnum.OCCUPIED.getCode());
                  return ip;
               }

               log.warn("IP已被占用，正在进行第{}次重试", i + 1);
               Thread.sleep(50L);
            } catch (CommonException var9) {
               throw var9;
            } catch (Exception var10) {
               if (i == maxRetries - 1) {
                  throw new CommonException("分配IP失败: " + var10.getMessage(), new Object[0]);
               }

               log.warn("分配IP时发生异常，正在进行第{}次重试: {}", i + 1, var10.getMessage());

               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var8) {
                  Thread.currentThread().interrupt();
                  throw new CommonException("分配IP被中断", new Object[0]);
               }
            }
         }

         throw new CommonException("分配IP失败，已达到最大重试次数", new Object[0]);
      }
   }

   private Integer resolveIpRand(String lineId) {
      if (StrUtil.isBlank(lineId)) {
         log.warn("分配公网IP时线路ID为空，按顺序分配");
         return 0;
      } else {
         MemberLocalLine line = this.memberLocalLineService.queryEntity(lineId);
         if (line != null && line.getIpRand() != null) {
            return line.getIpRand();
         } else {
            log.warn("线路不存在或未配置IP分配规则，lineId={}，按顺序分配", lineId);
            return 0;
         }
      }
   }

   @Override
   public void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion) {
      boolean rows = this.update(
         (Wrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)new LambdaUpdateWrapper<MemberPublicNetwork>().eq(MemberPublicNetwork::getId, id))
                  .eq(MemberPublicNetwork::getVersion, currentVersion))
               .set(MemberPublicNetwork::getEnable, newStatus))
            .setSql("version = version + 1", new Object[0])
      );
      if (!rows) {
         throw new CommonException("IP已被占用，请重试", new Object[0]);
      }
   }

   @Override
   public void updateStatus(String id, int code) {
      MemberPublicNetwork memberPublicNetwork = this.queryEntity(id);
      memberPublicNetwork.setEnable(code);
      this.updateById(memberPublicNetwork);
   }

   @Override
   public void updateStatus(String ip) {
      this.update(
         (Wrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)new LambdaUpdateWrapper<MemberPublicNetwork>().eq(MemberPublicNetwork::getIp, ip))
            .set(MemberPublicNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode())
      );
   }

   @Override
   public void updateStatus(List<String> ipList) {
      this.update(
         (Wrapper<MemberPublicNetwork>)((LambdaUpdateWrapper<MemberPublicNetwork>)new LambdaUpdateWrapper<MemberPublicNetwork>().in(MemberPublicNetwork::getIp, ipList))
            .set(MemberPublicNetwork::getEnable, NetworkStatusEnum.UNOCCUPIED.getCode())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void rollbackStatus(String id) {
      MemberPublicNetwork network = this.queryEntity(id);
      network.setEnable(NetworkStatusEnum.UNOCCUPIED.getCode());
      this.updateById(network);
   }
}
