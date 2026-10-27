package com.rxinyun.member.production.modular.ecs.available.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.production.modular.ecs.available.entity.MemberAvailableRegion;
import com.rxinyun.member.production.modular.ecs.available.mapper.MemberAvailableRegionMapper;
import com.rxinyun.member.production.modular.ecs.available.param.MemberAvailableRegionIdParam;
import com.rxinyun.member.production.modular.ecs.available.param.MemberAvailableRegionPageParam;
import com.rxinyun.member.production.modular.ecs.available.response.MemberAvailableRegionResponse;
import com.rxinyun.member.production.modular.ecs.available.service.MemberAvailableRegionService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberAvailableRegionServiceImpl extends ServiceImpl<MemberAvailableRegionMapper, MemberAvailableRegion> implements MemberAvailableRegionService {
   @Resource
   private MemberAvailableRegionMapper memberAvailableRegionMapper;

   @Override
   public Page<MemberAvailableRegion> page(MemberAvailableRegionPageParam pageParam) {
      QueryWrapper<MemberAvailableRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberAvailableRegion::getStatus, 1);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberAvailableRegion::getAvailableName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberAvailableRegion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberAvailableRegion detail(MemberAvailableRegionIdParam serverAvailableRegionIdParam) {
      QueryWrapper<MemberAvailableRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      ((LambdaQueryWrapper<MemberAvailableRegion>)queryWrapper.lambda().eq(MemberAvailableRegion::getStatus, 1))
         .eq(MemberAvailableRegion::getId, serverAvailableRegionIdParam.getId());
      return (MemberAvailableRegion)this.getOne(queryWrapper);
   }

   @Override
   public MemberAvailableRegion queryEntity(String id) {
      MemberAvailableRegion serverAvailableRegion = (MemberAvailableRegion)this.getById(id);
      if (ObjectUtil.isEmpty(serverAvailableRegion)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverAvailableRegion;
      }
   }

   @Override
   public List<MemberAvailableRegionResponse> queryByRegionId(String regionId) {
      return this.memberAvailableRegionMapper.selectByRegionId(regionId);
   }
}
