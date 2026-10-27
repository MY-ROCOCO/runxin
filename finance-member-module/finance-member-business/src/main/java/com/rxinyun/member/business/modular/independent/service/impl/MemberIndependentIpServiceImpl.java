package com.rxinyun.member.business.modular.independent.service.impl;

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
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentIp;
import com.rxinyun.member.business.modular.independent.mapper.MemberIndependentIpMapper;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpAddParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpEditParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpIdParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIpPageParam;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentIpService;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberIndependentIpServiceImpl extends ServiceImpl<MemberIndependentIpMapper, MemberIndependentIp> implements MemberIndependentIpService {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentIpServiceImpl.class);
   @Resource
   private MemberIndependentIpMapper memberIndependentIpMapper;
   @Resource
   private MemberIndependentService memberIndependentService;

   @Override
   public Page<MemberIndependentIp> page(MemberIndependentIpPageParam pageParam) {
      QueryWrapper<MemberIndependentIp> queryWrapper = new QueryWrapper().checkSqlInjection();
      MemberIndependent memberIndependent = this.memberIndependentService.queryEntity(pageParam.getServerId());
      if (null == memberIndependent) {
         throw new CommonException("服务器不存在，serverId值为：{}", new Object[]{pageParam.getServerId()});
      } else {
         if (ObjectUtil.isNotEmpty(pageParam.getType())) {
            queryWrapper.lambda().and(q -> q.eq(MemberIndependentIp::getType, pageParam.getType()));
         }

         if (ObjectUtil.isNotEmpty(pageParam.getServerId())) {
            queryWrapper.lambda().and(q -> q.eq(MemberIndependentIp::getServerId, pageParam.getServerId()));
         }

         if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
            CommonSortOrderEnum.validate(pageParam.getSortOrder());
            queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         } else {
            queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
         }

         return (Page<MemberIndependentIp>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberIndependentIpAddParam serverIpAddParam) {
      MemberIndependentIp publicNetwork = (MemberIndependentIp)BeanUtil.toBean(serverIpAddParam, MemberIndependentIp.class);
      log.info("新增ip数据:{}", JSON.toJSONString(publicNetwork));
      this.save(publicNetwork);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void edit(MemberIndependentIpEditParam serverIpEditParam) {
      MemberIndependentIp serverIp = this.queryEntity(serverIpEditParam.getId());
      BeanUtil.copyProperties(serverIpEditParam, serverIp, new String[0]);
      this.updateById(serverIp);
   }

   @Override
   public MemberIndependentIp detail(MemberIndependentIpIdParam serverIpIdParam) {
      MemberIndependentIp serverIp = this.queryEntity(serverIpIdParam.getId());
      this.memberIndependentService.queryEntity(serverIp.getServerId());
      return serverIp;
   }

   @Override
   public MemberIndependentIp queryEntity(String id) {
      MemberIndependentIp serverIp = (MemberIndependentIp)this.getById(id);
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
      List<MemberIndependentIp> memberServerIpList = this.queryByServerId(serverId);
      if (CollectionUtils.isEmpty(memberServerIpList)) {
         throw new CommonException("服务器IP不存在，serverId值为：{}", new Object[]{serverId});
      } else {
         this.memberIndependentIpMapper.deleteByServerId(serverId);
      }
   }

   @Override
   public List<MemberIndependentIp> queryByServerId(String serverId) {
      LambdaQueryWrapper<MemberIndependentIp> queryWrapper = (LambdaQueryWrapper<MemberIndependentIp>)new LambdaQueryWrapper<MemberIndependentIp>()
         .eq(MemberIndependentIp::getServerId, serverId);
      return this.list(queryWrapper);
   }

   @Override
   public List<String> listServerIdByIp(String ip) {
      if (StrUtil.isBlank(ip)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<MemberIndependentIp> q = ((LambdaQueryWrapper<MemberIndependentIp>)new LambdaQueryWrapper<MemberIndependentIp>().like(MemberIndependentIp::getIp, ip))
            .select(MemberIndependentIp::getServerId);
         return this.list(q).stream().map(MemberIndependentIp::getServerId).distinct().collect(Collectors.toList());
      }
   }
}
