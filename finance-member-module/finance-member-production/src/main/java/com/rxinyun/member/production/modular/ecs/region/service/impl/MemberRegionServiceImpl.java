package com.rxinyun.member.production.modular.ecs.region.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.production.modular.ecs.region.entity.MemberRegion;
import com.rxinyun.member.production.modular.ecs.region.mapper.MemberRegionMapper;
import com.rxinyun.member.production.modular.ecs.region.param.MemberRegionIdParam;
import com.rxinyun.member.production.modular.ecs.region.param.MemberRegionPageParam;
import com.rxinyun.member.production.modular.ecs.region.service.MemberRegionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberRegionServiceImpl extends ServiceImpl<MemberRegionMapper, MemberRegion> implements MemberRegionService {
   @Resource
   private MemberRegionMapper memberRegionMapper;

   @Override
   public Page<MemberRegion> page(MemberRegionPageParam pageParam) {
      QueryWrapper<MemberRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberRegion::getStatus, 1);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberRegion::getRegionName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByAsc(MemberRegion::getSortId);
      }

      return (Page<MemberRegion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberRegion detail(MemberRegionIdParam serverRegionIdParam) {
      QueryWrapper<MemberRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberRegion::getId, serverRegionIdParam.getId());
      queryWrapper.lambda().eq(MemberRegion::getStatus, 1);
      return (MemberRegion)this.getOne(queryWrapper);
   }

   @Override
   public MemberRegion queryEntity(String id) {
      MemberRegion serverRegion = (MemberRegion)this.getById(id);
      if (ObjectUtil.isEmpty(serverRegion)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverRegion;
      }
   }
}
