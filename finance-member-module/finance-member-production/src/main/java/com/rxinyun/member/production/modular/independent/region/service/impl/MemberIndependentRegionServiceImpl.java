package com.rxinyun.member.production.modular.independent.region.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.region.mapper.MemberIndependentRegionMapper;
import com.rxinyun.member.production.modular.independent.region.model.entity.Region;
import com.rxinyun.member.production.modular.independent.region.service.IMemberRegionService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberIndependentRegionServiceImpl extends ServiceImpl<MemberIndependentRegionMapper, Region> implements IMemberRegionService {
   @Resource
   private MemberIndependentRegionMapper memberIndependentRegionMapper;

   @Override
   public CommonResult<List<Region>> purchase() {
      return CommonResult.data(
         ((this.lambdaQuery().eq(Region::getStatus, 1)).orderByAsc(Region::getSort)).list()
      );
   }

   @Override
   public Region searchRegionById(String id) {
      return (Region)this.memberIndependentRegionMapper.selectById(id);
   }
}
