package com.rxinyun.member.business.modular.esc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;
import com.rxinyun.member.business.modular.esc.mapper.MemberServerIpMapper;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpEditParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpIdParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerIpPageParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerIpService;
import com.rxinyun.member.business.modular.esc.service.MemberServerService;
import com.rxinyun.production.api.MemberNatNetworkApi;
import com.rxinyun.production.api.MemberPrivateNetworkApi;
import com.rxinyun.production.api.MemberPublicNetworkApi;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServerIpServiceImpl extends ServiceImpl<MemberServerIpMapper, MemberServerIp> implements MemberServerIpService {
   private static final Logger log = LoggerFactory.getLogger(MemberServerIpServiceImpl.class);
   @Resource
   private MemberServerIpMapper memberServerIpMapper;
   @Resource
   private MemberPublicNetworkApi memberPublicNetworkApi;
   @Resource
   private MemberPrivateNetworkApi memberPrivateNetworkApi;
   @Resource
   private MemberNatNetworkApi memberNatNetworkApi;
   @Resource
   private MemberServerService memberServerService;

   @Override
   public Page<MemberServerIp> page(MemberServerIpPageParam pageParam) {
      QueryWrapper<MemberServerIp> queryWrapper = new QueryWrapper().checkSqlInjection();
      MemberServer memberServer = this.memberServerService.queryEntity(pageParam.getServerId());
      if (null == memberServer) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{pageParam.getServerId()});
      } else {
         if (ObjectUtil.isNotEmpty(pageParam.getType())) {
            queryWrapper.lambda().and(q -> q.eq(MemberServerIp::getType, pageParam.getType()));
         }

         if (ObjectUtil.isNotEmpty(pageParam.getServerId())) {
            queryWrapper.lambda().and(q -> q.eq(MemberServerIp::getServerId, pageParam.getServerId()));
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<MemberServerIp>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberServerIpAddParam serverIpAddParam) {
      this.assertServerNotLocked(serverIpAddParam.getServerId());
      MemberServerIp publicNetwork = (MemberServerIp)BeanUtil.toBean(serverIpAddParam, MemberServerIp.class);
      log.info("新增ip数据:{}", JSON.toJSONString(publicNetwork));
      this.save(publicNetwork);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(MemberServerIpEditParam serverIpEditParam) {
      MemberServerIp serverIp = this.queryEntity(serverIpEditParam.getId());
      this.assertServerNotLocked(serverIp.getServerId());
      BeanUtil.copyProperties(serverIpEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public MemberServerIp detail(MemberServerIpIdParam serverIpIdParam) {
      MemberServerIp serverIp = this.queryEntity(serverIpIdParam.getId());
      this.memberServerService.queryEntity(serverIp.getServerId());
      return serverIp;
   }

   @Override
   public MemberServerIp queryEntity(String id) {
      MemberServerIp serverIp = (MemberServerIp)this.getById(id);
      if (ObjectUtil.isEmpty(serverIp)) {
         throw new CommonException("服务器IP不存在，id值为：{}", new Object[]{id});
      } else {
         return serverIp;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteByServerId(String serverId) {
      log.info("删除ip数据，serverId:{}", serverId);
      List<MemberServerIp> memberServerIpList = this.queryByServerId(serverId);
      if (CollectionUtils.isEmpty(memberServerIpList)) {
         throw new CommonException("服务器IP不存在，serverId值为：{}", new Object[]{serverId});
      } else {
         for (MemberServerIp memberServerIp : memberServerIpList) {
            if (memberServerIp.getType().equals("PUBLIC")) {
               this.memberPublicNetworkApi.updateStatus(memberServerIp.getIp());
            } else if (memberServerIp.getType().equals("PRIVATE")) {
               this.memberPrivateNetworkApi.updateStatus(memberServerIp.getIp());
            } else {
               if (!memberServerIp.getType().equals("NAT")) {
                  throw new CommonException("服务器IP类型错误，serverId值为：{}", new Object[]{serverId});
               }

               this.memberNatNetworkApi.updateStatus(memberServerIp.getIp());
            }
         }

         this.memberServerIpMapper.deleteByServerId(serverId);
      }
   }

   @Override
   public List<MemberServerIp> queryByServerId(String serverId) {
      LambdaQueryWrapper<MemberServerIp> queryWrapper = (LambdaQueryWrapper<MemberServerIp>)new LambdaQueryWrapper<MemberServerIp>().eq(MemberServerIp::getServerId, serverId);
      return this.list(queryWrapper);
   }

   @Override
   public List<String> listServerIdByIp(String ip) {
      if (StrUtil.isBlank(ip)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<MemberServerIp> q = ((LambdaQueryWrapper<MemberServerIp>)new LambdaQueryWrapper<MemberServerIp>().like(MemberServerIp::getIp, ip))
            .select(MemberServerIp::getServerId);
         return this.list(q).stream().map(MemberServerIp::getServerId).distinct().collect(Collectors.toList());
      }
   }

   @Override
   public Map<String, Integer> countPublicIpByServerIds(List<String> serverIds) {
      if (CollectionUtils.isEmpty(serverIds)) {
         return new HashMap<>();
      } else {
         LambdaQueryWrapper<MemberServerIp> q = ((LambdaQueryWrapper<MemberServerIp>)((LambdaQueryWrapper<MemberServerIp>)new LambdaQueryWrapper<MemberServerIp>().in(MemberServerIp::getServerId, serverIds))
               .in(MemberServerIp::getType, new Object[]{"PUBLIC", "NAT"}))
            .select(MemberServerIp::getServerId);
         return this.list(q).stream().collect(Collectors.groupingBy(MemberServerIp::getServerId, Collectors.summingInt(serverIp -> 1)));
      }
   }

   private void assertServerNotLocked(String serverId) {
      if (!StrUtil.isBlank(serverId)) {
         MemberServer server = this.memberServerService.queryEntity(serverId);
         if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
            throw new CommonException("云服务器已锁定，请先解锁后再操作", new Object[0]);
         }
      }
   }
}
