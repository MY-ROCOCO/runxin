package com.rxinyun.member.production.modular.independent.cloud.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.production.modular.independent.cloud.entity.MemberLine;
import com.rxinyun.member.production.modular.independent.cloud.mapper.MemberLineMapper;
import com.rxinyun.member.production.modular.independent.cloud.param.MemberLineIdParam;
import com.rxinyun.member.production.modular.independent.cloud.param.MemberLinePageParam;
import com.rxinyun.member.production.modular.independent.cloud.service.MemberLineService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberLineServiceImpl extends ServiceImpl<MemberLineMapper, MemberLine> implements MemberLineService {
   @Resource
   private MemberLineMapper memberLineMapper;

   @Override
   public Page<MemberLine> page(MemberLinePageParam independentLinePageParam) {
      QueryWrapper<MemberLine> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberLine::getStatus, 1);
      if (ObjectUtil.isAllNotEmpty(new Object[]{independentLinePageParam.getSortField(), independentLinePageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(independentLinePageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            independentLinePageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(independentLinePageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(MemberLine::getId);
      }

      return (Page<MemberLine>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public List<MemberLine> getLineName() {
      return this.memberLineMapper.selectList(new QueryWrapper<MemberLine>().lambda().select(MemberLine::getId, MemberLine::getName));
   }

   @Override
   public MemberLine detail(MemberLineIdParam memberLineIdParam) {
      QueryWrapper<MemberLine> queryWrapper = new QueryWrapper();
      ((LambdaQueryWrapper<MemberLine>)queryWrapper.lambda().eq(MemberLine::getId, memberLineIdParam.getId())).eq(MemberLine::getStatus, 1);
      MemberLine independentLine = (MemberLine)this.getOne(queryWrapper);
      if (ObjectUtil.isEmpty(independentLine)) {
         throw new CommonException("可用线路不存在，id值为:{}", new Object[]{memberLineIdParam.getId()});
      } else {
         return independentLine;
      }
   }

   @Override
   public List<MemberLine> findLine(String regionId) {
      return this.memberLineMapper
         .selectList(
            (Wrapper<MemberLine>)((LambdaQueryWrapper<MemberLine>)((LambdaQueryWrapper<MemberLine>)new QueryWrapper<MemberLine>().lambda().eq(MemberLine::getRegionId, regionId)).eq(MemberLine::getStatus, 1))
               .orderByAsc(MemberLine::getWeight)
         );
   }

   @Override
   public MemberLine queryEntity(String id) {
      return (MemberLine)this.getById(id);
   }
}
