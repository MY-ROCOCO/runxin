package com.rxinyun.member.production.modular.vhost.region.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.production.modular.vhost.region.entity.MemberVhostRegion;
import com.rxinyun.member.production.modular.vhost.region.mapper.MemberVhostRegionMapper;
import com.rxinyun.member.production.modular.vhost.region.param.MemberVhostRegionPageParam;
import com.rxinyun.member.production.modular.vhost.region.service.MemberVhostRegionService;
import org.springframework.stereotype.Service;

@Service
public class MemberVhostRegionServiceImpl extends ServiceImpl<MemberVhostRegionMapper, MemberVhostRegion> implements MemberVhostRegionService {
   @Override
   public Page<MemberVhostRegion> page(MemberVhostRegionPageParam pageParam) {
      QueryWrapper<MemberVhostRegion> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberVhostRegion::getStatus, 1);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().like(MemberVhostRegion::getName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         ((LambdaQueryWrapper<MemberVhostRegion>)queryWrapper.lambda().orderByAsc(MemberVhostRegion::getSortId)).orderByDesc(MemberVhostRegion::getId);
      }

      return (Page<MemberVhostRegion>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }
}
