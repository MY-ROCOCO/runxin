package com.rxinyun.member.production.modular.ecs.line.service.impl;

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
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLineSku;
import com.rxinyun.member.production.modular.ecs.line.mapper.MemberLineSkuMapper;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLineSkuIdParam;
import com.rxinyun.member.production.modular.ecs.line.param.MemberLineSkuPageParam;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLineSkuService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberLineSkuServiceImpl extends ServiceImpl<MemberLineSkuMapper, MemberLineSku> implements MemberLineSkuService {
   @Resource
   private MemberLineSkuMapper memberLineSkuMapper;

   @Override
   public Page<MemberLineSku> page(MemberLineSkuPageParam pageParam) {
      QueryWrapper<MemberLineSku> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberLineSku::getName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         ((LambdaQueryWrapper<MemberLineSku>)queryWrapper.lambda().orderByAsc(MemberLineSku::getSortId)).orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<MemberLineSku>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public MemberLineSku detail(MemberLineSkuIdParam serverLineSkuIdParam) {
      return this.queryEntity(serverLineSkuIdParam.getId());
   }

   @Override
   public MemberLineSku queryEntity(String id) {
      MemberLineSku serverLineSku = (MemberLineSku)this.getById(id);
      if (ObjectUtil.isEmpty(serverLineSku)) {
         throw new CommonException("可用区域不存在，id值为：{}", new Object[]{id});
      } else {
         return serverLineSku;
      }
   }

   @Override
   public List<MemberLineSku> selectListByLineId(String lineId) {
      if (ObjectUtil.isEmpty(lineId)) {
         return new ArrayList<>();
      } else {
         LambdaQueryWrapper<MemberLineSku> queryWrapper = (LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)new QueryWrapper<MemberLineSku>()
                  .lambda()
                  .eq(MemberLineSku::getLineId, lineId))
               .orderByAsc(MemberLineSku::getSortId))
            .orderByDesc(CommonEntity::getCreateTime);
         return this.memberLineSkuMapper.selectList(queryWrapper);
      }
   }

   @Override
   public MemberLineSku selectByLineSku(String lineId, Integer cpu, Integer ram) {
      LambdaQueryWrapper<MemberLineSku> queryWrapper = (LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)new QueryWrapper<MemberLineSku>()
               .lambda()
               .eq(MemberLineSku::getLineId, lineId))
            .eq(MemberLineSku::getCpu, cpu))
         .eq(MemberLineSku::getRam, ram);
      return (MemberLineSku)this.memberLineSkuMapper.selectOne(queryWrapper);
   }

   @Override
   public List<MemberLineSku> queryByCpu(MemberLineSkuIdParam memberLineSkuIdParam) {
      LambdaQueryWrapper<MemberLineSku> queryWrapper = (LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)new QueryWrapper<MemberLineSku>()
                  .lambda()
                  .eq(MemberLineSku::getLineId, memberLineSkuIdParam.getLineId()))
               .eq(MemberLineSku::getCpu, memberLineSkuIdParam.getCpu()))
            .orderByAsc(MemberLineSku::getSortId))
         .orderByDesc(CommonEntity::getCreateTime);
      return this.memberLineSkuMapper.selectList(queryWrapper);
   }

   @Override
   public List<MemberLineSku> queryByRam(MemberLineSkuIdParam memberLineSkuIdParam) {
      LambdaQueryWrapper<MemberLineSku> queryWrapper = (LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)((LambdaQueryWrapper<MemberLineSku>)new QueryWrapper<MemberLineSku>()
                  .lambda()
                  .eq(MemberLineSku::getLineId, memberLineSkuIdParam.getLineId()))
               .eq(MemberLineSku::getRam, memberLineSkuIdParam.getRam()))
            .orderByAsc(MemberLineSku::getSortId))
         .orderByDesc(CommonEntity::getCreateTime);
      return this.memberLineSkuMapper.selectList(queryWrapper);
   }
}
